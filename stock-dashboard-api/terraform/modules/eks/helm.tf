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
  chart = ""
  name  = ""
}

# todo: add cluster-autoscaler