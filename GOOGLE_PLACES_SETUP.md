# Configuração Google Places API

## 🔑 Como obter sua API Key

1. Acesse [Google Cloud Console](https://console.cloud.google.com/)
2. Crie um novo projeto (ou selecione existente)
3. Ative a **Places API**:
   - Menu → APIs & Services → Library
   - Busque por "Places API"
   - Clique em "Enable"

4. Crie credenciais:
   - Menu → APIs & Services → Credentials
   - Clique em "Create Credentials" → "API Key"
   - Copie a chave gerada

## ⚙️ Configurar no Projeto

### **Opção 1: Variável de Ambiente (Recomendado)**

```bash
# Windows PowerShell
$env:GOOGLE_PLACES_API_KEY="sua-chave-aqui"

# Windows CMD
set GOOGLE_PLACES_API_KEY=sua-chave-aqui

# Linux/Mac
export GOOGLE_PLACES_API_KEY=sua-chave-aqui
```

### **Opção 2: application.properties**

```properties
google.places.api.key=AIzaSyABC123DEF456GHI789JKL012MNO345PQR
```

⚠️ **NUNCA commite a chave no Git!**

## 💰 Custos

- **Grátis**: 50.000 chamadas/mês
- **Após limite**: $17 por 1.000 chamadas adicionais
- **Requer**: Cartão de crédito cadastrado

## 🧪 Testando

```bash
# Com variável de ambiente configurada
mvn spring-boot:run -s settings.xml

# Testar endpoint
curl http://localhost:8080/api/hoteis/proximos/01310100?limite=5
```

## 📊 Response Esperado

```json
{
  "cepConsultado": "01310-100",
  "enderecoConsultado": "Avenida Paulista, Bela Vista",
  "cidade": "São Paulo",
  "uf": "SP",
  "hoteis": [
    {
      "nome": "Hotel Real Parque",
      "endereco": "Alameda Santos, 85",
      "estrelas": 4,
      "descricao": "Avaliação: 4.2 (1523 avaliações)",
      "distanciaKm": 0.82
    }
  ],
  "totalEncontrado": 5
}
```

## 🔒 Segurança

Para produção, restrinja a API Key:

1. Google Cloud Console → Credentials
2. Edite sua API Key
3. Application restrictions → HTTP referrers
4. Adicione seu domínio: `https://seusite.com/*`

## ⚡ Limites e Otimizações

- **Raio padrão**: 5000 metros (5 km)
- **Tipo**: `lodging` (hotéis e pousadas)
- **Cache**: Considere implementar para reduzir custas
- **Rate Limit**: Máximo 100 req/segundo

## 🆓 Alternativa Gratuita

Se não quiser usar cartão de crédito, o mock anterior funciona perfeitamente para demonstração!
