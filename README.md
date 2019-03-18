# Planet-API

Planet-API é um webservice para registro e consulta de planetas com integração ao serviço SWAPI (https://swapi.co), utilizado para obtenção de informações sobre a franquia StarWars.

A aplicação consiste de um servidor Vertx e um banco de dados Cassandra, é escrita em linguagem Kotlin e possui integração ao serviço SWAPI via HTTP.
![alt text](https://i.imgur.com/l6YyRwU.png)

# Funcional

O Planet-API fornece interface REST/HTTP para a execução de operações. Abaixo há a descrição de cada operação fornecida:

## Adicionar um planeta (com nome, clima e terreno)
A API de adicionar planeta faz a persistência das informações indicadas no banco de dados local.
Deve-se passar a representação do Planeta em formato JSON, com id, nome, clima e terreno.
Durante o processamento, o serviço https://swapi.co é consultado com base no ID indicado na representação do planeta, caso exista um planeta com o mesmo ID é calculado o total de exibições em filmes deste planet (com base na quantidade de elementos no array "films" fornecido pela API). Caso o ID não existe no serviço externo, o total de exibições será 0.
Apenas o total de exibições é derivado do serviço https://swapi.co, todos os outros campos são persistidos localmente com base nos valores de entrada da requisição.
O planeta criado é retornado no body do response se for processado com sucesso.

Request:
```
curl -X POST \
  http://localhost:4000/v1/planet \
  -H 'Content-Type: application/json' \
  -d '{
    "id": 1,
    "name": "Alderaan",
    "climate": "temperate",
    "terrain": "grasslands, mountains"
}'
```
Response:
```
POST /v1/planet
Content-Type: application/json
Accept: */*
Host: localhost:4000
accept-encoding: gzip, deflate
content-length: 107
{ "id": 1, "name": "Alderaan", "climate": "temperate", "terrain": "grasslands, mountains" }

HTTP/1.1 200
status: 200
content-type: application/json
content-length: 98
{"id":1,"name":"Alderaan","climate":"temperate","terrain":"grasslands, mountains","exhibitions":5}
```

## Listar planetas do banco de dados
A API de lista de planetas faz a busca de todos os planetas do banco de dados local e retorna em formato JSON.
Sempre é retornado um array com a lista de planetas existentes, mesmo que retorne uma lista vazia.

```
curl -X GET http://localhost:4000/v1/planet
```

Response:
```
GET /v1/planet
cache-control: no-cache
Accept: */*
Host: localhost:4000
accept-encoding: gzip, deflate

HTTP/1.1 200
status: 200
content-type: application/json
content-length: 220
[{"id": 1, "name": "Alderaan", "climate": "temperate", "exhibitions": 5, "terrain": "grasslands, mountains"}, {"id": 2, "name": "Alderaan-2", "climate": "temperate", "exhibitions": 2, "terrain": "grasslands, mountains"}]
```

## Listar planetas da API do Star Wars
A API de lista de planetas da API SWAPI faz a busca de planetas diretamente no serviço externo, mas exibe somente o formato da API Planet-API contendo nome, clima, terreno e exibições.

Request:
```
curl -X GET http://localhost:4000/v1/swapi
```
Response:
```
GET /v1/swapi
cache-control: no-cache
Accept: */*
Host: localhost:4000
accept-encoding: gzip, deflate

HTTP/1.1 200
status: 200
content-type: application/json
content-length: 1032
[{"id":null,"name":"Alderaan","climate":"temperate","terrain":"grasslands, mountains","exhibitions":2}, {"id":null,"name":"Yavin IV","climate":"temperate, tropical","terrain":"jungle, rainforests","exhibitions":1}, {"id":null,"name":"Hoth","climate":"frozen","terrain":"tundra, ice caves, mountain ranges","exhibitions":1}, {"id":null,"name":"Dagobah","climate":"murky","terrain":"swamp, jungles","exhibitions":3}, {"id":null,"name":"Bespin","climate":"temperate","terrain":"gas giant","exhibitions":1}, {"id":null,"name":"Endor","climate":"temperate","terrain":"forests, mountains, lakes","exhibitions":1}, {"id":null,"name":"Naboo","climate":"temperate","terrain":"grassy hills, swamps, forests, mountains","exhibitions":4}, {"id":null,"name":"Coruscant","climate":"temperate","terrain":"cityscape, mountains","exhibitions":4}, {"id":null,"name":"Kamino","climate":"temperate","terrain":"ocean","exhibitions":1}, {"id":null,"name":"Geonosis","climate":"temperate, arid","terrain":"rock, desert, mountain, barren","exhibitions":1}]
```

Obs: Por limitação da SWAPI, a listagem de planetas não possui ID.

## Buscar por nome no banco de dados
A busca de planetas por nome faz a listagem de todos os planetas do banco de dados local com o nome indicado.
É utilizado a mesma API de listar planetas do banco de dados com o filtro na própria query:

Request:
```
curl -X GET 'http://localhost:4000/v1/planet?name=Alderaan'
```
Response:
```
GET /v1/planet
cache-control: no-cache
Accept: */*
Host: localhost:4000
accept-encoding: gzip, deflate

HTTP/1.1 200
status: 200
content-type: application/json
content-length: 109
[{"id": 1, "name": "Alderaan", "climate": "temperate", "exhibitions": 5, "terrain": "grasslands, mountains"}]
```

## Buscar por ID no banco de dados
A busca de planetas por ID faz a busca de um único planeta que possua o identificador indicado.
A identificação do planeta é inforado por parâmetro na URI.

Request:
```
curl -X GET http://localhost:4000/v1/planet/1
```
Response:
```
GET /v1/planet/1
cache-control: no-cache
Accept: */*
Host: localhost:4000
accept-encoding: gzip, deflate

HTTP/1.1 200
status: 200
content-type: application/json
content-length: 112
{"id": 1, "name": "Alderaan", "climate": "temperate", "exhibitions": 5, "terrain": "grasslands, mountains"}
```

## Remover planeta
A remoção de planeta efetua o DELETE do planeta no banco de dados local.
A remoção é feita somente por ID, que deve ser indicado na URI. O planeta removido é retornado no body da requisição quando sucesso.

Request:
```
curl -X DELETE http://localhost:4000/v1/planet/1
```
Response:
```
DELETE /v1/planet/1
cache-control: no-cache
Accept: */*
Host: localhost:4000
accept-encoding: gzip, deflate

HTTP/1.1 200
status: 200
content-type: application/json
content-length: 112
{"id": 1, "name": "Alderaan", "climate": "temperate", "exhibitions": 5, "terrain": "grasslands, mountains"}
```


## OpenAPI

As APIs descritas estão especificadas no padrão OpenAPI 3.0, sendo possível utilizar a especificação para gerar o client com ferramentas utilitárias
A especificação está disponível em 
```
.\planet-server\src\main\resources\specs\planet.yaml
```

# DataFlow

A aplicação Planet-API é um servidor HTTP Vertx com conexão à um banco de dados Cassandra.
Há quatro componentes: PlanetServer, Handlers, PlanetPersistence e SwapiClient

### PlanetServer
PlanetServer é o serviço principal que faz a subida do servidor HTTP.
Este serviço faz a leitura da especificação OpenAPI disponível em ./resources/spec, diponibiliza as interfaces HTTP e direciona o tráfedo para o Handler de cada operação.
### Handlers
Cada operação disponibilizada na interface HTTP possui um Handler para o tratamento.
Cada Handler executa as operações na camada de persistência ou no serviço HTTP externo, transforma o resultado na resposta adequada e responde a requisição HTTP.
### PlanetPersistence
A camada de persistência no banco de dados local é feito pelo PlanetPersistce.
Este serviço possui a comunicação com o bando de dados Cassandra gerenciando o acesso à dados, fornecedno uma API simplificada para o serviço de Handlers.
A API é assíncrona, retornando valores encapusulados em Futures.
### SwapiClient
O serviço SwapiClient é utilizado para comunicação com o servidor externo https://swapi.co.
A integração com o serviço externao é feito por interface HTTP, o resultado obtido pelo sistema externo é transformado antes do retorno ao Handler que fez a requisição.
A API é assíncrona, retornando valores encapusulados em Futures.

O diagrama abaixo, com o exemplo da criação de planeta, exemplifica o fluxo Server -> Handler -> Swapi|Persistence.

![alt text](https://i.imgur.com/MLXNZSD.png)

# Running up

### Cassandra
Para executar o serviço do Planet-API, é necessário a inicialização do banco de dados Cassandra.
No diretório ./planet-database, existe os scripts para criação do KEYSPACE e da tabela.

Com docker:
Inicializa o serviço de banco de dados:
```
docker run --name planet-database -p 127.0.0.1:9042:9042 -p 127.0.0.1:9160:9160 -d cassandra:3.11
```
Faz o copy dos scripts para o container:
```
docker cp .\cassandra\KEYSPACE\PLANET_CLI.cql planet-database:/home/PLANET_CLI.cql
docker cp .\cassandra\TAB\PLANET.cql planet-database:/home/PLANET.cql
```
Executa os scripts para criação do keyspace e da tabela:
```
docker exec planet-database cqlsh -f /home/PLANET_CLI.cql
docker exec planet-database cqlsh -f /home/PLANET.cql
```

Manualmente:

No serviço do Cassandra, criar o KEYSPACE:
```cql
CREATE KEYSPACE PLANET_CLI
  WITH REPLICATION = {
   'class' : 'SimpleStrategy', 
   'replication_factor' : 1 
  };
```
Criar a Tabela:
```cql
CREATE TABLE PLANET_CLI.PLANET ( 
   ID        int,
   NAME      text,
   CLIMATE   text,
   TERRAIN   text,
   EXHIBITIONS smallint,
   PRIMARY KEY (ID, NAME));
```

### Vert.x

Após a inicialização do banco de dados, é necessário a subida do serviço HTTP.
Há o commit do .jar pronto para execução:
```
.\planet-server\target\planet-server-1.0.0-fat.jar
```

Fazer o checkout do project, na raiz executar a JVM:

```sh
java -jar ./planet-server/target/planet-server-1.0.0-fat.jar -Dlogback.configuration=./src/main/resources/logback.xml -Dplanet.log=./planet-server/log -Dopenapi.spec=./planet-server/src/main/resources/specs/planet.yaml -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory -Dvertx.port=4000 -Dcassandra.contactpoint=localhost -Dcassandra.keyspace=planet_cli -Drest.get.limit=50 -dvertx.disableDnsResolver=true
```

Abaixo a descrição dos diversos parâmetros:
| -DParam | Descrição |
| ------ | ------ |
| logback.configuration | Apontamento para o arquivo de logs do logback |
| openapi.spec | Apontamento para o arquivo de especificação de API OpenAPI |
| planet.log | Diretório para despejo de logs |
| vertx.logger-delegate-factory-class-name= | Classe para delegate de logs do Vert.x |
| vertx.port | Porta para disponibilização do serviço HTTP |
| cassandra.contactpoint | ContactPoint do cluster Cassandra |
| cassandra.keyspace | Keyspace da aplicação Planet-API no cluster Cassandra |
| rest.get.limit | Limit de registros retornados em um GET de listagem |
| vertx.disableDnsResolver | Flag para desligar o serviço de lookup no DNS |

Se preferir fazer o build manualmente, o buildtool é Maven (3.5.0)
```sh
cd ./planet-api/planet-server
mvn package
```