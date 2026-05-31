param(
    [switch]$SkipDocker
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

if (-not $SkipDocker) {
    docker compose up -d
}

mvn -q -DskipTests package

$services = @(
    @{ Name = "config-server"; Port = 8888 },
    @{ Name = "discovery-server"; Port = 8761 },
    @{ Name = "api-gateway"; Port = 8080 },
    @{ Name = "user-service"; Port = 8081 },
    @{ Name = "inventory-service"; Port = 8086 },
    @{ Name = "order-service"; Port = 8082 },
    @{ Name = "payment-service"; Port = 8083 },
    @{ Name = "notification-service"; Port = 8084 },
    @{ Name = "search-service"; Port = 8085 }
)

foreach ($service in $services) {
    $jar = Join-Path $root "$($service.Name)\target\$($service.Name)-0.0.1-SNAPSHOT.jar"
    Start-Process powershell -WindowStyle Normal -ArgumentList @(
        "-NoExit",
        "-Command",
        "cd '$root'; java -jar '$jar'"
    )
    Start-Sleep -Seconds 4
}

Write-Host "Services are starting."
Write-Host "Gateway: http://localhost:8080"
Write-Host "Eureka:  http://localhost:8761"
Write-Host "Grafana: http://localhost:3000 admin/admin"
