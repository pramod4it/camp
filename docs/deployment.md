# Deployment

## Docker Compose

Build the Spring Boot jars:

```powershell
cd D:\camp
mvn clean package -DskipTests
```

Run only infrastructure:

```powershell
docker compose up -d
```

Run infrastructure and all application containers:

```powershell
docker compose --profile apps up -d --build
```

Gateway:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

Kibana:

```text
http://localhost:5601
```

Stop everything:

```powershell
docker compose --profile apps down
```

## Kubernetes

Build images locally:

```powershell
cd D:\camp
.\scripts\build-images.ps1
```

Deploy:

```powershell
kubectl apply -f .\k8s\00-namespace.yml
kubectl apply -f .\k8s\01-config.yml
kubectl apply -f .\k8s\02-infra.yml
kubectl apply -f .\k8s\03-apps.yml
```

Or use the helper:

```powershell
.\scripts\deploy-k8s.ps1 -BuildImages
```

Gateway:

```text
http://localhost:30080
```

Swagger UI:

```text
http://localhost:30080/swagger-ui.html
```

Kibana NodePort:

```text
http://localhost:30601
```

Check status:

```powershell
kubectl get pods -n camp
kubectl get svc -n camp
```

Delete:

```powershell
kubectl delete namespace camp
```

## Image Names

The Kubernetes manifests use local images:

```text
camp/config-server:latest
camp/discovery-server:latest
camp/api-gateway:latest
camp/user-service:latest
camp/inventory-service:latest
camp/order-service:latest
camp/payment-service:latest
camp/notification-service:latest
camp/search-service:latest
```

For a remote cluster, push these images to a registry and update the image names in `k8s/03-apps.yml`.
