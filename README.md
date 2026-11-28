# Сервис организации спортивных турниров
Приложение на **Spring Boot** (Java 17) для автоматизации проведения спортивных турниров:
управление пользователями (администраторы, менеджеры, тренеры, судьи, игроки), командами,
турнирами и матчами. Данные хранятся в **PostgreSQL** и доступны через **Spring Data JPA**,
снаружи система предоставляет **REST API** (CRUD + выборки с фильтрами).
* Лабораторная работа №1 — объектная модель, бизнес-логика, многопоточная обработка, unit-тесты.
* Лабораторная работа №2 — слой хранения (PostgreSQL + PgAdmin), Spring Data JPA, REST API,
  архитектура MVC, тесты Spring Boot без обращения к базе данных.
* Лабораторная работа №3 — собственные исключения, единый формат ошибок REST API,
  глобальная обработка ошибок, проверка ничьей и турнирная таблица с подсчётом очков.
* Лабораторная работа №4 — Spring Security (HTTP Basic, роли, BCrypt-хеширование паролей),
  единые ответы 401/403, логирование в консоль и файл через SLF4J и Logback,
  Swagger UI и OpenAPI-спецификация (springdoc-openapi).
---
## Что делает система
1. **Пользователи.** Абстрактный класс `User` и наследники по ролям: `AdminUser`,
   `ManagerUser`, `CoachUser`, `RefereeUser`, `PlayerUser` (+ роль `CAPTAIN`).
   Сервис проверяет уникальность email, умеет назначать игрока в команду
   и переключать роли `PLAYER` ↔ `CAPTAIN`.
   Пароль хранится только в виде BCrypt-хеша: открытый пароль в базе не сохраняется.
2. **Команды.** Название, город, тренер, капитан, состав игроков.
3. **Турниры.** Название, вид спорта, даты, призовой фонд, судья, список команд.
   Модель определяет, открыта ли регистрация; отдельный запрос строит таблицу по завершённым
   матчам, считает победы/ничьи/поражения и очки (3 за победу, 1 за ничью).
4. **Матчи.** Две команды, счёт в формате `X:Y`, статус (`SCHEDULED`, `IN_PROGRESS`,
   `FINISHED`, `CANCELLED`), определение победителя, генерация протокола матча и
   многопоточная (пакетная) обработка коллекции матчей. Ничья разрешена только в тех видах
   спорта, для которых это указано в `SportType`.
