$ErrorActionPreference = "Stop"

$stepDirectory = Split-Path -Parent $MyInvocation.MyCommand.Path

kubectl apply -f (Join-Path $stepDirectory "med-config-repository-configmap.yaml")
kubectl apply -f (Join-Path $stepDirectory "med-discovery-service.yaml")
kubectl apply -f (Join-Path $stepDirectory "med-discovery-deployment.yaml")
kubectl apply -f (Join-Path $stepDirectory "med-config-service.yaml")
kubectl apply -f (Join-Path $stepDirectory "med-config-deployment.yaml")
