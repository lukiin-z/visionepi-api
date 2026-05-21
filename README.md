# VisionEPI — API REST

API REST desenvolvida com **Spring Boot** para gerenciamento de detecções de EPI (Equipamentos de Proteção Individual) realizadas pelo sistema de visão computacional VisionEPI em ambientes industriais.

> **Sprint 1 — Advanced Backend | FIAP Engenharia de Computação 3ECR**

---

## Integrantes

| Nome | RM |
|---|---|
| Pedro Gonçalves | 557936 |
| Lucas Baraldi | 555407 |
| Lucas Zolla | 557952 |
| Vitor Pantarotto | 554961 |

---

## Entidade Principal: `DeteccaoEpi`

Representa uma detecção realizada pelo sistema de visão computacional. Cada registro corresponde a um frame analisado por uma câmera, identificando quais EPIs o operador estava utilizando no momento da captura.

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | Long | Identificador gerado automaticamente |
| `nomeOperador` | String | Nome completo do operador |
| `matricula` | String | Matrícula funcional |
| `setor` | String | Setor de atuação (ex: Soldagem) |
| `descricaoCamera` | String | Localização da câmera |
| `dataDeteccao` | LocalDateTime | Preenchido automaticamente no cadastro |
| `capacete` | String (S/N) | Capacete detectado |
| `colete` | String (S/N) | Colete detectado |
| `luva` | String (S/N) | Luvas detectadas |
| `oculos` | String (S/N) | Óculos de proteção detectados |
| `botina` | String (S/N) | Botina de segurança detectada |
| `confianca` | Double | Confiança do modelo (0–100%) |
| `nivelRisco` | Integer | Calculado automaticamente (1–5) |

---

## Endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/deteccoes` | Registra uma nova detecção |
| `GET` | `/deteccoes` | Lista todas as detecções |
| `GET` | `/deteccoes?setor=Soldagem` | Filtra por setor |
| `GET` | `/deteccoes?matricula=004521` | Filtra por matrícula |
| `GET` | `/deteccoes?nivelRiscoMinimo=3` | Filtra por nível de risco mínimo |
| `GET` | `/deteccoes/{id}` | Busca detecção por ID |
| `PUT` | `/deteccoes/{id}` | Atualiza detecção existente |
| `DELETE` | `/deteccoes/{id}` | Remove detecção |

---

## Como rodar o projeto

### Pré-requisitos

- Java 17+
- Maven 3.8+

### Passos

```bash
# 1. Clonar o repositório
git clone https://github.com/lukiin-z/visionepi-api.git
cd visionepi-api

# 2. Compilar e subir a aplicação
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

O banco H2 persiste os dados na pasta `./data/visionepi_db`.

Console H2 (para inspecionar o banco): `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/visionepi_db`
- User: `sa` | Password: *(vazio)*

---

## Exemplos de uso (Postman / Insomnia)

### POST `/deteccoes` — Criar detecção

```json
POST http://localhost:8080/deteccoes
Content-Type: application/json

{
  "nomeOperador": "João Silva",
  "matricula": "004521",
  "setor": "Soldagem",
  "descricaoCamera": "Câmera 01 - Entrada Soldagem",
  "capacete": "S",
  "colete": "S",
  "luva": "N",
  "oculos": "N",
  "botina": "S",
  "confianca": 97.3
}
```

**Resposta `201 Created`:**
```json
{
  "id": 1,
  "nomeOperador": "João Silva",
  "matricula": "004521",
  "setor": "Soldagem",
  "descricaoCamera": "Câmera 01 - Entrada Soldagem",
  "dataDeteccao": "2026-05-21T10:35:00",
  "capacete": "S",
  "colete": "S",
  "luva": "N",
  "oculos": "N",
  "botina": "S",
  "confianca": 97.3,
  "nivelRisco": 2
}
```

### GET `/deteccoes` — Listar todas

```
GET http://localhost:8080/deteccoes
```

### GET `/deteccoes/1` — Buscar por ID

```
GET http://localhost:8080/deteccoes/1
```

**Resposta `404 Not Found`** se o ID não existir.

### GET com filtro — Por setor

```
GET http://localhost:8080/deteccoes?setor=Soldagem
```

### GET com filtro — Risco mínimo 3

```
GET http://localhost:8080/deteccoes?nivelRiscoMinimo=3
```

### PUT `/deteccoes/1` — Atualizar

```json
PUT http://localhost:8080/deteccoes/1
Content-Type: application/json

{
  "nomeOperador": "João Silva",
  "matricula": "004521",
  "setor": "Soldagem",
  "descricaoCamera": "Câmera 01 - Entrada Soldagem",
  "dataDeteccao": "2026-05-21T10:35:00",
  "capacete": "S",
  "colete": "S",
  "luva": "S",
  "oculos": "S",
  "botina": "S",
  "confianca": 98.0
}
```

### DELETE `/deteccoes/1` — Remover

```
DELETE http://localhost:8080/deteccoes/1
```

**Resposta `204 No Content`** em caso de sucesso.
**Resposta `404 Not Found`** se o ID não existir.

---

## Arquitetura

```
src/main/java/br/com/fiap/visionepi/
├── VisionepiApplication.java       ← Ponto de entrada
├── model/
│   └── DeteccaoEpi.java            ← Entidade JPA
├── repository/
│   └── DeteccaoEpiRepository.java  ← JpaRepository
├── service/
│   └── DeteccaoEpiService.java     ← Regras de negócio
└── controller/
    ├── DeteccaoEpiController.java  ← Endpoints REST
    └── GlobalExceptionHandler.java ← Tratamento de erros
```

---

## Tecnologias

| Tecnologia | Versão |
|---|---|
| Java | 17 |
| Spring Boot | 3.2.5 |
| Spring Web | — |
| Spring Data JPA | — |
| H2 Database (file mode) | — |
| Bean Validation | — |
| Lombok | — |
| Maven | 3.8+ |
