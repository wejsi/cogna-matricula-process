# Teste Técnico — Desenvolvedor Java Sênior

## Contexto

Você está ingressando em um time que mantém microsserviços de processamento de dados acadêmicos. Os serviços seguem uma arquitetura event-driven: recebem eventos via Kafka, aplicam regras de negócio, persistem em MongoDB e publicam novos eventos para downstream.

Seu desafio é construir um microsserviço do zero que se encaixe nesse ecossistema.

---

## O Desafio

Crie um microsserviço chamado **`matricula-process`** que:

1. **Consome** um evento Kafka do tópico `turma-atualizada`
2. **Busca** dados complementares em um serviço REST externo (ciclo vigente)
3. **Aplica regra de negócio** (descrita abaixo)
4. **Persiste** o resultado em MongoDB
5. **Publica** um novo evento Kafka no tópico `matricula-atualizada`

---

## Evento de Entrada (tópico: `turma-atualizada`)

```json
{
  "businessKey": "GRAD/ENG/BACH/PRESENCIAL/NOTURNO/UNIT-SP-01",
  "turma": {
    "codigo": "T2026-001",
    "diasDaSemana": ["SEGUNDA", "QUARTA", "SEXTA"],
    "horarioInicio": "19:00",
    "horarioFim": "22:30",
    "vagas": 40
  },
  "cicloId": 20261
}
```

---

## Serviço REST Externo (Ciclo Vigente)

Seu microsserviço deve consultar um endpoint REST para validar se o ciclo está vigente:

**GET** `/api/ciclos/{cicloId}`

### Respostas possíveis

| Cenário | Status HTTP | Resposta |
|---------|:-----------:|----------|
| Ciclo vigente | 200 | `{"id": 20261, "ativo": true, "dataInicioCaptura": "2026-01-15", "dataFimCaptura": "2026-07-01"}` |
| Ciclo inativo | 200 | `{"id": 20262, "ativo": false, "dataInicioCaptura": "2026-01-15", "dataFimCaptura": "2026-07-01"}` |
| Ciclo expirado | 200 | `{"id": 20252, "ativo": true, "dataInicioCaptura": "2025-01-15", "dataFimCaptura": "2025-07-01"}` |
| Ciclo não encontrado | 404 | — |

> **Para desenvolvimento:** Implemente um mock do serviço externo (WireMock, stub controller ou similar).
>
> **Para avaliação:** Substituiremos o mock por um serviço real com o mesmo contrato. Garanta que a URL do serviço seja configurável via variável de ambiente (ex: `CICLO_API_URL`).

---

## Regras de Negócio

1. **Validação de ciclo vigente:** Consultar o serviço REST com o `cicloId` do evento. O ciclo é considerado vigente se **ambas** as condições forem verdadeiras:
   - `ativo == true`
   - A data em que a mensagem está sendo processada está dentro do período de captura (`dataInicioCaptura` ≤ data atual < `dataFimCaptura`)
   
   Ou seja, só processe o evento se o ciclo estiver ativo **e** o momento do processamento estiver dentro da janela de captura.

2. **Se ciclo NÃO vigente ou não encontrado (404):** Ignorar o evento (logar e descartar). Não persistir nem publicar.

3. **Se ciclo vigente:** Buscar no MongoDB todas as matrículas com o mesmo `businessKey` e status `ATIVA`.

4. **Comparação de dias:** Para cada matrícula encontrada, comparar os `diasDaSemana` da matrícula com os novos `diasDaSemana` da turma.
   - Se forem **iguais** → não fazer nada
   - Se forem **diferentes** → atualizar a matrícula com os novos dias e publicar evento

---

## Evento de Saída (tópico: `matricula-atualizada`)

Para cada matrícula atualizada, publicar:

```json
{
  "matriculaId": "64a1b2c3d4e5f6a7b8c9d0e1",
  "alunoId": "ALU-001",
  "businessKey": "GRAD/ENG/BACH/PRESENCIAL/NOTURNO/UNIT-SP-01",
  "cicloId": 20261,
  "diasDaSemanaAnterior": ["SEGUNDA", "QUARTA"],
  "diasDaSemanaNovo": ["SEGUNDA", "QUARTA", "SEXTA"],
  "dataAtualizacao": "2026-06-12T10:30:00"
}
```

> **Nota:** O campo `dataAtualizacao` deve conter a data/hora em que o processamento efetivamente ocorreu. O valor acima é apenas ilustrativo.

---

## Modelo de Dados (MongoDB)

Coleção: `matriculas`

