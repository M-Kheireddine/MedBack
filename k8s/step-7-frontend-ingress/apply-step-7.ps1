$ErrorActionPreference = "Stop"

$namespace = "medback"
$stepPath = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host "Applying Step 7 manifests to namespace '$namespace'..."

kubectl apply -f (Join-Path $stepPath "front-med-service.yaml")
kubectl apply -f (Join-Path $stepPath "front-med-deployment.yaml")
kubectl apply -f (Join-Path $stepPath "medback-ingress.yaml")

Write-Host "Waiting for frontend deployment rollout..."
kubectl rollout status deployment/front-med -n $namespace --timeout=180s

Write-Host "Step 7 completed successfully."
