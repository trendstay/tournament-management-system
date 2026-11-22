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
---
## Что делает система
1. **Пользователи.** Абстрактный класс `User` и наследники по ролям: `AdminUser`,
   `ManagerUser`, `CoachUser`, `RefereeUser`, `PlayerUser` (+ роль `CAPTAIN`).
   Сервис проверяет уникальность email, умеет назначать игрока в команду
   и переключать роли `PLAYER` ↔ `CAPTAIN`.
   Пароль сохраняется как есть: хеширование и аутентификация — задача ЛР №4.
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
| Lombok | генерация геттеров/сеттеров DTO |
| Jakarta Bean Validation | валидация входящих запросов |
| JUnit 5 + Mockito | unit-тесты |
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
├── exception                         собственные исключения, ErrorResponse и обработчик ошибок
├── controller                        REST-контроллеры (слой Controller модели MVC)
│   ├── UserController                /api/users
│   ├── TeamController                /api/teams
│   ├── TournamentController          /api/tournaments
│   └── MatchController               /api/matches
└── config                            DemoDataRunner (демо-данные, профиль demo)
```
Принцип единственной ответственности выдержан: контроллеры только маршрутизируют запросы,
сервисы выполняют бизнес-логику, репозитории — доступ к данным, мапперы — преобразование
в DTO.
> В ЛР №3 добавлены собственные исключения и `@RestControllerAdvice`: ошибки API имеют
> единый формат с кодом, подробным сообщением, деталями и подсказкой по исправлению.
> Spring Security, роли доступа и логирование остаются темами ЛР №4 и здесь не реализуются.
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
```bash
curl -X POST http://127.0.0.1:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"email":"coach@test.com","password":"pass123","firstName":"Сергей","lastName":"Тренеров","role":"COACH"}'
```
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
После запуска REST API доступен по адресу `http://127.0.0.1:8080/api/...`.
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
| `*ControllerTest` | REST-маршруты, успешные ответы и обработка ошибок API |
| `TournamentTest` | доменную логику турнира |
---
## CI
* `.github/workflows/maven.yml` — GitHub Actions: `./mvnw verify` на Java 17, запускается
  на каждый push и pull request. Этот pipeline настроен с лабораторной работы №1
  и в ЛР №2 не менялся.
* `.gitlab-ci.yml` — GitLab CI (образ `maven:3-openjdk-17`, `mvn verify`). В ЛР №2 файл
  также не менялся: он уже запускает unit-тесты.
