# 🐘 Guia Docker PostgreSQL - hotel-guessr

## 📋 Pré-requisitos

1. **Docker Desktop instalado** no Windows
   - Baixe em: https://www.docker.com/products/docker-desktop
   - Instale e reinicie o computador se necessário

2. **Verificar se Docker está rodando**
```powershell
docker --version
docker-compose --version
```

---

## 🚀 Passo a Passo para Subir o PostgreSQL

### 1️⃣ Abrir PowerShell no diretório do projeto
```powershell
cd "C:\Users\golivei1\Projetos Pessoais\hotel-guessr"
```

### 2️⃣ Subir o PostgreSQL com Docker Compose
```powershell
docker-compose up -d
```

**O que esse comando faz:**
- `-d`: Executa em background (detached mode)
- Baixa a imagem do PostgreSQL 15 Alpine (se ainda não tiver)
- Cria o container `hotel-guessr-postgres`
- Cria o banco `hotel-guessr_db`
- Expõe a porta `5432`

### 3️⃣ Verificar se o container está rodando
```powershell
docker-compose ps
```

Você deve ver algo como:
```
NAME                      STATUS    PORTS
hotel-guessr-postgres     Up        0.0.0.0:5432->5432/tcp
```

### 4️⃣ Ver logs do PostgreSQL (opcional)
```powershell
docker-compose logs postgres
```

### 5️⃣ Compilar e rodar a aplicação
```powershell
# Compilar
./mvnw clean install -s settings.xml

# Executar
./mvnw spring-boot:run -s settings.xml
```

A aplicação irá:
- Conectar no PostgreSQL em `localhost:5432`
- Usar o banco `hotel-guessr_db`
- Criar automaticamente a tabela `consultas_cep`

---

## 🔍 Comandos Úteis do Docker

### Ver containers rodando
```powershell
docker ps
```

### Ver todos os containers (inclusive parados)
```powershell
docker ps -a
```

### Parar o PostgreSQL
```powershell
docker-compose stop
```

### Iniciar novamente (se já existe)
```powershell
docker-compose start
```

### Parar e remover container (mantém os dados no volume)
```powershell
docker-compose down
```

### Remover TUDO (inclusive dados do banco)
```powershell
docker-compose down -v
```

### Ver logs em tempo real
```powershell
docker-compose logs -f postgres
```

---

## 🗃️ Acessar o PostgreSQL Diretamente

### Opção 1: Via Docker Exec
```powershell
docker exec -it hotel-guessr-postgres psql -U postgres -d hotel-guessr_db
```

Dentro do PostgreSQL:
```sql
-- Listar tabelas
\dt

-- Ver estrutura da tabela
\d consultas_cep

-- Consultar dados
SELECT * FROM consultas_cep;

-- Sair
\q
```

### Opção 2: Via DBeaver ou pgAdmin
- **Host:** localhost
- **Port:** 5432
- **Database:** hotel-guessr_db
- **Username:** postgres
- **Password:** postgres

---

## 🔧 Solução de Problemas

### Porta 5432 já está em uso
```powershell
# Ver o que está usando a porta
Get-Process -Id (Get-NetTCPConnection -LocalPort 5432).OwningProcess

# Parar o processo ou alterar a porta no docker-compose.yml
# Editar: ports: - "5433:5432"
```

### Container não inicia
```powershell
# Ver logs detalhados
docker-compose logs postgres

# Remover e recriar
docker-compose down -v
docker-compose up -d
```

### Erro de conexão da aplicação
- Verifique se o container está rodando: `docker-compose ps`
- Verifique as credenciais em `application.properties`
- Aguarde alguns segundos após subir o container (healthcheck)

---

## 📊 Dados Persistentes

Os dados do PostgreSQL são salvos em um **volume Docker** chamado `postgres_data`.

**Isso significa:**
- ✅ Dados persistem mesmo após `docker-compose down`
- ✅ Sobrevivem a reinicializações do Docker
- ❌ Somente são apagados com `docker-compose down -v`

### Ver volumes
```powershell
docker volume ls
```

### Remover volume específico (APAGA TODOS OS DADOS!)
```powershell
docker volume rm hotel-guessr_postgres_data
```

---

## ✅ Checklist Rápido

- [ ] Docker Desktop instalado e rodando
- [ ] Abrir PowerShell no diretório do projeto
- [ ] Executar: `docker-compose up -d`
- [ ] Verificar: `docker-compose ps` (deve aparecer "Up")
- [ ] Compilar: `./mvnw clean install -s settings.xml`
- [ ] Rodar: `./mvnw spring-boot:run -s settings.xml`
- [ ] Testar: `http://localhost:8080/api/cep/01310100`

---

## 🎯 Resumo dos Comandos Principais

```powershell
# Subir PostgreSQL
docker-compose up -d

# Ver status
docker-compose ps

# Parar
docker-compose stop

# Reiniciar
docker-compose start

# Parar e remover (mantém dados)
docker-compose down

# Parar e remover TUDO (apaga dados)
docker-compose down -v

# Ver logs
docker-compose logs -f postgres

# Acessar banco
docker exec -it hotel-guessr-postgres psql -U postgres -d hotel-guessr_db
```

---

## 📝 Configurações do Banco

```
Host: localhost
Port: 5432
Database: hotel-guessr_db
Username: postgres
Password: postgres
```

---

✅ **Pronto! Seu PostgreSQL está rodando no Docker e a aplicação pode se conectar!**
