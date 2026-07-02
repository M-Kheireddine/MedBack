$ErrorActionPreference = "Stop"

$stepDirectory = Split-Path -Parent $MyInvocation.MyCommand.Path

kubectl apply -f (Join-Path $stepDirectory "prometheus-configmap.yaml")
kubectl apply -f (Join-Path $stepDirectory "prometheus-service.yaml")
kubectl apply -f (Join-Path $stepDirectory "prometheus-pvc.yaml")
kubectl apply -f (Join-Path $stepDirectory "prometheus-deployment.yaml")

kubectl apply -f (Join-Path $stepDirectory "grafana-datasources-configmap.yaml")
kubectl apply -f (Join-Path $stepDirectory "grafana-service.yaml")
kubectl apply -f (Join-Path $stepDirectory "grafana-pvc.yaml")
kubectl apply -f (Join-Path $stepDirectory "grafana-deployment.yaml")

kubectl apply -f (Join-Path $stepDirectory "kafka-ui-service.yaml")
kubectl apply -f (Join-Path $stepDirectory "kafka-ui-deployment.yaml")
