data "aws_rds_engine_version" "postgresql" {
  engine  = "aurora-postgresql"
  version = "16.11"
}

resource "aws_security_group" "db_bastion_access" {
  name        = "${var.cluster_name}-db-bastion-access-sg"
  description = "Attach to a bastion host to allow connecting to the Aurora PostgreSQL cluster"
  vpc_id      = var.vpc_id

  ingress {
    description      = "SSH from internet"
    from_port        = 22
    to_port          = 22
    protocol         = "tcp"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  egress {
    description      = "Allow all outbound"
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  tags = {
    Name        = "${var.cluster_name}-db-bastion-access-sg"
    Terraform   = "true"
    Environment = var.env
  }
}

module "aurora_db" {
  source = "terraform-aws-modules/rds-aurora/aws"

  name              = var.cluster_name
  engine            = data.aws_rds_engine_version.postgresql.engine
  engine_mode       = "provisioned"
  storage_encrypted = true
  master_username   = "root"
  apply_immediately = true
  database_name     = var.db_name
  skip_final_snapshot = true
  vpc_id               = var.vpc_id
  db_subnet_group_name = var.database_subnet_group_name

  security_group_ingress_rules = {
    private-az1 = {
      cidr_ipv4 = var.private_subnets_cidr_blocks[0]
    }
    private-az2 = {
      cidr_ipv4 = var.private_subnets_cidr_blocks[1]
    }
    private-az3 = {
      cidr_ipv4 = var.private_subnets_cidr_blocks[2]
    }
    bastion = {
      referenced_security_group_id = aws_security_group.db_bastion_access.id
      from_port                    = 5432
      to_port                      = 5432
      ip_protocol                  = "tcp"
      description                  = "Allow PostgreSQL from bastion access SG"
    }
  }

  serverlessv2_scaling_configuration = {
    min_capacity             = 0
    max_capacity             = 2
    seconds_until_auto_pause = 300
  }
  cluster_instance_class = "db.serverless"
  cluster_timeouts = {
    delete = "30m"
  }

  instances = {
    one = {}
  }
  instance_timeouts = {
    delete = "30m"
  }
  tags = {
    Terraform   = "true"
    Environment = var.env
  }
}
