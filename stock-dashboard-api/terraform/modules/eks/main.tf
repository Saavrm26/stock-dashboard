module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 21.0"

  name               = var.cluster_name
  create_iam_role    = true
  kubernetes_version = "1.34"

  endpoint_public_access                   = true
  enable_cluster_creator_admin_permissions = true

  vpc_id     = var.vpc_id
  subnet_ids = var.private_subnets
}

resource "aws_eks_addon" "vpc_cni" {
  cluster_name             = module.eks.cluster_name
  addon_name               = "vpc-cni"
  service_account_role_arn = aws_iam_role.vpc_cni.arn
}

resource "aws_eks_addon" "coredns" {
  cluster_name                = module.eks.cluster_name
  addon_name                  = "coredns"
  resolve_conflicts_on_update = "OVERWRITE"
  resolve_conflicts_on_create = "OVERWRITE"
}

resource "aws_eks_addon" "kube_proxy" {
  cluster_name                = module.eks.cluster_name
  addon_name                  = "kube-proxy"
  resolve_conflicts_on_create = "OVERWRITE"
  resolve_conflicts_on_update = "OVERWRITE"
}

# This is to allow ALB to send traffic to nodes and consequently to pods
resource "aws_security_group" "aws_alb_shared_backend_sg" {
  name        = "k8s-traffic-${module.eks.cluster_name}"
  description = "A shared security group for ALB to send traffic to nodes and consequently to pods"
  vpc_id      = var.vpc_id

  # Ingress rules (inbound traffic)
  ingress {
    description      = "HTTP from anywhere"
    from_port        = 80
    to_port          = 80
    protocol         = "tcp"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  ingress {
    description = "HTTPS from anywhere"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Egress rules (outbound traffic)
  egress {
    description      = "Allow all outbound"
    from_port        = 0
    to_port          = 0
    protocol         = "-1"  # -1 means all protocols
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  tags = {
    "elbv2.k8s.aws/cluster" = module.eks.cluster_name
    "elbv2.k8s.aws/resource" = "backend-sg"
    Terraform   = "true"
    Environment = "dev"
  }
}

resource "aws_security_group" "aws_allow_traffic_from_alb" {
  name        = "mng-allow-traffic-from-alb"
  description = "A shared security group for nodes to receive traffic from ALB and consequently forward to pods"
  vpc_id      = var.vpc_id

  ingress {
    description     = "Allow from ALB"
    from_port       = 0
    to_port         = 0
    protocol        = "-1"
    security_groups = [aws_security_group.aws_alb_shared_backend_sg.id]
  }
}

module "spot_eks_managed_node_group" {
  source                            = "terraform-aws-modules/eks/aws//modules/eks-managed-node-group"
  instance_types                    = var.node_instance_types
  ami_type                          = "AL2023_x86_64_STANDARD"
  name                              = "spot"
  cluster_name                      = module.eks.cluster_name
  subnet_ids                        = var.private_subnets
  cluster_primary_security_group_id = module.eks.cluster_primary_security_group_id
  cluster_service_cidr              = module.eks.cluster_service_cidr
  vpc_security_group_ids            = [module.eks.node_security_group_id, aws_security_group.aws_allow_traffic_from_alb.id]

  min_size        = var.min_size
  max_size        = var.max_size
  desired_size    = var.initial_desired_size
  capacity_type   = "SPOT"
  create_iam_role = true

  # Disable CNI policy on node role since we use IRSA for VPC CNI
  iam_role_attach_cni_policy = false
  iam_role_name              = "eksNodeRole_spot_${var.cluster_name}"

  depends_on = [aws_eks_addon.vpc_cni]
}
