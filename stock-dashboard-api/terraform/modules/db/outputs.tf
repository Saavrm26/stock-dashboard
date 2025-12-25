output "aurora_db_cluster_id" {
  value       = module.aurora_db.cluster_id
  description = "The public IP address of the EC2 instance"
}

output "aurora_db_secret_name" {
  value = module.aurora_db.cluster_master_password
}