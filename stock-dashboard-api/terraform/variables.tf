
variable "env" {
  description = "The environment of the VPC"
  type        = string
  default     = "dev"
}

variable "name" {
  description = "Name of the application"
  type = string
}

variable "node_instance_types" {
  description = "Instance types for the node group"
  type = list(string)
}

variable "use_spot_node_group" {
  type = bool
  description = "Use spot instances for the node group"
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

variable "create_db" {
  description = "Whether to create the database module"
  type        = bool
  default     = true
}

