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
```
## 🚀 Как запустить систему
Для корректной работы микросервисов запускай их в следующей последовательности:

Eureka Server (EurekaServerApplication) — порт 8761

Hotel Management Service (HotelManagementServiceApplication) — порт 8081

Booking Service (BookingServiceApplication) — порт 8082

API Gateway (ApiGatewayApplication) — порт 8080

После запуска вы можете проверить статус всех сервисов в панели Eureka: http://localhost:8761.

## 📡 Поддерживаемые эндпоинты
Все запросы клиентов маршрутизируются через API Gateway (http://localhost:8080).

1. Пользователи и Безопасность (Booking Service)
   POST /api/user/register — Регистрация нового пользователя.

POST /api/user/auth — Аутентификация и получение JWT-токена.

2. Бронирование (Booking Service — Требует JWT)
   POST /api/bookings — Создание бронирования (Запуск распределенной транзакции Saga).

3. Отели и Номера (Hotel Management Service)
   GET /api/hotels — Получить список всех отелей.

POST /api/hotels — Добавить новый отель (Администратор).

POST /api/hotels/{hotelId}/rooms — Добавить номер в отель.

GET /api/rooms — Получить свободные номера (поддерживает сортировку по популярности times_booked для балансировки нагрузки).

## 🧪 Как протестировать код (Сквозной сценарий)
Проверить работу системы можно с помощью Postman, cURL или встроенного Swagger UI.

Шаг 1: Регистрация и получение токена
Отправьте запрос на регистрацию пользователя:

curl -X POST http://localhost:8080/api/user/register \
-H "Content-Type: application/json" \
-d '{"username": "testuser", "password": "password123"}'

Затем авторизуйтесь, чтобы получить JWT-токен:
curl -X POST http://localhost:8080/api/user/auth \
-H "Content-Type: application/json" \
-d '{"username": "testuser", "password": "password123"}'

Скопируйте полученную строку токена.

Шаг 2: Создание отеля и номера (через прямой порт или через шлюз)
Создайте отель в Hotel Service:
curl -X POST http://localhost:8081/api/hotels \
-H "Content-Type: application/json" \
-d '{"name": "Grand Hotel", "city": "Moscow", "address": "Tverskaya 1"}'

Добавьте свободный номер к созданному отелю (hotelId: 1):
curl -X POST http://localhost:8081/api/hotels/1/rooms \
-H "Content-Type: application/json" \
-d '{"roomNumber": "101", "type": "DELUXE", "price": 150.0}'

Шаг 3: Оформление брони (Saga Pattern)
Отправьте запрос на бронирование через API Gateway, передав в заголовке Authorization ваш JWT-токен:
curl -X POST http://localhost:8080/api/bookings \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <ВАШ_JWT_ТОКЕН>" \
-d '{"hotelId": 1, "roomId": 1}'

Что происходит под капотом:

Booking Service создает запись в статусе PENDING.

Посредством RestClient отправляется запрос в Hotel Service на блокировку номера (confirm-availability).

Если номер свободен, он блокируется, счетчик times_booked увеличивается, а бронь переходит в статус CONFIRMED.

Если номер занят или произошел сбой сети, срабатывает компенсация (Saga Compensation): бронь переводится в статус CANCELLED, а номер мгновенно освобождается.

## 📚 Документация API (Swagger UI)
Вы можете протестировать эндпоинты через интерактивную документацию Swagger:

Hotel Management Service: http://localhost:8081/swagger-ui/index.html

Booking Service: http://localhost:8082/swagger-ui/index.html