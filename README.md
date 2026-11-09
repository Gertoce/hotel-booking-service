# 🏨 Microservice Hotel Booking System

Production-lite микросервисная система бронирования отелей, разработанная на **Java 17**, **Spring Boot 3.5.0** и **Spring Cloud (2023.0.3)** в едином многомодульном Maven-репозитории (Monorepo).

---

## 🏗 Архитектура и стек технологий

Проект разделен на независимые микросервисы, взаимодействующие друг с другом через Service Discovery и API Gateway с поддержкой распределенных транзакций (паттерн **Saga**).

* **Java 17+** / **Spring Boot 3.3.x**
* **Spring Cloud (Eureka Server & Netflix Eureka Client)** — сервис-дискавери и регистрация.
* **Spring Cloud Gateway** — единая точка входа и динамическая маршрутизация запросов.
* **Spring Data JPA & H2 Database** — реляционная БД для каждого сервиса в памяти.
* **Spring Security & JJWT (JSON Web Token)** — аутентификация и авторизация (токены со сроком жизни 1 час).
* **RestClient** — декларативный HTTP-клиент для межсервисного взаимодействия с балансировкой нагрузки (`@LoadBalanced`).
* **Springdoc OpenAPI (Swagger UI)** — интерактивная документация REST API.

---

## 📂 Структура проекта

```text
hotel-booking-system/
├── pom.xml                     # Корневой Maven-агрегатор
├── eureka-server/              # Реестр микросервисов (Порт: 8761)
├── api-gateway/                # API Gateway (Порт: 8080)
├── hotel-management-service/   # Управление отелями, номерами и доступностью (Порт: 8081)
└── booking-service/            # Управление пользователями, JWT и бронированиями (Порт: 8082)