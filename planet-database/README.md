#planet-database

cd .\planet-api\planet-database

Inicializa o serviço do banco de dados:
```
docker run --name planet-database -p 127.0.0.1:9042:9042 -p 127.0.0.1:9160:9160 -d cassandra:3.11
docker cp .\cassandra\KEYSPACE\PLANET_CLI.cql planet-database:/home/PLANET_CLI.cql
docker cp .\cassandra\TAB\PLANET.cql planet-database:/home/PLANET.cql
docker exec planet-database cqlsh -f /home/PLANET_CLI.cql
docker exec planet-database cqlsh -f /home/PLANET.cql
```