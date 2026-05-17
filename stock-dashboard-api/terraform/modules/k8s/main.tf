data "aws_secretsmanager_secret" "aurora_db_secret" {
  arn = var.aurora_db_secret_arn
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
    aurora_db_secret_name = data.aws_secretsmanager_secret.aurora_db_secret.name
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

resource "kubernetes_storage_class_v1" "gp3" {
  metadata {
    name = "gp3"
  }

  storage_provisioner = "ebs.csi.aws.com"

  parameters = {
    type    = "gp3"
    fsType  = "ext4"
  }

  reclaim_policy         = "Delete"
  volume_binding_mode    = "WaitForFirstConsumer"
  allow_volume_expansion = true
}
