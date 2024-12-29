FROM eclipse-temurin:23 AS builder
LABEL authors="joyie"

WORKDIR /app

COPY . .

RUN ./mvnw install -DskipTests

FROM eclipse-temurin:23

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

ENV PORT=8080

EXPOSE ${PORT}

ENTRYPOINT SERVER_PORT=${PORT} java -jar /app/app.jar -Dserver.port=${PORT}