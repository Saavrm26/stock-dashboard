resource "kubernetes_config_map_v1" "infra_config" {
  metadata {
    name = "infra-config"
    labels = {
      app = "stock-dashboard"
    }
  }

  data = {
    db_url  = var.db_url
    db_user = var.db_user
  }
}
