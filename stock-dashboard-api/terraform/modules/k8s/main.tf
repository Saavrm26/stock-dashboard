locals {
  aurora_db_secret_name = element(split(":", var.aurora_db_secret_arn), -1)
}

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
    aurora_db_secret_name = local.aurora_db_secret_name
  }
}

resource "kubernetes_service_account_v1" "app_sa" {
  metadata {
    name = var.app_sa_name
    annotations = {
      "eks.amazonaws.com/role-arn": aws_iam_role.app_role.arn
    }
  }
}