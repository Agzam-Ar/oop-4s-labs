#!/bin/bash
set -e

echo "Stopping PostgreSQL and removing data..."
docker stop lab3-postgres 2>/dev/null || true
docker rm lab3-postgres

echo "Data removed."
