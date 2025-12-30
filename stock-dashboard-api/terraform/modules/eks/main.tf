module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 21.0"

  name               = var.cluster_name
  create_iam_role    = true
  kubernetes_version = "1.34"

  endpoint_public_access                   = true
  enable_cluster_creator_admin_permissions = true

  # eks_managed_node_groups = {
  #   spot = {
  #     instance_types = var.node_instance_types
  #     ami_type       = "AL2023_x86_64_STANDARD"
  #     name           = "spot-${replace(var.node_instance_types[0], ".", "-")}"
  #
  #     min_size        = var.min_size
  #     max_size        = var.max_size
  #     desired_size    = var.initial_desired_size
  #     capacity_type   = "SPOT"
  #     create_iam_role = true
  #
  #     # Disable CNI policy on node role since we use IRSA for VPC CNI
  #     iam_role_attach_cni_policy = false
  #     iam_role_name              = "eksNodeRole-spot-${var.cluster_name}"
  #   }
  # }
  vpc_id     = var.vpc_id
  subnet_ids = var.private_subnets
}

resource "aws_eks_addon" "vpc_cni" {
  cluster_name             = module.eks.cluster_name
  addon_name               = "vpc-cni"
  service_account_role_arn = aws_iam_role.vpc_cni.arn
}

resource "aws_eks_addon" "coredns" {
  cluster_name                = module.eks.cluster_name
  addon_name                  = "coredns"
  resolve_conflicts_on_update = "OVERWRITE"
  resolve_conflicts_on_create = "OVERWRITE"
}

resource "aws_eks_addon" "kube_proxy" {
  cluster_name                = module.eks.cluster_name
  addon_name                  = "kube-proxy"
  resolve_conflicts_on_create = "OVERWRITE"
  resolve_conflicts_on_update = "OVERWRITE"
}

module "spot_eks_managed_node_group" {
  source                            = "terraform-aws-modules/eks/aws//modules/eks-managed-node-group"
  instance_types                    = var.node_instance_types
  ami_type                          = "AL2023_x86_64_STANDARD"
  name                              = "spot"
  cluster_name                      = module.eks.cluster_name
  subnet_ids                        = var.private_subnets
  cluster_primary_security_group_id = module.eks.cluster_primary_security_group_id
  cluster_service_cidr              = module.eks.cluster_service_cidr
  vpc_security_group_ids            = [module.eks.node_security_group_id]

  min_size        = var.min_size
  max_size        = var.max_size
  desired_size    = var.initial_desired_size
  capacity_type   = "SPOT"
  create_iam_role = true

  # Disable CNI policy on node role since we use IRSA for VPC CNI
  iam_role_attach_cni_policy = false
  iam_role_name              = "eksNodeRole_spot_${var.cluster_name}"

  depends_on = [aws_eks_addon.vpc_cni]
}