---
## Стек
| Технология | Назначение |
|---|---|
| Java 17, Spring Boot 4.1.1 | основа приложения |
| Spring Web MVC | REST API (модель MVC) |
| Spring Data JPA (Hibernate) | доступ к данным |
| PostgreSQL + PgAdmin | база данных (схема в PgAdmin) |
| Spring Security | аутентификация HTTP Basic и проверка ролей |
| BCrypt (`BCryptPasswordEncoder`) | хеширование паролей |
| SLF4J + Logback | журналирование в консоль и файл |
| springdoc-openapi 3.1.1 | Swagger UI и OpenAPI-спецификация |
| Lombok | генерация геттеров/сеттеров DTO |
| Jakarta Bean Validation | валидация входящих запросов |
| JUnit 5 + Mockito + spring-security-test | unit- и HTTP-тесты |
---
## Структура пакетов
```
com.tournament
├── TournamentServiceApplication      точка входа (@SpringBootApplication)
├── model                             модель предметной области (сущности JPA)
│   ├── enums                         перечисления UserRole, SportType, MatchStatus
│   ├── user                          User (abstract) + AdminUser, ManagerUser,
│   │                                 CoachUser, RefereeUser, PlayerUser
│   ├── team                          Team — команда, тренер, капитан, игроки
│   ├── tournament                    Tournament — турнир, команды, судья
│   └── match                         Match — матч, счёт, статус, протокол
├── repository                        интерфейсы Spring Data JPA (JpaRepository)
│   ├── UserRepository                пользователи (фильтры по роли и фамилии)
│   ├── PlayerUserRepository          игроки (команда, капитан, свободные агенты)
│   ├── TeamRepository                команды (город, название, тренер)
│   ├── TournamentRepository          турниры (вид спорта, название, даты, судья)
│   └── MatchRepository               матчи (статус, турнир, команда, период дат)
├── service                           контракты бизнес-логики (интерфейсы)
│   ├── CrudService                   общий интерфейс CRUD
│   ├── UserService / TeamService / TournamentService / MatchService
│   ├── factory/UserFactory           фабрика пользователей по роли
│   └── impl                          реализации
│       ├── AbstractCrudService       абстрактный шаблонный CRUD-сервис
│       └── *ServiceImpl              логика пользователей/команд/турниров/матчей
├── dto                               данные запросов и ответов REST API
│   └── user, team, tournament, match Request / Response / Filter
├── mapper                            преобразование сущностей в DTO
├── security                          интеграция со Spring Security (ЛР №4)
│   ├── TournamentUserDetailsService  загрузка пользователя по email, ROLE_ + роль
│   ├── JsonSecurityErrorHandler      ответы 401 и 403 в формате ErrorResponse
│   └── SecurityAccessLogFilter       журнал входов и отказов в доступе
├── exception                         собственные исключения, ErrorResponse и обработчик ошибок
├── controller                        REST-контроллеры (слой Controller модели MVC)
│   ├── UserController                /api/users
│   ├── TeamController                /api/teams
│   ├── TournamentController          /api/tournaments
│   └── MatchController               /api/matches
└── config                            конфигурация приложения
    ├── SecurityConfig                SecurityFilterChain: HTTP Basic, CSRF off, открытый Swagger
    ├── SecurityBeansConfig           BCryptPasswordEncoder, @EnableMethodSecurity
    ├── OpenApiConfig                 OpenAPI, схема basicAuth, схема ошибки ErrorResponse
    └── DemoDataRunner                демо-данные, профиль demo
```
Принцип единственной ответственности выдержан: контроллеры только маршрутизируют запросы,
сервисы выполняют бизнес-логику, репозитории — доступ к данным, мапперы — преобразование
в DTO.
> Ошибки API имеют единый формат с кодом, подробным сообщением, деталями и подсказкой
> по исправлению. В ЛР №4 к этому формату приведены и ответы Spring Security: 401 и 403.
---
## База данных
Схема реализована в **PgAdmin** и продублирована скриптом [`db/schema.sql`](db/schema.sql).
| Таблица | Назначение |
|---|---|
| `users` | все пользователи; Single Table Inheritance, дискриминатор `user_type` |
| `teams` | команды (тренер и капитан — ссылки на `users`) |
| `tournaments` | турниры (судья — ссылка на `users`) |
| `tournament_teams` | связь «многие ко многим» турниры ↔ команды |
| `matches` | матчи (домашняя и гостевая команды, турнир, счёт, статус) |
Порядок работы в PgAdmin:
1. `CREATE DATABASE tournament_db;`
2. Выделить `tournament_db` → **Query Tool** → открыть `db/schema.sql` → **Execute**.
3. Проверить таблицы в дереве БД (**Schemas → public → Tables**).
Параметры подключения задаются в `src/main/resources/application.properties` и могут быть
переопределены переменными окружения `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`,
`DB_PASSWORD` (по умолчанию `localhost:5432/tournament_db`, `postgres/postgres`).
---
## REST API
Базовый путь — `/api`. Формат данных — JSON.
### Пользователи `/api/users`
| Метод | Путь | Описание |
|---|---|---|
| POST | `/api/users` | создать пользователя |
| GET | `/api/users` | список всех пользователей |
| GET | `/api/users/{id}` | пользователь по идентификатору |
| PUT | `/api/users/{id}` | обновить пользователя |
| DELETE | `/api/users/{id}` | удалить пользователя |
| GET | `/api/users/search?role=COACH&lastName=Ив` | выборка с фильтрами (роль, фамилия) |
| GET | `/api/users/email/{email}` | поиск по email |
### Команды `/api/teams`
| Метод | Путь | Описание |
|---|---|---|
| POST | `/api/teams` | создать команду |
| GET | `/api/teams` | список команд |
| GET | `/api/teams/{id}` | команда по идентификатору |
| PUT | `/api/teams/{id}` | обновить команду |
| DELETE | `/api/teams/{id}` | удалить команду (если нет игроков и матчей) |
| GET | `/api/teams/search?city=Москва&name=Спартак&coachId=4` | выборка с фильтрами |
| GET | `/api/teams/{id}/players` | состав команды |
| POST | `/api/teams/{id}/players/{playerId}` | добавить игрока |
| DELETE | `/api/teams/{id}/players/{playerId}` | убрать игрока |
| PUT | `/api/teams/{id}/captain` | назначить капитана `{"playerId": 5}` |
### Турниры `/api/tournaments`
| Метод | Путь | Описание |
|---|---|---|
| POST | `/api/tournaments` | создать турнир |
| GET | `/api/tournaments` | список турниров |
| GET | `/api/tournaments/{id}` | турнир по идентификатору |
| PUT | `/api/tournaments/{id}` | обновить турнир |
| DELETE | `/api/tournaments/{id}` | удалить турнир (если нет матчей) |
| GET | `/api/tournaments/search?sportType=FOOTBALL&title=Кубок&startFrom=2026-01-01&startTo=2026-12-31&refereeId=3` | выборка с фильтрами |
| POST | `/api/tournaments/{id}/teams` | зарегистрировать команду `{"teamId": 1}` |
| DELETE | `/api/tournaments/{id}/teams/{teamId}` | исключить команду |
| GET | `/api/tournaments/{id}/standings` | турнирная таблица |
### Матчи `/api/matches`
| Метод | Путь | Описание |
|---|---|---|
| POST | `/api/matches` | создать матч |
| GET | `/api/matches` | список матчей |
| GET | `/api/matches/{id}` | матч по идентификатору |
| PUT | `/api/matches/{id}` | обновить матч |
| DELETE | `/api/matches/{id}` | удалить матч |
| GET | `/api/matches/search?status=FINISHED&tournamentId=1&teamId=2&from=2026-01-01T00:00:00&to=2026-12-31T23:59:59` | выборка с фильтрами |
| PUT | `/api/matches/{id}/finish` | завершить матч `{"score": "3:1"}` |
| POST | `/api/matches/process` | пакетная многопоточная обработка `{"matchIds": [1,2,3]}` |
Валидация входящих запросов выполняется через Jakarta Bean Validation (`@Valid`).
Ошибки обрабатываются отдельно: некорректные поля и формат запроса — `400`, запрет
операции — `403`, отсутствие объекта — `404`, конфликт данных — `409`, нарушение
бизнес-правила — `422`; непредвиденная ошибка возвращает `500`. Ответ содержит `status`,
`errorCode`, `message`, `details` и практическую подсказку в `suggestion`.
### Пример запроса
Все методы REST API защищены HTTP Basic: в заголовке `Authorization` передаются
email (он же имя входа) и пароль. Swagger UI (`/swagger-ui/index.html`) и OpenAPI JSON
(`/v3/api-docs`) открыты без входа.
```bash
# администратор из демо-профиля, пароль pass123
curl -u admin@tournament.com:pass123 http://127.0.0.1:8080/api/teams

curl -u admin@tournament.com:pass123 -X POST http://127.0.0.1:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"email":"coach@test.com","password":"pass123","firstName":"Сергей","lastName":"Тренеров","role":"COACH"}'
```
---
## Безопасность и роли (ЛР №4)
Вход выполняется по HTTP Basic: имя пользователя — `email`, пароль проверяется по
BCrypt-хешу из поля `password_hash`. Аутентификация выполняется при каждом запросе,
CSRF для REST отключён, Swagger UI и OpenAPI JSON доступны без входа, остальные методы
требуют входа. Права объявлены над методами контроллеров аннотацией `@PreAuthorize`.
### Матрица прав
| Операция | ADMIN | MANAGER | REFEREE | COACH | CAPTAIN | PLAYER |
|---|:---:|:---:|:---:|:---:|:---:|:---:|
| Просмотр команд, турниров, матчей, состава | ✔ | ✔ | ✔ | ✔ | ✔ | ✔ |
| Просмотр пользователя по `id` и по `email` | ✔ | ✔ | ✔ | ✔ | ✔ | ✔ |
| Список пользователей, `/api/users/search` | ✔ | ✔ | — | — | — | — |
| Создание, изменение, удаление пользователей | ✔ | — | — | — | — | — |
| Создание и изменение команд и турниров | ✔ | ✔ | — | — | — | — |
| Регистрация команды в турнире, исключение команды | ✔ | ✔ | — | — | — | — |
| Удаление команды, турнира, матча | ✔ | — | — | — | — | — |
| Создание и изменение матчей (расписание) | ✔ | ✔ | — | — | — | — |
| Завершение матча (`PUT /api/matches/{id}/finish`) | ✔ | — | ✔ | — | — | — |
| Пакетная обработка (`POST /api/matches/process`) | ✔ | — | — | — | — | — |
| Добавление и удаление игрока состава | ✔ | — | — | ✔ | — | — |
| Назначение капитана | ✔ | — | — | ✔ | ✔ | — |
Отказ возвращается в том же формате `ErrorResponse`: `401` — вход не выполнен,
`403` — роли не хватает. Пароли и заголовок `Authorization` в лог не попадают.
---
## Swagger UI
* Swagger UI — `http://127.0.0.1:8080/swagger-ui/index.html`
  (адрес `http://127.0.0.1:8080/swagger-ui.html` перенаправляет сюда же)
