output "cluster_url" {
  value = try(module.stock_dashboard_db[0].cluster_url, null)
}

output "database_user" {
  value = try(module.stock_dashboard_db[0].database_user, null)
  sensitive = true
}

output "database_name" {
  value = try(module.stock_dashboard_db[0].database_name, null)
}
