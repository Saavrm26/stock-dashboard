# external-dns -> for managing Route53 records
resource "helm_release" "external_dns" {
  name       = "external-dns"
  repository = "https://kubernetes-sigs.github.io/external-dns/"
  chart      = "external-dns"
  namespace  = "kube-system"

  set = [
    {
      name  = "serviceAccount.annotations.eks\\.amazonaws\\.com/role-arn"
      value = aws_iam_role.external_dns.arn
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

  depends_on = [module.eks, module.spot_eks_managed_node_group]
}

# todo: add load balancer controller

resource "helm_release" "aws_load_balancer_controller" {
  repository = "https://aws.github.io/eks-charts"
  chart      = "aws-load-balancer-controller"
  name       = "aws-load-balancer-controller"
  namespace  = "kube-system"

  set = [
    {
      name  = "clusterName"
      value = module.eks.cluster_name
    },
    {
      name  = "serviceAccount.create"
      value = "true"
    },
    {
      name  = "serviceAccount.name"
      value = "aws-load-balancer-controller"
    },
    {
      name  = "serviceAccount.annotations.eks\\.amazonaws\\.com/role-arn"
      value = aws_iam_role.aws_load_balancer_controller.arn
    },
    {
      name  = "vpcId"
      value = var.vpc_id
    }
  ]

  depends_on = [module.eks, module.spot_eks_managed_node_group]
}

# todo: add cluster-autoscaler
