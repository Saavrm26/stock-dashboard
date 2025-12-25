data "aws_rds_engine_version" "postgresql" {
  engine  = "aurora-postgresql"
  version = "16.11"
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
