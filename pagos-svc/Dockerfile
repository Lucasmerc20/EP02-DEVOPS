# Etapa 1: compila el jar (las pruebas corren en el pipeline)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# Etapa 2: imagen liviana solo con el JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# usuario sin privilegios
RUN addgroup -S pagos && adduser -S pagos -G pagos
COPY --from=build --chown=pagos:pagos /app/target/*.jar app.jar
USER pagos

# la JVM respeta el limite de memoria del contenedor
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
