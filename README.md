Deploy the backend first

```sh
cd stock-dashboard-api/terraform
terraform apply
```

Set k8s context from eks. Example: 

```sh
aws eks update-kubeconfig --region ap-south-1 --name stock-dashboard-dev
```

Then deploy the k8s stuff. Change the certificate in the ingress

```sh
cd stock-dashboard-api/deployment/prod
kubectl apply -f nginx-deployment.yaml
kubectl apply -f nginx-service.yaml
kubectl apply -f ingress.yaml
```

TODO:  
1. Migrate to helm
2. Actually make the backend :smile:
