# 🏨 Sistema de Busca de Hotéis Próximos

## 📋 Visão Geral

O sistema permite encontrar hotéis próximos a partir de um CEP informado, calculando a distância real entre o endereço consultado e cada hotel cadastrado no banco de dados.

---

## 🔄 Fluxo de Funcionamento

### **1. Validação e Consulta do CEP**

```java
var endereco = cepService.consultarCep(cep);
```

**O que acontece:**
- Valida formato do CEP (8 dígitos, aceita com ou sem hífen)
- Consulta API externa ViaCEP: `https://viacep.com.br/ws/{cep}/json/`
- Retorna: logradouro, bairro, cidade, UF
- Salva consulta no banco de dados (tabela `consultas_cep`) para histórico
- Lança exceção se CEP inválido ou não encontrado

**Exemplo:**
```
Input: 01310100
Output: Avenida Paulista, Bela Vista, São Paulo, SP
```

---

### **2. Obtenção de Coordenadas Geográficas**

```java
CoordenadasResponse coordenadas = geolocalizacaoService.obterCoordenadasPorCep(cep);
```

**O que acontece:**
- Sistema atual usa **simulação** com coordenadas aproximadas
- Baseado no UF (estado), retorna coordenadas da capital
- Em produção, seria substituído por API de geocoding real

**Mapeamento Atual:**

| Estado | Capital        | Latitude  | Longitude  |
|--------|----------------|-----------|------------|
| SP     | São Paulo      | -23.5505  | -46.6333   |
| RJ     | Rio de Janeiro | -22.9068  | -43.1729   |
| MG     | Belo Horizonte | -19.9167  | -43.9345   |
| BA     | Salvador       | -12.9714  | -38.5014   |
| PR     | Curitiba       | -25.4284  | -49.2733   |
| Outros | Brasília       | -15.7801  | -47.9292   |

**Lógica de identificação do estado:**
```java
if (cep.startsWith("01") || cep.startsWith("02") || cep.startsWith("03")) {
    return "SP";  // São Paulo
} else if (cep.startsWith("20") || cep.startsWith("21")) {
    return "RJ";  // Rio de Janeiro
}
// ... demais estados
```

---

### **3. Busca com Fórmula de Haversine**

```java
List<Hotel> hoteis = hotelRepository.findHoteisProximosPorCoordenadas(
    coordenadas.getLatitude(),
    coordenadas.getLongitude(),
    endereco.getUf(),
    limite
);
```

**Query SQL executada:**

```sql
SELECT * FROM hoteis h
WHERE h.uf = :uf
ORDER BY (
    6371 * acos(
        cos(radians(:latitude)) * 
        cos(radians(h.latitude)) * 
        cos(radians(h.longitude) - radians(:longitude)) + 
        sin(radians(:latitude)) * 
        sin(radians(h.latitude))
    )
) ASC
LIMIT :limite
```

#### **📐 Entendendo a Fórmula de Haversine**

A fórmula calcula a **distância em linha reta** entre dois pontos na superfície da Terra, considerando sua curvatura.

**Componentes:**
- `6371` = Raio médio da Terra em quilômetros
- `radians()` = Converte graus para radianos
- `acos()` = Arco cosseno
- `sin()` / `cos()` = Funções trigonométricas

**Passo a passo:**
1. Converte latitude e longitude para radianos
2. Calcula diferença angular entre os pontos
3. Aplica fórmula esférica
4. Multiplica pelo raio da Terra para obter distância em Km

**Filtros aplicados:**
- Busca apenas hotéis no mesmo **estado (UF)**
- Ordena do **mais próximo** ao **mais distante**
- Limita quantidade de resultados (padrão: 5)

---

### **4. Cálculo Final de Distância**

```java
Double distancia = calcularDistancia(
    coordenadas.getLatitude(),
    coordenadas.getLongitude(),
    hotel.getLatitude(),
    hotel.getLongitude()
);
```

**Implementação Java da Haversine:**

