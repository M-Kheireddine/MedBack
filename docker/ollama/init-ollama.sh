#!/bin/sh
set -eu

OLLAMA_MODEL="${OLLAMA_MODEL:-llama3}"

ollama serve &
OLLAMA_PID=$!

until ollama list >/dev/null 2>&1; do
  sleep 2
done

if ! ollama list | awk '{print $1}' | grep -qx "${OLLAMA_MODEL}"; then
  ollama pull "${OLLAMA_MODEL}"
fi

wait "${OLLAMA_PID}"