* OpenAPI JSON — `http://127.0.0.1:8080/v3/api-docs`

Методы сгруппированы по четырём контроллерам: «Пользователи», «Команды», «Турниры»,
«Матчи». У каждого метода описаны назначение, параметры, тело запроса и все
возвращаемые коды (200/201/204, 400, 401, 403, 404, 409, 422, 500). Чтобы выполнять
защищённые запросы, нажмите **Authorize** и введите email и пароль пользователя.
---
## Логирование
Логи пишутся через SLF4J и Logback одновременно в консоль и в файл `logs/tournament.log`
(конфигурация — `src/main/resources/logback-spring.xml`, каталог `logs/` в `.gitignore`).
* создание, изменение и удаление записей (сервисный слой);
* завершение матча, пакетная обработка и имя потока для каждого матча;
* успешные запросы к API с email и ролью пользователя, отказы 401 и 403.
---
## Запуск
```bash
# 1. Создать БД и выполнить db/schema.sql в PgAdmin (см. раздел «База данных»)
# 2. Собрать и запустить приложение
./mvnw clean package
./mvnw spring-boot:run
# 3. Заполнить БД демонстрационными данными (необязательно)
./mvnw spring-boot:run -Dspring-boot.run.profiles=demo
```
После запуска REST API доступен по адресу `http://127.0.0.1:8080/api/...`,
Swagger UI — `http://127.0.0.1:8080/swagger-ui/index.html`, файл лога — `logs/tournament.log`.
В демо-профиле создаются пользователи `admin@tournament.com`, `manager@tournament.com`,
`referee@tournament.com`, `coach@tournament.com` и `player@tournament.com`
с паролем `pass123` (в базе хранятся только BCrypt-хеши).

