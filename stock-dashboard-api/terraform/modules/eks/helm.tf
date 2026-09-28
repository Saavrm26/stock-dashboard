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
    },
    {
      name = "backendSecurityGroup",
      value = aws_security_group.aws_alb_shared_backend_sg.id
    }
  ]

  depends_on = [module.eks, module.spot_eks_managed_node_group]
}
