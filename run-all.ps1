$ErrorActionPreference = "Stop"
.\build.ps1
docker compose -f docker-compose-dev.yaml up --build