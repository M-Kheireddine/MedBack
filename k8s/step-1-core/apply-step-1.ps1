$ErrorActionPreference = "Stop"

$stepDirectory = Split-Path -Parent $MyInvocation.MyCommand.Path

kubectl apply -f (Join-Path $stepDirectory "medback-namespace.yaml")

do {
    Start-Sleep -Seconds 2
    $namespacePhase = kubectl get namespace medback -o jsonpath="{.status.phase}" 2>$null
} while ($namespacePhase -ne "Active")

kubectl apply -f (Join-Path $stepDirectory "medback-global-configmap.yaml")
kubectl apply -f (Join-Path $stepDirectory "medback-global-secret.yaml")
kubectl apply -f (Join-Path $stepDirectory "prometheus-rbac.yaml")