```java
private Double calcularDistancia(Double lat1, Double lon1, Double lat2, Double lon2) {
    final int RAIO_TERRA_KM = 6371;
    
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLon / 2) * Math.sin(dLon / 2);
    
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    
    return RAIO_TERRA_KM * c;  // Retorna distância em Km
}
```

**Formatação:**
- Arredonda para **2 casas decimais**
- Exemplo: `Math.round(distancia * 100.0) / 100.0` → `1.45 Km`

---

## 🎯 Exemplo Completo

### **Request:**
```http
GET /api/hoteis/proximos/01310100?limite=3
```

### **Processamento:**

1. **CEP validado:** `01310-100`
2. **Endereço obtido:** Avenida Paulista, Bela Vista, São Paulo, SP
3. **Coordenadas aproximadas:** `-23.5505, -46.6333` (São Paulo)
4. **Query executa:** Busca 3 hotéis mais próximos em SP
5. **Hotéis encontrados e ordenados por distância**

### **Response:**

```json
{
  "cepConsultado": "01310-100",
  "enderecoConsultado": "Avenida Paulista, Bela Vista",
  "cidade": "São Paulo",
  "uf": "SP",
  "hoteis": [
    {
      "id": 1,
      "nome": "Hotel hotel-guessr Grand",
      "endereco": "Av. Paulista, 1000",
      "cidade": "São Paulo",
      "uf": "SP",
      "cep": "01310-100",
      "estrelas": 5,
      "descricao": "Hotel 5 estrelas no coração da Paulista",
      "distanciaKm": 0.45
    },
    {
      "id": 2,
      "nome": "hotel-guessr Business Hotel",
      "endereco": "Av. Paulista, 1500",
      "cidade": "São Paulo",
      "uf": "SP",
      "cep": "01310-200",
      "estrelas": 4,
      "descricao": "Hotel corporativo moderno",
      "distanciaKm": 0.89
    },
    {
      "id": 3,
      "nome": "hotel-guessr Park Hotel",
      "endereco": "Rua Augusta, 200",
      "cidade": "São Paulo",
      "uf": "SP",
      "cep": "01311-000",
      "estrelas": 4,
      "descricao": "Hotel com vista para o parque",
      "distanciaKm": 1.23
    }
  ],
  "totalEncontrado": 3
}
```

---

## 🏗️ Arquitetura (SOLID)

### **Interfaces (Dependency Inversion)**
- `CepServiceInterface` → Consulta de CEP
- `GeolocalizacaoServiceInterface` → Obtenção de coordenadas
- `HotelServiceInterface` → Busca de hotéis

### **Serviços (Single Responsibility)**
- `CepService` → Valida CEP, consulta ViaCEP, persiste histórico
- `GeolocalizacaoService` → Converte CEP em coordenadas
- `HotelService` → Orquestra busca, calcula distâncias

### **Controller (Interface Segregation)**
- `HotelController` → Endpoint REST `/api/hoteis/proximos/{cep}`

### **Repository**
- `HotelRepository` → Query nativa com Haversine no PostgreSQL

---

## 📊 Modelo de Dados

### **Tabela: hoteis**

| Campo      | Tipo         | Descrição                    |
|------------|--------------|------------------------------|
| id         | BIGINT       | Chave primária               |
| nome       | VARCHAR(200) | Nome do hotel                |
| cep        | VARCHAR(9)   | CEP do hotel                 |
| endereco   | VARCHAR      | Endereço completo            |
| cidade     | VARCHAR(100) | Cidade                       |
| uf         | VARCHAR(2)   | Estado (UF)                  |
| latitude   | DOUBLE       | Coordenada geográfica        |
| longitude  | DOUBLE       | Coordenada geográfica        |
| descricao  | TEXT         | Descrição do hotel           |
| estrelas   | INTEGER      | Classificação (1-5 estrelas) |

### **Dados Mockados (data.sql)**

O sistema carrega automaticamente hotéis de exemplo em:
- **São Paulo (SP):** 5 hotéis
- **Rio de Janeiro (RJ):** 3 hotéis
- **Belo Horizonte (MG):** 2 hotéis

---

