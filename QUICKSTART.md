# Guia de Início Rápido - Transilvania

## 🚀 Setup Inicial

### 1. Compilar o projeto
```powershell
# Limpar e compilar (usar settings.xml para ambiente corporativo)
./mvnw clean install -s settings.xml
```

### 2. Executar a aplicação
```powershell
# Iniciar aplicação
./mvnw spring-boot:run -s settings.xml

# Aplicação estará disponível em:
# http://localhost:8080
# H2 Console: http://localhost:8080/h2-console
```

### 3. Testar a API

#### Consultar CEP
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/cep/01310100" -Method GET
```

#### Listar todas as consultas
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/cep/consultas" -Method GET
```

#### Buscar por ID
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/cep/consultas/1" -Method GET
```

#### Buscar histórico de um CEP
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/cep/historico/01310100" -Method GET
```

## 📊 Acessar o Banco H2

1. Acesse: http://localhost:8080/h2-console
2. JDBC URL: `jdbc:h2:mem:transilvania_db`
3. User: `sa`
4. Password: (deixe em branco)
5. Consultar tabela:
```sql
```sql
SELECT * FROM consultas_cep ORDER BY data_consulta DESC;
```

## 📝 Endpoints da API

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/cep/{cep}` | Consulta CEP diretamente |
| GET | `/api/cep/consultas` | Lista todas as consultas |
| GET | `/api/cep/consultas/{id}` | Busca consulta por ID |
| GET | `/api/cep/historico/{cep}` | Histórico de um CEP |

## 🔧 Tecnologias

- Java 17
- Spring Boot 3.4.0
- Spring Data JPA
- H2 Database (in-memory)
- Bean Validation
- Lombok
- Maven

## 🐛 Troubleshooting

### Porta 8080 já está em uso
```powershell
# Encontrar processo usando a porta
Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess

# Matar processo
taskkill /F /PID <process-id>
```

### Erro de compilação Maven
```powershell
# Limpar cache Maven e recompilar
./mvnw clean install -s settings.xml -U
```
7. 🔜 Observabilidade (Prometheus/Grafana)
