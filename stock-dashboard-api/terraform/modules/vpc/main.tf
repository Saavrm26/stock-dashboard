data "aws_availability_zones" "available" {
  state = "available"
}

locals {
  vpc_cidr = "10.0.0.0/16"
  azs      = slice(data.aws_availability_zones.available.names, 0, 3)
}

module "vpc" {
  source = "terraform-aws-modules/vpc/aws"

  name = var.vpc_name
  cidr = var.cidr_block

  azs              = local.azs
  public_subnets   = [for k, v in local.azs : cidrsubnet(local.vpc_cidr, 8, k)]
  private_subnets  = [for k, v in local.azs : cidrsubnet(local.vpc_cidr, 8, k + 3)]
  database_subnets = [for k, v in local.azs : cidrsubnet(local.vpc_cidr, 8, k + 6)]

  enable_nat_gateway     = true
  single_nat_gateway     = true
  one_nat_gateway_per_az = false

  tags = {
    Terraform   = "true"
    Environment = var.env
  }

  # Required for elastic load balancer to work - for Ingress and LoadBalancer service resources
  public_subnet_tags = {
    "kubernetes.io/role/elb" = 1
    # adding here for consistency
    Terraform   = "true"
    Environment = var.env
  }

  private_subnet_tags = {
    "kubernetes.io/role/internal-elb" = 1
    Terraform   = "true"
    Environment = var.env
  }

  database_subnet_tags = {
    Terraform   = "true"
    Environment = var.env
  }
}