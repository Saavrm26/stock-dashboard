variable "db_url" {
  type = string # The type of the variable, in this case a string
}

variable "db_user" {
  type = string
}

variable "aurora_db_secret_arn" {
  type = string
}

variable "custom_app_policy_arn" {
  type    = string
  default = null
}

variable "app_sa_name" {
  type    = string
  default = "app-sa"
}
variable "eks_oidc_provider" {
  type = string
}
variable "eks_oidc_provider_arn" {
  type = string
}

variable "cluster_name" {
  type = string
}

variable "karpenter_node_role" {
  type = string
}

variable "karpenter_ami_version" {
  type    = string
  default = "v20260917"
}
