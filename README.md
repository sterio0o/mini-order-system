Микросервисный интернет-магазин на Spring Boot + Kafka + JWT.


**Рабочий процесс**
1. Пользователь регистрируется -> получает JWT токен
2. Пользователь создает заказ -> OrderService → Kafka
3. PaymentService получает событие -> обрабатывает платеж
4. NotificationService получает событие -> отправляет email

# Сервисы и порты
| Сервис               | Порт | Что делает                                      |
|----------------------|------|-------------------------------------------------|
| User Service         | 8080 | Регистрация, логин, JWT токены, веб-страницы    |
| Order Service        | 8081 | Создание и просмотр заказов                     |
| Payment Service      | 8082 | Обработка платежей                              |
| Notification Service | 8083 | Отправка email уведомлений                      |
| PostgreSQL           |  -   | Каждый сервис имеет свою БД                     |
| Kafka                | 9092 | Обмен событиями между сервисами                 |

## Базы данных

Каждый сервис имеет свою собственную PostgreSQL БД:

| Сервис               | Host                     | Порт | Database            |
|----------------------|--------------------------|------|---------------------|
| User Service         | postgres-user            | 5432 | userdb              |
| Order Service        | postgres-order           | 5433 | orderdb             |
| Payment Service      | postgres-payment         | 5434 | paymentdb           |
| Notification Service | postgres-notification    | 5435 | notificationdb      |

**Технологии**
  * Java 21
  * Spring Boot 4.3
  * Spring Security + JWT
  * PostgreSQL
  * Apache Kafka
  * Docker / Docker Compose
