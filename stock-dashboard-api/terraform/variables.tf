variable "vpc_name" {
  description = "The name of the VPC"
  type        = string
}

variable "env" {
  description = "The environment of the VPC"
  type        = string
  default     = "dev"
}

variable "db_name" {
  description = "The name of the DB"
  type        = string
}

variable "cluster_name" {
  description = "The name of the cluster"
  type        = string
}
