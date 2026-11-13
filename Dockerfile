# Etapa 1: Compilacion y empaquetado con Maven y Java 21
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Cachear dependencias primero para acelerar reconstrucciones cuando solo cambie el codigo
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar codigo fuente y generar el JAR ejecutable
COPY src ./src
RUN mvn clean package -DskipTests -B

# Etapa 2: Imagen final liviana solo con JRE 21 (sin Maven ni codigo fuente)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Ejecutar con un usuario sin privilegios root por seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /app/target/franchise-api-*.jar app.jar
RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

