# Анализатор страниц (Java)

[![hexlet-check](https://github.com/kicha3007-boop/java-project-72/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/kicha3007-boop/java-project-72/actions)
[![Java CI](https://github.com/kicha3007-boop/java-project-72/actions/workflows/main.yml/badge.svg)](https://github.com/kicha3007-boop/java-project-72/actions/workflows/main.yml)

Создадите полноценное веб-приложение, которое выполняет запросы по сети и сохраняет данные в базу данных. Настроите CI и выполните деплой.

Учебный проект Хекслета: https://ru.hexlet.io/programs/java
Как это должно работать: https://files.hexlet.app/a/f9wlja

## Стек

- Java 21, Javalin 7, jte, Tailwind CSS (сборка стилей через npm)
- JDBC + HikariCP: H2 в памяти локально и в тестах, PostgreSQL в продакшене (`JDBC_DATABASE_URL`)
- Unirest + jsoup — запрос и разбор проверяемой страницы
- JUnit 5, AssertJ, MockWebServer, JaCoCo (порог 80% в `check`), Spotless

## Установка

Нужны JDK 21, Node.js 22 и make.

```bash
git clone https://github.com/kicha3007-boop/java-project-72.git
cd java-project-72
make setup        # npm ci, сборка стилей, ./gradlew installDist
make start        # режим разработки: вотчер стилей + приложение на http://localhost:7070
make start-dist   # собранный дистрибутив в режиме production
make lint
make test
```

Переменные окружения:

| Переменная | По умолчанию | Назначение |
|---|---|---|
| `PORT` | `7070` | порт приложения |
| `JDBC_DATABASE_URL` | H2 в памяти | база, например `jdbc:postgresql://host:5432/db?user=u&password=p` |
| `APP_ENV` | `production` | `development` читает шаблоны и стили из `src` без перезапуска |

## Использование

Сайт проверяет страницы на SEO-пригодность:

1. На главной вводится адрес — сохраняется только схема, хост и порт (`https://site.org:8080`).
   Повторный адрес не дублируется, некорректный возвращает `422` и «Некорректный URL».
2. `/urls` — список сайтов, новые первыми, с датой и кодом последней проверки.
3. `/urls/{id}` — сайт и его проверки; «Запустить проверку» запрашивает страницу и сохраняет код
   ответа, `title`, `h1` и `description` (длинные значения обрезаются до 200 символов).

Маршруты: `GET /`, `GET|POST /urls`, `GET /urls/{id}`, `POST /urls/{id}/checks`.

---

<details>
<summary>Автоматические тесты Хекслета</summary>

Тесты запускаются на каждый коммит. За запуск отвечает файл `.github/workflows/hexlet-check.yml` — не удаляйте и не переименовывайте ни его, ни репозиторий.

</details>

## О Хекслете

[Хекслет](https://ru.hexlet.io/) — школа программирования: авторские программы обучения с практикой, поддержкой наставников и реальными проектами, которые остаются в резюме. Этот репозиторий — один из таких проектов.
