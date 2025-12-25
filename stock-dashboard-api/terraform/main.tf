provider "aws" {
  region = "ap-south-1"
}

module "stock_dashboard_vpc" {
  source = "./modules/vpc"

  vpc_name = var.vpc_name
  env = var.env
}

module "stock_dashboard_db" {
  source = "./modules/db"

  db_name = var.db_name
  cluster_name = var.cluster_name
  vpc_id = module.stock_dashboard_vpc.vpc_id
  private_subnets_cidr_blocks = module.stock_dashboard_vpc.private_subnets_cidr_blocks
  database_subnet_group_name = module.stock_dashboard_vpc.database_subnet_group_name
}