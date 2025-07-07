# Filmorate 🎬 [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## О проекте
**Filmorate** — это REST API для оценки фильмов и поиска друзей по интересам.  
Аналог кинопоиска с возможностью:
- Добавления фильмов и пользователей
- Оставления лайков
- Поиска друзей и общих интересов

## 🛠 Технологии и зависимости
| Компонент               | Версия       |
|-------------------------|-------------|
| Java                    | 21          |
| Spring Boot             | 3.2.4       |
| Lombok                  | (из родительского POM) |
| Logbook                 | 3.7.2       |
| Spring Validation       | 3.2.4       |

```xml
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>logbook-spring-boot-starter</artifactId>
    <version>3.7.2</version>
</dependency>
```

---


## 🚀 Запуск проекта
Требования:
- JDK 21+
- Maven 3.9+

**Сборка и запуск**
```java
mvn clean package
java -jar target/filmorate-0.0.1-SNAPSHOT.jar
```

