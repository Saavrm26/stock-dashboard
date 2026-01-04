provider "aws" {
  region = "ap-south-1"
}

provider "helm" {
  kubernetes = {
    host                   = module.stock_dashboard_eks.cluster_endpoint
    cluster_ca_certificate = base64decode(module.stock_dashboard_eks.cluster_certificate_authority_data)
    exec = {
      api_version = "client.authentication.k8s.io/v1beta1"
      args        = ["eks", "get-token", "--cluster-name", module.stock_dashboard_eks.cluster_name]
      command     = "aws"
    }
  }
}

provider "kubernetes" {
  host                   = module.stock_dashboard_eks.cluster_endpoint
  cluster_ca_certificate = base64decode(module.stock_dashboard_eks.cluster_certificate_authority_data)
  exec {
    api_version = "client.authentication.k8s.io/v1beta1"
    args        = ["eks", "get-token", "--cluster-name", module.stock_dashboard_eks.cluster_endpoint]
    command     = "aws"
  }
}

module "stock_dashboard_vpc" {
  source = "./modules/vpc"

  vpc_name = "${var.name}_${var.env}"
  env      = var.env
}

module "stock_dashboard_db" {
  source = "./modules/db"
  count  = var.create_db ? 1 : 0

  db_name                     = "${var.name}-${var.env}"
  cluster_name                = "${var.name}-${var.env}"
  vpc_id                      = module.stock_dashboard_vpc.vpc_id
  private_subnets_cidr_blocks = module.stock_dashboard_vpc.private_subnets_cidr_blocks
  database_subnet_group_name  = module.stock_dashboard_vpc.database_subnet_group_name
}

module "stock_dashboard_eks" {
  source = "./modules/eks"

  cluster_name         = "${var.name}-${var.env}"
  node_instance_types  = var.node_instance_types
  min_size             = var.min_size
  max_size             = var.max_size
  initial_desired_size = var.initial_desired_size
  vpc_id               = module.stock_dashboard_vpc.vpc_id
  private_subnets      = module.stock_dashboard_vpc.private_subnets
}

module "stock_dashboard_k8s" {
  source = "./modules/k8s"
  count = var.create_k8s ? 1 : 0
  db_url = "${module.stock_dashboard_db[0].cluster_url}/${module.stock_dashboard_db[0].database_name}"
  db_user = module.stock_dashboard_db[0].database_user

  # just to be safe, theoretically it is not required as the k8s provider depends on eks init
  depends_on = [module.stock_dashboard_eks]
}