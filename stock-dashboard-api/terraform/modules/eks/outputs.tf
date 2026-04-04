output "cluster_name" {
  description = "EKS cluster name"
  value       = module.eks.cluster_name
}

output "cluster_endpoint" {
  description = "EKS cluster endpoint"
  value       = module.eks.cluster_endpoint
}

output "cluster_certificate_authority_data" {
  description = "Base64 encoded certificate data for the cluster"
  value       = module.eks.cluster_certificate_authority_data
}

output "external_dns_role_arn" {
  description = "IAM role ARN for External DNS"
  value       = aws_iam_role.external_dns.arn
}

output "eks_oidc_provider" {
  description = "EKS OIDC provider"
  value = module.eks.oidc_provider
}

output "eks_oidc_provider_arn" {
  description = "EKS OIDC provider ARN"
  value = module.eks.oidc_provider_arn
}
