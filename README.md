# Stock Dashboard

## Components

### stock-dashboard-api

This acts as both a Backend for Frontend as well as a resource server.
This primarily handles all the core activities like authn, authz.

### stock-dashboard-api-sidecar

This is a python sidecar. It's primary function is to provide functionalities that
are hard to make in java. Examples:

1. Yahoo finance package to search stocks
2. Langgraph to create agents

The sidecar wouldn't be exposed to the internet

### stock-dashboard-frontend

TODO

## Deployment

Deploy the backend first

```sh
cd stock-dashboard-api/terraform
terraform apply
```

Set k8s context from eks. Example:

```sh
aws eks update-kubeconfig --region ap-south-1 --name stock-dashboard-prd
```

Then deploy the k8s stuff. Change the certificate in the ingress

```sh
cd stock-dashboard-api/deployment
kubectl apply -k prod
```

## Features (Planned and Active)

### Stocks core (WIP)

1. Search and add stocks (currently working)
2. Stock history (planned, but haven't started)
3. Use various knowledge bases to know more about the stocks

#### TODOs

1. ~~side car api~~
2. ~~integration with main api~~
3. Making the frontend
4. ~~Sidecar Dockerfile~~
5. ~~Deployment~~

### Chat with stocks(Planned)

1. Use knowledge sources to chat with a stock, search the current news
