# BdayLoop

Бэкенд для «BdayHub» - сервиса, который помогает не забывать про дни рождения друзей, дарить подарки вскладчину и обсуждать их в закрытом чате.

Проект идёт поэтапно - чистая java, spring + hibernate, spring boot. Этот README описывает **первый этап**.

## Этап 1 — чистая Java

### Стек

- **Java 26**, Maven
- **Embedded Jetty + Servlets** — HTTP-слой
- **Raw JDBC** — SQL написан вручную
- **PostgreSQL 16** — в Docker
- **Jackson** — (де)сериализация JSON
- **BCrypt** — хэширование паролей
- **HttpSession** — аутентификация по cookie-сессии
- **JUnit 5 + Testcontainers** — тесты DAO-слоя на Postgres в контейнере
- **GitHub Actions** — CI на каждый push и pull request
- Фронтенд — vanilla HTML/JS/fetch, без сборки, только для ручной проверки API

### Архитектура


```
Servlet → Service → DAO → PostgreSQL
```

- **DAO** — только SQL и проверки целостности
- **Service** — вся бизнес-логика и проверки прав («можно ли этому пользователю это делать»)
- **Servlet** — только перевод HTTP ↔ JSON, маршрутизация и коды ответов

### Доменная модель

`users`, `groups`, `group_members`, `group_subscriptions`, `subscriptions`, `gifts`, `messages` — полная схема в [`db/schema.sql`](db/schema.sql), применяется автоматически при первом поднятии контейнера.

### Функциональность

- Регистрация, вход/выход, роль `USER`/`ADMIN`
- Поиск пользователей — по имени, username или id
- Поиск групп - по названию
- Просмотр и редактирование профиля
- Группы: создание, вступление/выход, подписка, переименование, удаление
- Подарки: добавление, удаление, список по пользователю
- Чат про подарок — сообщения между подписчиком и именинником, доступ только после подписки
- Массовый импорт пользователей администратором

### Запуск

**1. База данных**

```bash
cp .env.example .env   # заполнить POSTGRES_DB / POSTGRES_USER / POSTGRES_PASSWORD
docker compose up -d
```

**2. Бэкенд**

Задать те же три переменные окружения в Run Configuration и запустить `Application.main()`. Сервер поднимается на `http://localhost:8080`.

**3. Фронтенд**

```bash
cd frontend
python3 -m http.server 8123
```

Открыть `http://localhost:8123/html/login.html`.

### Тесты и CI

```bash
mvn test
```

Тесты DAO-слоя поднимают отдельный Postgres в Docker через Testcontainers на время прогона. GitHub Actions гоняет тот же `mvn test` на каждый push и pull request.

### Осознанно отложено на этап 2 (Spring + Hibernate)

- Напоминания о днях рождения
- Смена пароля — сейчас можно поменять только имя, дату рождения и username
- Атомарность массового импорта — при ошибке в середине списка уже созданные пользователи остаются в базе
