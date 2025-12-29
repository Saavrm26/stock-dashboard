variable "cluster_name" {
  type        = string
}

variable "node_instance_types" {
  type = list(string)
}

variable "min_size" {
  description = "Minimum size of the node group"
  type = number
}

variable "max_size" {
  description = "Minimum size of the node group"
  type = number
}

variable "initial_desired_size" {
  description = "Minimum size of the node group"
  type = number
}

variable "vpc_id" {
  type = string
}

variable "private_subnets" {
  type = list(string)
}