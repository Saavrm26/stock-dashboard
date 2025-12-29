provider "aws" {
  region = "ap-south-1"
}

module "stock_dashboard_vpc" {
  source = "./modules/vpc"

  vpc_name = "${var.name}_${var.env}"
  env      = var.env
}

module "stock_dashboard_db" {
  source = "./modules/db"

  db_name                     = "${var.name}_${var.env}"
  cluster_name                = "${var.name}_${var.env}"
  vpc_id                      = module.stock_dashboard_vpc.vpc_id
  private_subnets_cidr_blocks = module.stock_dashboard_vpc.private_subnets_cidr_blocks
  database_subnet_group_name  = module.stock_dashboard_vpc.database_subnet_group_name
}

module "stock_dashboard_eks" {
  source = "./modules/eks"

  cluster_name = "${var.name}_${var.env}"
  node_instance_types = var.node_instance_types
  min_size = var.min_size
  max_size = var.max_size
  initial_desired_size = var.initial_desired_size
  vpc_id = module.stock_dashboard_vpc.vpc_id
  private_subnets = module.stock_dashboard_vpc.private_subnets
}
