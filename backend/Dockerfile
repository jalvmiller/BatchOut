# BUILD da aplicação, Maven + JDK21
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar arquivo de dependências pom.xml primeiro
COPY pom.xml ./

# Baixar dependências em modo offline e salvar no cache do Docker
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -B

# Copiar os fontes e compilar
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -DskipTests package

# Rodar com uma imagem JDK para permitir compilação local no desenvolvimento
# COPY puxa todos os arquivos da pasta backend pra /app
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/sistemasquestoes-0.0.1-SNAPSHOT.jar app.jar

# A aplicação roda nessa porta
EXPOSE 8080

CMD ["java", "-Dserver.port=${PORT:8080}", "-jar", "app.jar"]