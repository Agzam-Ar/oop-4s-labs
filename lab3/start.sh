#!/bin/bash
set -e

echo "Starting PostgreSQL..."
docker run -d \
    --name lab3-postgres \
    -e POSTGRES_DB=lab3db \
    -e POSTGRES_USER=postgres \
    -e POSTGRES_PASSWORD=postgres \
    -p 5432:5432 \
    postgres:18-alpine

echo "Waiting for PostgreSQL to be ready..."
until docker exec lab3-postgres pg_isready -U postgres -d lab3db > /dev/null 2>&1; do
    sleep 1
done

echo "PostgreSQL is ready!"
echo "Connection: jdbc:postgresql://localhost:5432/lab3db"
echo "User: postgres / Password: postgres"
