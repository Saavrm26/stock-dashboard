variable "db_name" {
  type        = string
  description = "name of the db"
}

variable "cluster_name" {
  type        = string
  description = "name of the cluster"
}

variable "vpc_id" {
  type        = string
  description = "vpc id"
}

variable "env" {
  description = "The environment of the VPC"
  type        = string
  default     = "dev"
}

variable "private_subnets_cidr_blocks" {
  type        = list(string)
  description = "The private subnets of the VPC"
}

variable "database_subnet_group_name" {
  type        = string
  description = "The database subnet group name"
}