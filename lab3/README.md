# Lab3 — PostgreSQL + Hibernate

## Требования

- Docker Desktop
- Java 17+
- Maven 3.8+

## Технологии

- Hibernate ORM 6.6.46
- PostgreSQL JDBC 42.7.10
- PostgreSQL 18

## Быстрый старт

Запуск бд:

```bash
./start.sh
```

Остановка бд:

```bash
./stop.sh
```

Cброс данных:

```bash
./reset.sh
```

## Подключение к БД

### Консоль

```bash
docker exec -it lab3-postgres psql -U postgres -d lab3db
```

### GUI-клиент

| Параметр | Значение |
|----------|---------|
| Host | `localhost` |
| Port | `5432` |
| Database | `lab3db` |
| User | `postgres` |
| Password | `postgres` |

## Сборка и запуск приложения

```bash
mvn compile
mvn exec:java -Dexec.mainClass="com.example.App"
```
