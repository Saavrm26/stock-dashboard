output "aurora_db_cluster_id" {
  value       = module.aurora_db.cluster_id
  description = "The public IP address of the EC2 instance"
}

output "aurora_db_secret_name" {

  value = element(split(":", module.aurora_db.cluster_master_user_secret[0].secret_arn), -1)
}

output "bastion_sg_id" {
  value = aws_security_group.db_bastion_access.id
}

output "cluster_url" {
  value = module.aurora_db.cluster_endpoint
}

output "database_user" {
  value = module.aurora_db.cluster_master_username
}

output "database_name" {
  value = module.aurora_db.cluster_database_name
}