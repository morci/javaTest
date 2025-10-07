# ----------------------------------------------------
# STAGE 1: BUILD
# ----------------------------------------------------
FROM maven:3.9.5-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# ----------------------------------------------------
# STAGE 2: RUNTIME
# ----------------------------------------------------
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S appuser && adduser -S appuser -G appuser
USER appuser

EXPOSE 8080

ARG JAR_FILE=/app/target/*.jar
COPY --from=build ${JAR_FILE} app.jar

# Comando de entrada
ENTRYPOINT ["java", "-Dspring.h2.console.settings.web-allow-others=true","-Xmx256m", "-jar", "/app.jar"]
