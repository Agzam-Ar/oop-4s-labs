#!/bin/bash
set -e

echo "Stopping PostgreSQL..."
docker stop lab3-postgres
docker rm lab3-postgres

echo "PostgreSQL stopped."