```json
{
  "_id": "64a1b2c3d4e5f6a7b8c9d0e1",
  "alunoId": "ALU-001",
  "businessKey": "GRAD/ENG/BACH/PRESENCIAL/NOTURNO/UNIT-SP-01",
  "status": "ATIVA",
  "turma": {
    "codigo": "T2026-001",
    "diasDaSemana": ["SEGUNDA", "QUARTA"],
    "horarioInicio": "19:00",
    "horarioFim": "22:30"
  },
  "cicloId": 20261,
  "dataMatricula": "2026-02-10T08:00:00"
}
```

---

## Massa de Dados Inicial

Utilize os documentos abaixo para popular a coleção `matriculas` no MongoDB. Cada bloco representa **um documento individual** na coleção:

**Documento 1** — dias diferentes do evento (deve atualizar e publicar)
```json
{
  "alunoId": "ALU-001",
  "businessKey": "GRAD/ENG/BACH/PRESENCIAL/NOTURNO/UNIT-SP-01",
  "status": "ATIVA",
  "turma": {
    "codigo": "T2026-001",
    "diasDaSemana": ["SEGUNDA", "QUARTA"],
    "horarioInicio": "19:00",
    "horarioFim": "22:30"
  },
  "cicloId": 20261,
  "dataMatricula": "2026-02-10T08:00:00"
}
```

**Documento 2** — dias iguais ao evento (não faz nada)
```json
{
  "alunoId": "ALU-002",
  "businessKey": "GRAD/ENG/BACH/PRESENCIAL/NOTURNO/UNIT-SP-01",
  "status": "ATIVA",
  "turma": {
    "codigo": "T2026-001",
    "diasDaSemana": ["SEGUNDA", "QUARTA", "SEXTA"],
    "horarioInicio": "19:00",
    "horarioFim": "22:30"
  },
  "cicloId": 20261,
  "dataMatricula": "2026-02-12T09:00:00"
}
```

**Documento 3** — status CANCELADA (ignorar, não é ATIVA)
```json
{
  "alunoId": "ALU-003",
  "businessKey": "GRAD/ENG/BACH/PRESENCIAL/NOTURNO/UNIT-SP-01",
  "status": "CANCELADA",
  "turma": {
    "codigo": "T2026-001",
    "diasDaSemana": ["SEGUNDA", "QUARTA"],
    "horarioInicio": "19:00",
    "horarioFim": "22:30"
  },
  "cicloId": 20261,
  "dataMatricula": "2026-02-10T10:00:00"
}
```

**Documento 4** — businessKey diferente do evento (não deve ser afetado)
```json
{
  "alunoId": "ALU-004",
  "businessKey": "GRAD/ADM/BACH/EAD/NOTURNO/UNIT-RJ-02",
  "status": "ATIVA",
  "turma": {
    "codigo": "T2026-050",
    "diasDaSemana": ["TERÇA", "QUINTA"],
    "horarioInicio": "19:00",
    "horarioFim": "22:30"
  },
  "cicloId": 20261,
  "dataMatricula": "2026-02-11T14:00:00"
}
```

> **Importante:** Estes são apenas dados de exemplo para desenvolvimento. Na avaliação, a coleção conterá milhares de matrículas por `businessKey`.

---

## Requisitos Técnicos

| Requisito | Obrigatório |
|-----------|:-----------:|
| Java 17 ou 21 | ✅ |
| Spring Boot 3.x | ✅ |
| Kafka consumer + producer | ✅ |
| MongoDB (Spring Data) | ✅ |
| Integração REST com serviço externo (consumir API de ciclos) | ✅ |
| Testes unitários com JUnit 5 + Mockito | ✅ |
| Dockerfile | ✅ |
| Docker Compose com MongoDB + Kafka | ✅ |
| Gradle como build tool | ✅ |
| README com instruções de execução | ✅ |
| URL do serviço externo configurável via variável de ambiente | ✅ |

> **Nota:** O Docker Compose deve provisionar todas as dependências (MongoDB, Kafka/Zookeeper). A aplicação deve ser responsável por criar as estruturas necessárias (coleções, índices, tópicos) na inicialização ou via configuração.

---

## Diferenciais (não obrigatórios, mas valorizados)

- Separação em camadas (Clean Architecture, Hexagonal ou Ports & Adapters)
- Docker Compose subindo tudo (app + dependências) com um único `docker-compose up`
- Testes de integração com Testcontainers ou Embedded Kafka
- Retry/DLT no consumer Kafka (tratamento de falhas)
- Logs estruturados (JSON) com MDC para rastreabilidade