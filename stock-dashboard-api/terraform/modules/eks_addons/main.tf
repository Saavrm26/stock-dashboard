terraform {
  required_providers {
    helm = {
      source  = "hashicorp/helm"
      version = ">= 2.0"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = ">= 2.0"
    }
  }
  required_version = ">= 1.2"
}

# karpenter helm
resource "helm_release" "karpenter" {
  name             = "karpenter"
  repository       = "oci://public.ecr.aws/karpenter"
  chart            = "karpenter"
  version          = "1.14.1"
  namespace        = "kube-system"
  create_namespace = true
  cleanup_on_fail = true
  atomic = true

  set = [
    {
      name  = "settings.clusterName"
      value = var.cluster_name
    },
    {
      name  = "settings.interruptionQueue"
      value = "Karpenter-${var.cluster_name}"
    },
    {
      name  = "settings.enableZonalShift"
      value = "false"
    },
    {
      name  = "controller.resources.requests.cpu"
      value = "1"
    },
    {
      name  = "controller.resources.requests.memory"
      value = "1Gi"
    },
    {
      name  = "controller.resources.limits.cpu"
      value = "1"
    },
    {
      name  = "controller.resources.limits.memory"
      value = "1Gi"
    }
  ]
}

# external dns
resource "helm_release" "external_dns" {
  name       = "external-dns"
  repository = "https://kubernetes-sigs.github.io/external-dns/"
  chart      = "external-dns"
  namespace  = "kube-system"
  version = "1.20.0"
  timeout = 120
  set = [
    {
      name  = "serviceAccount.annotations.eks\\.amazonaws\\.com/role-arn"
      value = var.external_dns_role_arn
    },
    {
      name  = "serviceAccount.name"
      value = "external-dns"
    },
    {
      name  = "provider.name"
      value = "aws"
    },
    {
      name  = "policy"
      value = "sync"
    }
  ]
}
