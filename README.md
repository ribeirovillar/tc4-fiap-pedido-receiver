# FIAP Pedido Receiver

Sistema de recebimento de pedidos via API REST com envio para RabbitMQ.

---

## 🚀 Tecnologias

- **Java 24**
- **Spring Boot 3.5.4**
- **RabbitMQ** (message broker)
- **MapStruct** (mapeamento de objetos)
- **Lombok** (redução de boilerplate)
- **JaCoCo** (cobertura de testes)
- **Docker & Docker Compose**
- **Arquitetura Limpa (Clean Architecture)**

---

## 📋 Endpoints REST

| Método | Endpoint             | Descrição                |
|--------|----------------------|--------------------------|
| POST   | `/orders`           | Criar e enviar pedido    |

---

## 🏗️ Arquitetura

O projeto segue os princípios da **Clean Architecture** com as seguintes camadas:

```
├── Controller (Interface REST)
├── UseCase (Regras de Negócio)
├── Gateway (Interface de Saída)
├── Producer (Implementação RabbitMQ)
└── Domain (Entidades de Negócio)
```

---

## 📦 Estrutura do Projeto

```
src/
├── main/java/com/fiap/pedido/
│   ├── controller/         # Controladores REST
│   ├── usecase/           # Casos de uso
│   ├── gateway/           # Interfaces de saída
│   │   └── messaging/     # Implementação RabbitMQ
│   ├── domain/            # Entidades de domínio
│   ├── mapper/            # Mapeamento MapStruct
│   └── configuration/     # Configurações Spring
└── test/                  # Testes unitários e integração
```

---

## 🚀 Como Executar

### Pré-requisitos
- Docker e Docker Compose
- Java 24 (para desenvolvimento local)
- Maven 3.9+

### Via Docker (Recomendado)
```bash
# Subir todos os serviços
docker-compose up --build

# Verificar logs
docker-compose logs -f

# Parar serviços
docker-compose down
```

### Desenvolvimento Local
```bash
# Subir apenas RabbitMQ
docker-compose up rabbitmq

# Executar aplicação via IDE ou Maven
mvn spring-boot:run
```

---

## 🧪 Testes

### Executar Todos os Testes
```bash
mvn clean test
```

### Relatório de Cobertura (JaCoCo)
```bash
mvn clean test jacoco:report
```

📊 **Meta de Cobertura:** 80%+  
📄 **Relatório:** `target/site/jacoco/index.html`

### Tipos de Teste
- **Unitários:** Testam componentes isoladamente
- **Integração:** Testam fluxo completo com MockMvc
- **Mocking:** RabbitTemplate mockado para evitar dependências

---

## 📡 API Reference

### Criar Pedido
```http
POST /orders
Content-Type: application/json

{
  "customerId": "123e4567-e89b-12d3-a456-426614174000",
  "cardNumber": "1234567890123456",
  "items": [
    {
      "sku": "SKU123",
      "quantity": 2
    }
  ]
}
```

**Response (201 Created):**
```json
{
  "orderId": "d87866f9-9f1f-4acc-a800-619a445e697c",
  "customerId": "123e4567-e89b-12d3-a456-426614174000",
  "cardNumber": "1234567890123456",
  "status": "ABERTO",
  "items": [
    {
      "sku": "SKU123",
      "quantity": 2
    }
  ]
}
```

**Error Response (503 Service Unavailable):**
```json
{
  "type": "about:blank",
  "title": "Message Queue Error",
  "status": 503,
  "detail": "Failed to connect to message queue service",
  "instance": "/orders"
}
```

