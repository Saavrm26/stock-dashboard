variable "vpc_name" {
  description = "The name of the VPC"
  type        = string
}

variable "cidr_block" {
  description = "The CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "env" {
  description = "The environment of the VPC"
  type        = string
  default     = "dev"
}