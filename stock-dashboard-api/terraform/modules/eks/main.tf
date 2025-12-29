module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 21.0"

  name = var.cluster_name
  create_iam_role = true
  kubernetes_version = "1.34"


  addons = {
    coredns    = {}
    kube-proxy = {}
  }

  eks_managed_node_groups = {
    spot = {
      instance_types = var.node_instance_types
      ami_type       = "AL2023_x86_64_STANDARD"
      name = "spot_${var.node_instance_types[0]}"

      min_size = var.min_size
      max_size = var.max_size
      desired_size = var.initial_desired_size
      capacity_type  = "SPOT"
      create_iam_role = true

      # Disable CNI policy on node role since we use IRSA for VPC CNI
      iam_role_attach_cni_policy = false
      iam_role_name = "eksNodeRole_spot_${var.cluster_name}"
    }
  }
  vpc_id     = var.vpc_id
  subnet_ids = var.private_subnets
}

resource "aws_eks_addon" "vpc_cni" {
  cluster_name             = module.eks.cluster_name
  addon_name               = "vpc-cni"
  service_account_role_arn = aws_iam_role.vpc_cni.arn
}