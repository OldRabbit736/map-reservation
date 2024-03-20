docker build -t map-reservation-apiserver:0.0.1-compose ../apiserver/
docker compose -f ../docker-compose-local.yml --project-name map-reservation-local up -d
