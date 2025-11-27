# 🏨 Hotel Guessr API

API REST desenvolvida em Spring Boot para consulta de CEPs e busca de hotéis próximos utilizando a Google Places API.

## 📋 Sobre o Projeto

O **Hotel Guessr** é uma aplicação que permite consultar informações de endereço através de um CEP e encontrar hotéis próximos a essa localização. A aplicação utiliza:
- **ViaCEP API** para consulta de endereços
- **Google Geocoding API** para conversão de endereços em coordenadas
- **Google Places API** para busca de hotéis próximos
- **PostgreSQL** para armazenamento de consultas realizadas

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.4.0**
- **Spring Data JPA**
- **PostgreSQL 15**
- **Docker & Docker Compose**
- **Maven**
- **Lombok**
- **SpringDoc OpenAPI (Swagger)**

## 📦 Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- [Docker](https://www.docker.com/get-started) (versão 20.10 ou superior)
- [Docker Compose](https://docs.docker.com/compose/install/) (versão 2.0 ou superior)
- [Java 17](https://adoptium.net/) (apenas para rodar localmente sem Docker)
- [Maven 3.8+](https://maven.apache.org/download.cgi) (apenas para rodar localmente sem Docker)

## 🔑 Configuração da API Key do Google

Para utilizar a funcionalidade de busca de hotéis, você precisa de uma API Key do Google Cloud Platform:

1. Acesse o [Google Cloud Console](https://console.cloud.google.com/)
2. Crie um novo projeto ou selecione um existente
3. Habilite as seguintes APIs:
   - **Places API**
   - **Geocoding API**
4. Crie uma API Key em **APIs & Services > Credentials**
5. Configure a API Key no arquivo `.env` (veja próxima seção)

### Criar arquivo .env

Crie um arquivo `.env` na raiz do projeto com o seguinte conteúdo:

```env
GOOGLE_PLACES_API_KEY=sua_api_key_aqui
```

> ⚠️ **Importante**: Nunca commite o arquivo `.env` com sua API Key real. O arquivo já está no `.gitignore`.

## 🐳 Como Executar com Docker (Recomendado)

### Opção 1: Docker Compose (Mais Simples)

Execute o comando abaixo para subir a aplicação e o banco de dados:

```bash
docker-compose up -d
```

Para parar os containers:

```bash
docker-compose down
```

### Opção 2: Docker Build Manual

```bash
# Build da imagem
docker build -t hotel-guessr:latest .

# Executar o container (certifique-se de ter o PostgreSQL rodando)
docker run -p 8080:8080 \
  -e GOOGLE_PLACES_API_KEY=sua_api_key_aqui \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/hotel_guessr_db \
  hotel-guessr:latest
```

## 💻 Como Executar Localmente (Sem Docker)

### 1. Configurar o Banco de Dados PostgreSQL

Certifique-se de ter um PostgreSQL rodando com as seguintes configurações:

```
Host: localhost
Porta: 5432
Database: hotel_guessr_db
Usuário: postgres
Senha: admin
```

Ou altere as configurações em `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hotel_guessr_db
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

### 2. Configurar a API Key

Defina a variável de ambiente:

**Windows (PowerShell):**
```powershell
$env:GOOGLE_PLACES_API_KEY="sua_api_key_aqui"
```

**Linux/Mac:**
```bash
export GOOGLE_PLACES_API_KEY="sua_api_key_aqui"
```

### 3. Executar a Aplicação

```bash
# Compilar o projeto
./mvnw clean package

# Executar a aplicação
./mvnw spring-boot:run
```

Ou com o JAR compilado:

```bash
java -jar target/hotel-guessr-0.0.1-SNAPSHOT.jar
```

## 📚 Documentação da API (Swagger)

Após iniciar a aplicação, acesse a documentação interativa do Swagger:

**Interface Visual (Swagger UI):**
```
http://localhost:8080/swagger-ui/index.html
```

ou

```
http://localhost:8080/swagger-ui.html
```

**Documentação JSON (OpenAPI 3.0):**
```
http://localhost:8080/v3/api-docs
```

A documentação Swagger permite:
- ✅ Visualizar todos os endpoints disponíveis
- ✅ Testar as requisições diretamente pelo navegador
- ✅ Ver exemplos de requisições e respostas
- ✅ Consultar os schemas dos objetos (DTOs)
- ✅ Verificar os códigos de status HTTP possíveis

## 🔗 Endpoints Disponíveis

### 1. Consultar CEP

**GET** `/api/cep/{cep}`

Consulta informações de endereço a partir de um CEP.

**Exemplo de Requisição:**
```bash
curl -X GET "http://localhost:8080/api/cep/01310100"
```

**Exemplo de Resposta:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "cep": "01310100",
  "logradouro": "Avenida Paulista",
  "complemento": "lado ímpar",
  "bairro": "Bela Vista",
  "localidade": "São Paulo",
  "uf": "SP",
  "ddd": "11",
  "dataConsulta": "2024-01-15T10:30:45"
}
```

### 2. Buscar Hotéis Próximos

**GET** `/api/hoteis/proximos/{cep}?limite=5`

Busca hotéis próximos a um CEP específico.

**Parâmetros:**
- `cep` (path) - CEP de referência
- `limite` (query) - Quantidade máxima de hotéis a retornar (padrão: 5)

**Exemplo de Requisição:**
```bash
curl -X GET "http://localhost:8080/api/hoteis/proximos/01310100?limite=5"
```

**Exemplo de Resposta:**
```json
{
  "cepConsultado": "01310100",
  "enderecoConsultado": "Avenida Paulista, 1578 - Bela Vista",
  "cidade": "São Paulo",
  "uf": "SP",
  "hoteis": [
    {
      "nome": "Hotel Renaissance São Paulo",
      "endereco": "Alameda Santos, 2233 - Jardim Paulista",
      "estrelas": 4,
      "descricao": "Hotel de luxo com vista panorâmica da cidade",
      "distanciaKm": 1.2
    }
  ],
  "totalEncontrado": 5
}
```

## 🧪 Executar Testes

```bash
# Rodar todos os testes
./mvnw test

# Rodar com cobertura
./mvnw test jacoco:report
```

Os testes incluem:
- ✅ Testes unitários dos serviços
- ✅ Mocks de APIs externas
- ✅ Validação de exceções
- ✅ Cobertura de até 100% na maioria dos serviços principais

## 📊 Estrutura do Projeto

```
hotel-guessr/
├── src/
│   ├── main/
│   │   ├── java/com/hotel/guessr/
│   │   │   ├── config/          # Configurações (OpenAPI, RestTemplate)
│   │   │   ├── controller/      # Controllers e interfaces Swagger
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── exception/       # Exceções customizadas e handlers
│   │   │   ├── model/           # Entidades JPA
│   │   │   ├── repository/      # Repositórios JPA
│   │   │   └── service/         # Lógica de negócio
│   │   └── resources/
│   │       └── application.properties
│   └── test/                    # Testes unitários
├── docker-compose.yml           # Configuração Docker Compose
├── Dockerfile                   # Imagem Docker da aplicação
├── pom.xml                      # Dependências Maven
└── README.md
```

## 🐛 Tratamento de Erros

A API retorna respostas padronizadas em caso de erro:

```json
{
  "apierro": {
    "timestamp": "2024-01-15T10:30:45",
    "status": "NOT_FOUND",
    "codigoErro": 404,
    "mensagemDetalhada": "CEP não encontrado na base de dados"
  }
}
```

**Códigos de erro possíveis:**
- `400 Bad Request` - CEP inválido ou parâmetros incorretos
- `404 Not Found` - CEP ou recurso não encontrado
- `500 Internal Server Error` - Erro ao consultar APIs externas ou erro interno

## 📝 Variáveis de Ambiente

| Variável | Descrição | Padrão | Obrigatório |
|----------|-----------|--------|-------------|
| `GOOGLE_PLACES_API_KEY` | API Key do Google Cloud Platform | - | ✅ Sim |
| `SPRING_DATASOURCE_URL` | URL do banco PostgreSQL | `jdbc:postgresql://localhost:5432/hotel_guessr_db` | Não |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco | `postgres` | Não |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco | `admin` | Não |

## 🤝 Contribuindo

Este é um projeto de desafio técnico, mas sugestões são bem-vindas!

## 📄 Licença

Este projeto foi desenvolvido como parte de um desafio técnico.

## 👨‍💻 Autor

Desenvolvido por **Gabriel Oliveira Quintanilha**

---

⭐ Se este projeto foi útil para você, considere dar uma estrela no repositório!