## ⚠️ Limitações Atuais

### **1. Coordenadas Aproximadas**
- Usa coordenadas da capital do estado
- Não é a localização exata do CEP
- **Solução futura:** Integrar com API de geocoding (Google Maps, OpenCage, etc.)

### **2. Dados Mockados**
- Hotéis são inseridos via script SQL
- Quantidade limitada de hotéis cadastrados
- **Solução futura:** Sistema de cadastro de hotéis

### **3. Busca Limitada ao Estado**
- Query filtra apenas por UF
- Não busca em estados vizinhos
- **Possível melhoria:** Remover filtro de UF e buscar por raio (ex: 50 Km)

---

## 🚀 Possíveis Melhorias

### **1. Geocoding Real**
```java
// Exemplo com API externa
public CoordenadasResponse obterCoordenadasPorCep(String cep) {
    String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + cep;
    // ... chamada REST e parsing
}
```

### **2. Cache de Coordenadas**
```java
@Cacheable("coordenadas")
public CoordenadasResponse obterCoordenadasPorCep(String cep) {
    // Evita consultas repetidas
}
```

### **3. Busca por Raio**
```sql
-- Buscar hotéis em um raio de 10 Km
HAVING distancia_km <= 10
ORDER BY distancia_km ASC
```

### **4. Filtros Adicionais**
- Classificação (estrelas)
- Faixa de preço
- Comodidades (Wi-Fi, piscina, etc.)

---

## 🧪 Testando a API

### **1. Buscar hotéis próximos (padrão 5 resultados)**
```bash
curl http://localhost:8080/api/hoteis/proximos/01310100
```

### **2. Buscar com limite personalizado**
```bash
curl http://localhost:8080/api/hoteis/proximos/01310100?limite=10
```

### **3. CEP de outro estado**
```bash
# Rio de Janeiro
curl http://localhost:8080/api/hoteis/proximos/22040020

# Minas Gerais
curl http://localhost:8080/api/hoteis/proximos/30130100
```

---

## 🔧 Tecnologias Utilizadas

- **Spring Boot 3.4.0** - Framework
- **PostgreSQL 15** - Banco de dados
- **Spring Data JPA** - ORM
- **Lombok** - Redução de boilerplate
- **ViaCEP API** - Consulta de CEP
- **Fórmula de Haversine** - Cálculo de distância geográfica

---

## 📝 Tratamento de Erros

### **CEP Inválido (400 Bad Request)**
```json
{
  "apierro": {
    "timestamp": "2025-11-26T17:45:00",
    "status": 400,
    "codigoErro": "BAD_REQUEST",
    "mensagemDetalhada": "CEP 1234 está em formato inválido. Use formato: 00000000 ou 00000-000"
  }
}
```

### **CEP Não Encontrado (404 Not Found)**
```json
{
  "apierro": {
    "timestamp": "2025-11-26T17:45:00",
    "status": 404,
    "codigoErro": "NOT_FOUND",
    "mensagemDetalhada": "CEP 99999999 não encontrado"
  }
}
```

### **Nenhum Hotel Encontrado (404 Not Found)**
```json
{
  "apierro": {
    "timestamp": "2025-11-26T17:45:00",
    "status": 404,
    "codigoErro": "NOT_FOUND",
    "mensagemDetalhada": "Nenhum hotel encontrado para o estado: AC"
  }
}
```

### **Erro no Serviço Externo (503 Service Unavailable)**
```json
{
  "apierro": {
    "timestamp": "2025-11-26T17:45:00",
    "status": 503,
    "codigoErro": "SERVICE_UNAVAILABLE",
    "mensagemDetalhada": "Serviço de consulta de CEP temporariamente indisponível"
  }
}
```

---

## 📚 Referências

- [Fórmula de Haversine](https://en.wikipedia.org/wiki/Haversine_formula)
- [ViaCEP API](https://viacep.com.br/)
- [PostgreSQL Geometric Functions](https://www.postgresql.org/docs/current/functions-geometry.html)
- [Spring Data JPA Native Queries](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.at-query)