> Если в существующей базе остались пользователи со старым паролем без хеша,
> вход для них не сработает: нужна резервная копия и повторная загрузка демо-данных
> (пустая учебная база + профиль `demo`).
---
## Тесты
```bash
./mvnw test
```
Тесты (`@SpringBootTest`) **не подключаются к базе данных** и не изменяют её:
* репозитории и сервисы подменяются Mockito-моками (`@MockitoBean`);
* автоконфигурации `DataSource`, Hibernate и Spring Data JPA отключены в
  `src/test/resources/application.properties`;
* проверяются сервисная логика, доменные правила и REST-контроллеры.
| Тестовый класс | Что проверяет |
|---|---|
| `TournamentServiceApplicationTests` | загрузку Spring-контекста |
| `UserServiceImplTest` | CRUD пользователей, роли, уникальность email, фильтры |
| `TeamServiceImplTest` | CRUD команд, состав, капитан, фильтры |
| `TournamentServiceImplTest` | CRUD турниров, регистрация команд, подсчёт очков в таблице, фильтры |
| `MatchServiceImplTest` | CRUD матчей, завершение, правила ничьей, пакетная обработка, фильтры |
| `GlobalExceptionHandlerTest` | соответствие типов исключений HTTP-статусам и формату ответа |
| `*ControllerTest` | REST-маршруты, успешные ответы и обработка ошибок API (от имени администратора) |
| `SecurityAccessTest` | матрицу прав: 401 без входа, 403 для чужой роли, успех для разрешённой |
| `PublicDocumentationAccessTest` | открытый Swagger/OpenAPI и защищённые методы API |
| `TournamentUserDetailsServiceTest` | загрузку пользователя по email и преобразование роли в `ROLE_*` |
| `PasswordHashingTest` | BCrypt-хеширование при создании и обновлении пользователя |
| `TournamentTest` | доменную логику турнира |
---
## CI
* `.github/workflows/maven.yml` — GitHub Actions: `./mvnw verify` на Java 17, запускается
  на каждый push и pull request. Этот pipeline настроен с лабораторной работы №1
  и в ЛР №2 не менялся.
* `.gitlab-ci.yml` — GitLab CI (образ `maven:3-openjdk-17`, `mvn verify`). В ЛР №2 файл
  также не менялся: он уже запускает unit-тесты.
