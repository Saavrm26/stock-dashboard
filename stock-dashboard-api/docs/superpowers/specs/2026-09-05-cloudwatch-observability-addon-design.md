# CloudWatch Observability Addon Design

## Goal

Enable CloudWatch container logs and observability for every EKS cluster created by the Terraform EKS module.

## Architecture

Add an IRSA role and attach it to the AWS-managed `amazon-cloudwatch-observability` EKS addon:

- Create `aws_iam_role.cloudwatch_agent` in `terraform/modules/eks/roles.tf`.
- Trust the module's EKS OIDC provider for only the service account `cloudwatch-agent` in namespace `amazon-cloudwatch`.
- Attach `arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy` to the role.
- Create `aws_eks_addon.cloudwatch_observability` in `terraform/modules/eks/main.tf`.
- Set the addon service account role ARN to `aws_iam_role.cloudwatch_agent.arn`.
- Configure create and update conflict handling as `OVERWRITE`, consistent with the existing managed addons.

The role name will include the cluster name as `CloudWatchAgentRole_${var.cluster_name}` to avoid collisions between clusters while preserving the naming convention used by the existing IRSA roles.

## Scope

The addon is unconditional within the EKS module. No new variable is required because every cluster using this module should receive CloudWatch observability.

## Validation

- Run `terraform fmt -check` against the Terraform configuration.
- Run `terraform validate` from the Terraform root.
- Inspect the resulting diff to confirm only the EKS role, policy attachment, and addon are added.
