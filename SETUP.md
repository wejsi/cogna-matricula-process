
---

**Microserviço Java (Matrícula Process)**

Foi adicionado um microserviço Spring Boot mínimo em `microservice/` com os seguintes arquivos:

- `microservice/pom.xml` — build Maven
- `microservice/src/main/java/com/cogna/core/CognaApplication.java` — aplicação Spring Boot
- `microservice/src/main/java/com/cogna/core/controller/HelloController.java` — endpoints `GET /hello` e `GET /health`
- `microservice/Dockerfile` — imagem Docker básica
- `docker-compose.yml` — compose para subir o serviço localmente

Como usar:

1. Buildar a aplicação com Maven:

```powershell
cd microservice
mvn -DskipTests package
```

2. Construir a imagem Docker:

```powershell
docker build -t cogna-microservice:latest .
```

3. Executar com Docker:

```powershell
docker run -p 8080:8080 cogna-microservice:latest
```

4. Ou subir com `docker compose` na raiz do repositório:

```powershell
docker compose up --build
```

Após iniciar, os endpoints disponíveis:

- `http://localhost:8080/hello`
- `http://localhost:8080/health`


**Requisitos**

- Java 17: ✅
- Spring Boot 3.x: ✅
- Kafka consumer + producer: ✅ (consumo com `@KafkaListener`, produção com `KafkaTemplate` em `microservice/src/main/java`)
- MongoDB (Spring Data): ✅ (`Matricula` + `MatriculaRepository` em `microservice/src/main/java`)
- Integração REST com serviço externo (configurável via `CICLO_API_URL`): ✅ (`CicloClient`)
- Testes unitários com JUnit 5 + Mockito: ✅ (`microservice/src/test/java/com/cogna/core/service/ProcessorServiceTest.java`)
- Dockerfile: ✅ (`microservice/Dockerfile`)
- Docker Compose com MongoDB + Kafka/Zookeeper: ✅ (`docker-compose.yml`)
- Gradle como build tool: ❌ — neste repositório optei por Maven (`microservice/pom.xml`). Se preferir, posso converter para Gradle.
- Gradle como build tool: ✅ (adicionado `microservice/build.gradle` e `microservice/settings.gradle`; Mantive `pom.xml` também)
- README com instruções de execução: ✅ (esta seção)

Como rodar os testes unitários:

```powershell
cd microservice
mvn test
```

Como subir a stack completa (MongoDB, Zookeeper, Kafka e aplicação):

```powershell
# na raiz do repositório
docker compose up --build
```

Variáveis de ambiente relevantes (podem ser definidas no `docker-compose.yml` ou no ambiente):

- `MONGODB_URI` (padrão: `mongodb://mongo:27017/matriculas`)
- `KAFKA_BOOTSTRAP_SERVERS` (padrão: `kafka:9092`)
- `CICLO_API_URL` (padrão usado no compose: `http://ciclo-api:8081`)

Observações:

- O projeto contém um `DataInitializer` que popula a coleção `matriculas` com os documentos de exemplo ao iniciar a aplicação.
- Para avaliar o fluxo completo de eventos, é preciso um produtor que publique mensagens no tópico `turma-atualizada`. Você pode usar `kafkacat` / `kcat` ou outra ferramenta para enviar um JSON compatível.

