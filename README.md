# Globus Backend

Backend-сервис для проекта Globus.

## Содержание
- [Требования](#требования)
- [Технологии](#технологии)
- [Архитектура](#архитектура)
- [Запуск проекта](#запуск-проекта)
- [API Документация](#api-документация)
- [База данных](#база-данных)
- [Разработка](#разработка)

## Требования
- Docker 20.10+
- Docker Compose 2.0+
- JDK 17
- Gradle 8.6+

## Технологии
- **Язык программирования**: Java
- **Фреймворк**: Spring Boot 3.x
- **База данных**: PostgreSQL 17.4
- **ORM**: Hibernate
- **API Документация**: OpenAPI/Swagger
- **Сборка**: Gradle
- **Контейнеризация**: Docker, Docker Compose
- **CI/CD**: GitHub Actions

## Архитектура
Проект построен по принципам чистой архитектуры (Clean Architecture) и следует паттернам:
- Repository Pattern
- Service Layer Pattern
- DTO Pattern
- REST API

### Структура проекта
```
src/
├── main/
│   ├── java/
│   │   ├── config/        # Конфигурации приложения
│   │   ├── controller/    # REST контроллеры
│   │   ├── dto/          # Data Transfer Objects
│   │   ├── entity/       # Сущности базы данных
│   │   ├── repository/   # Репозитории
│   │   └── service/      # Бизнес-логика
│   └── resources/
│       ├── application.yml # Конфигурация приложения
│       └── db/            # Миграции базы данных
├── test/                  # Тесты
└── build.gradle.kts       # Конфигурация сборки
```

## Запуск проекта

### Локальная разработка
1. Клонируйте репозиторий:
```bash
git clone [url-репозитория]
cd globus-backend
```

2. Запустите проект с помощью Docker Compose:
```bash
docker-compose up --build
```

Сервис будет доступен по адресу: `http://localhost:8080`

### Переменные окружения
Основные переменные окружения:
- `SPRING_DATASOURCE_URL`: URL базы данных
- `SPRING_DATASOURCE_USERNAME`: Пользователь БД
- `SPRING_DATASOURCE_PASSWORD`: Пароль БД

## API Документация
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI спецификация: http://localhost:8080/v3/api-docs

## База данных
### Конфигурация
- Хост: localhost
- Порт: 5432
- Пользователь: user
- Пароль: password
- База данных: postgres

### Миграции
Миграции базы данных управляются через Liquibase и находятся в директории `src/main/resources/db`

## Разработка
### Сборка проекта
```bash
./gradlew build
```

### Запуск тестов
```bash
./gradlew test
```

### Непрерывная интеграция (CI)
Проект использует GitHub Actions для автоматизации процессов разработки:
- Автоматическая проверка кода при каждом пуше в ветку
- Запуск тестов при каждом пуше в ветку
- Сборка проекта при каждом пуше в ветку

CI конфигурация находится в директории `.github/workflows`

## Остановка проекта
Для остановки всех сервисов:
```bash
docker-compose down
```

Для остановки и удаления всех данных (включая базу данных):
```bash
docker-compose down -v
```