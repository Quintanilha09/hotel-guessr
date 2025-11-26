FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copiar arquivos de configuração do Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Baixar dependências
RUN ./mvnw dependency:go-offline -B

# Copiar código fonte
COPY src src

# Build da aplicação
RUN ./mvnw clean package -DskipTests

# Etapa de execução
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar o JAR construído
COPY --from=build /app/target/*.jar app.jar

# Expor porta
EXPOSE 8080

# Variáveis de ambiente
ENV JAVA_OPTS=""

# Comando de execução
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
