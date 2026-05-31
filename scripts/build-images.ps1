$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

mvn -DskipTests package

$services = @(
    "config-server",
    "discovery-server",
    "auth-service",
    "api-gateway",
    "user-service",
    "inventory-service",
    "order-service",
    "payment-service",
    "notification-service",
    "search-service"
)

foreach ($service in $services) {
    docker build --build-arg SERVICE=$service -t "camp/$service`:latest" .
    docker tag "camp/$service`:latest" "camp/$service`:deploy"
}
