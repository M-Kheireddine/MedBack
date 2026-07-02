$ErrorActionPreference = "Stop"

$stepDirectory = Split-Path -Parent $MyInvocation.MyCommand.Path

kubectl apply -f (Join-Path $stepDirectory "zookeeper-service.yaml")
kubectl apply -f (Join-Path $stepDirectory "zookeeper-pvc.yaml")
kubectl apply -f (Join-Path $stepDirectory "zookeeper-statefulset.yaml")

kubectl apply -f (Join-Path $stepDirectory "kafka-service.yaml")
kubectl apply -f (Join-Path $stepDirectory "kafka-pvc.yaml")
kubectl apply -f (Join-Path $stepDirectory "kafka-statefulset.yaml")

kubectl apply -f (Join-Path $stepDirectory "ollama-service.yaml")
kubectl apply -f (Join-Path $stepDirectory "ollama-pvc.yaml")
kubectl apply -f (Join-Path $stepDirectory "ollama-statefulset.yaml")

kubectl rollout status statefulset/zookeeper -n medback --timeout=300s
kubectl rollout status statefulset/kafka -n medback --timeout=300s
kubectl rollout status statefulset/ollama -n medback --timeout=600s

kubectl apply -f (Join-Path $stepDirectory "ollama-llama3-pull-job.yaml")
