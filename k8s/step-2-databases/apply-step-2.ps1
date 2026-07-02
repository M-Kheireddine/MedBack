$ErrorActionPreference = "Stop"

$stepDirectory = Split-Path -Parent $MyInvocation.MyCommand.Path

kubectl apply -f (Join-Path $stepDirectory "med-postgres-service.yaml")
kubectl apply -f (Join-Path $stepDirectory "med-postgres-pvc.yaml")
kubectl apply -f (Join-Path $stepDirectory "med-postgres-statefulset.yaml")

kubectl apply -f (Join-Path $stepDirectory "med-mongodb-service.yaml")
kubectl apply -f (Join-Path $stepDirectory "med-mongodb-pvc.yaml")
kubectl apply -f (Join-Path $stepDirectory "med-mongodb-statefulset.yaml")
