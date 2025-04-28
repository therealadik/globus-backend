# Этап сборки
FROM gradle:8.11.1-jdk17 AS builder
WORKDIR /app

COPY . .

RUN gradle bootJar --no-daemon --warning-mode=none

# Этап запуска
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/service.jar ./service.jar

# Запуск приложения
ENTRYPOINT ["java", "-jar", "service.jar"]