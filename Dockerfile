# Этап сборки
FROM gradle:8.6.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# Этап запуска
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# Открываем порт
EXPOSE 8080

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"] 