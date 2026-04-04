resource "aws_iam_policy" "managed_app_policy" {
  policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Effect" : "Allow",
        "Action" : "secretsmanager:GetSecretValue",
        "Resource": var.aurora_db_secret_arn
      }
    ]
  })
}

resource "aws_iam_role" "app_role" {
  name = "app_role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Federated = var.eks_oidc_provider_arn
        }
        Action = "sts:AssumeRoleWithWebIdentity"
        Condition = {
          StringEquals = {
            "${var.eks_oidc_provider}:aud" = "sts.amazonaws.com"
            "${var.eks_oidc_provider}:sub" = "system:serviceaccount:default:${var.app_sa_name}"
          }
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "app_role_managed_policy_attachment" {
  role       = aws_iam_role.app_role.name
  policy_arn = aws_iam_policy.managed_app_policy.arn
}

resource "aws_iam_role_policy_attachment" "app_role_custom_policy_attachment" {
  count      = var.custom_app_policy_arn != null ? 1 : 0
  role       = aws_iam_role.app_role.name
  policy_arn = var.custom_app_policy_arn
}

