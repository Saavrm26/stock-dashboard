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

  azs             = local.azs
  public_subnets   = [for k, v in local.azs : cidrsubnet(local.vpc_cidr, 8, k)]
  private_subnets  = [for k, v in local.azs : cidrsubnet(local.vpc_cidr, 8, k + 3)]
  database_subnets = [for k, v in local.azs : cidrsubnet(local.vpc_cidr, 8, k + 6)]

  enable_nat_gateway = true
  single_nat_gateway = true
  one_nat_gateway_per_az = false

  tags = {
    Terraform = "true"
    Environment = var.env
  }
}