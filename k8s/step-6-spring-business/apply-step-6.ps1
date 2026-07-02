$ErrorActionPreference = "Stop"

$namespace = "medback"
$stepPath = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host "Applying Step 6 manifests to namespace '$namespace'..."

kubectl apply -f (Join-Path $stepPath "med-core-storage-pvc.yaml")

kubectl apply -f (Join-Path $stepPath "med-gateway-service.yaml")
kubectl apply -f (Join-Path $stepPath "med-gateway-deployment.yaml")

kubectl apply -f (Join-Path $stepPath "med-user-service-service.yaml")
kubectl apply -f (Join-Path $stepPath "med-user-service-deployment.yaml")

kubectl apply -f (Join-Path $stepPath "med-core-service-service.yaml")
kubectl apply -f (Join-Path $stepPath "med-core-service-deployment.yaml")

kubectl apply -f (Join-Path $stepPath "med-chatboot-service-service.yaml")
kubectl apply -f (Join-Path $stepPath "med-chatboot-service-deployment.yaml")

kubectl apply -f (Join-Path $stepPath "med-notification-service-service.yaml")
kubectl apply -f (Join-Path $stepPath "med-notification-service-deployment.yaml")

Write-Host "Waiting for business deployments to roll out..."

kubectl rollout status deployment/med-gateway -n $namespace --timeout=180s
kubectl rollout status deployment/med-user-service -n $namespace --timeout=180s
kubectl rollout status deployment/med-core-service -n $namespace --timeout=180s
kubectl rollout status deployment/med-chatboot-service -n $namespace --timeout=180s
kubectl rollout status deployment/med-notification-service -n $namespace --timeout=180s

Write-Host "Step 6 completed successfully."
