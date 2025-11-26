# 🎉 Projeto Transilvania - Sistema de Consulta de CEP

## ✅ Status: Projeto Implementado

O projeto **Transilvania - Sistema de Consulta de CEP para Reserva de Hotéis** é uma aplicação Spring Boot que integra com a API ViaCEP.

---

## 📦 Arquitetura MVC

### 1. ✅ Model (Entidades)
- `ConsultaCep.java` - Entidade JPA com validações

### 2. ✅ Repository
- `ConsultaCepRepository.java` - Spring Data JPA com queries customizadas

### 3. ✅ Service
- `CepService.java` - Lógica de negócio (consulta, validação, integração com API externa)

### 4. ✅ Controller
- `CepController.java` - REST API com 4 endpoints

### 5. ✅ DTOs
- `CepApiResponse.java` - Resposta da API ViaCEP
- `ConsultaCepResponse.java` - Resposta da aplicação
- `ErrorResponse.java` - Tratamento de erros

### 6. ✅ Exception Handling
- `CepNaoEncontradoException`, `CepInvalidoException`, `ErroConsultaExternaException`
- `GlobalExceptionHandler` - Tratamento centralizado de exceções

### 7. ✅ Configuration
- `RestTemplateConfig.java` - Cliente HTTP para API externa

### 8. ✅ Banco de Dados H2
- H2 in-memory para desenvolvimento
- JPA/Hibernate configurado
- Console H2 disponível em `/h2-console`

### 9. ✅ Validações
- Validação de formato de CEP
- Bean Validation nos DTOs e entidades
- Tratamento de erros completo

### 10. ✅ API Externa
- Integração com ViaCEP (https://viacep.com.br)
- Validações e tratamento de erros
- Logs detalhados

---

## 🚀 API Endpoints Implementados

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/cep/consultar` | Consulta CEP e registra no banco |
| GET | `/api/cep/{cep}` | Consulta CEP diretamente |
| GET | `/api/cep/consultas` | Lista todas as consultas |
| GET | `/api/cep/historico/{cep}` | Histórico de um CEP específico |

---

## 📊 Estrutura do Projeto

```
transilvania/
├── src/
│   ├── main/
│   │   ├── java/com/hotel/transilvania/
│   │   │   ├── model/
│   │   │   │   └── ConsultaCep.java
│   │   │   ├── repository/
│   │   │   │   └── ConsultaCepRepository.java
│   │   │   ├── service/
│   │   │   │   └── CepService.java
│   │   │   ├── controller/
│   │   │   │   └── CepController.java
│   │   │   ├── dto/
│   │   │   │   ├── CepApiResponse.java
│   │   │   │   ├── ConsultaCepResponse.java
│   │   │   │   └── ErrorResponse.java
│   │   │   ├── exception/
│   │   │   │   ├── CepNaoEncontradoException.java
│   │   │   │   ├── CepInvalidoException.java
│   │   │   │   ├── ErroConsultaExternaException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   └── config/
│   │   │       └── RestTemplateConfig.java
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│       └── java/com/hotel/transilvania/
│           └── TransilvaniaApplicationTests.java
│
├── pom.xml
├── settings.xml                           # Maven settings (bypass corporate proxy)
└── QUICKSTART.md
```

---

## 🔧 Como Executar

```powershell
# Compilar (usar settings.xml para bypass do proxy corporativo)
./mvnw clean install -s settings.xml

# Executar aplicação
./mvnw spring-boot:run -s settings.xml

# Aplicação estará disponível em:
# http://localhost:8080
# H2 Console: http://localhost:8080/h2-console
```

---

## 🧪 Testando a API

### 1. Consultar CEP
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/cep/01310100" -Method GET
```

### 2. Listar todas as consultas
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/cep/consultas" -Method GET
```

### 3. Buscar por ID
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/cep/consultas/1" -Method GET
```

### 4. Histórico de um CEP
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/cep/historico/01310100" -Method GET
```

---

## 📝 Tecnologias

- Java 17
- Spring Boot 3.4.0
- Spring Data JPA
- H2 Database (in-memory)
- Bean Validation
- Lombok
- Maven

---

## ✨ Conclusão

Aplicação MVC simples e funcional para consulta de CEPs com persistência em H2 e integração com ViaCEP.
