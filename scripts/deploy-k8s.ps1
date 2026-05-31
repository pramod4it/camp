param(
    [switch]$BuildImages
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

if ($BuildImages) {
    & "$PSScriptRoot\build-images.ps1"
}

kubectl apply -f .\k8s\00-namespace.yml
kubectl apply -f .\k8s\01-config.yml
kubectl apply -f .\k8s\02-infra.yml
kubectl apply -f .\k8s\03-apps.yml

Write-Host "Waiting for application deployments..."
kubectl rollout status deployment/config-server -n camp --timeout=180s
kubectl rollout status deployment/discovery-server -n camp --timeout=180s
kubectl rollout status deployment/auth-service -n camp --timeout=180s
kubectl rollout status deployment/api-gateway -n camp --timeout=180s

Write-Host "Gateway NodePort: http://localhost:30080"
