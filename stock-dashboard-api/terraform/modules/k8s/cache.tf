# Redis ConfigMap with master and replica configurations
resource "kubernetes_config_map_v1" "redis" {
  metadata {
    name = "redis"
    labels = {
      app = "redis"
    }
  }

  data = {
    "master.conf"  = file("${path.module}/master.conf")
    "replica.conf" = file("${path.module}/replica.conf")
  }
}

# Redis Headless Service
resource "kubernetes_service_v1" "redis_headless" {
  metadata {
    name = "redis-headless"
    labels = {
      app = "redis"
    }
  }

  spec {
    type = "ClusterIP"
    cluster_ip = "None"

    port {
      name        = "redis"
      port        = 6379
      target_port = "redis"
    }

    selector = {
      app                              = "redis"
      "statefulset.kubernetes.io/pod-name" = "redis-0"
    }
  }
}

# Redis StatefulSet
resource "kubernetes_stateful_set_v1" "redis" {
  metadata {
    name = "redis"
    labels = {
      app = "redis"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "redis"
      }
    }

    service_name = "redis-headless"

    template {
      metadata {
        labels = {
          app = "redis"
        }
      }

      spec {
        security_context {
          fs_group = 999
        }

        container {
          name  = "redis"
          image = "docker.io/library/redis:8.6-trixie"

          port {
            container_port = 6379
            name           = "redis"
          }

          volume_mount {
            name       = "redis-data"
            mount_path = "/data"
          }
        }
      }
    }

    volume_claim_template {
      metadata {
        name = "redis-data"
      }

      spec {
        access_modes = ["ReadWriteOnce"]
        storage_class_name = "gp3"
        resources {
          requests = {
            storage = "1Gi"
          }
        }
      }
    }
  }

  depends_on = [ kubernetes_storage_class_v1.gp3 ]
}