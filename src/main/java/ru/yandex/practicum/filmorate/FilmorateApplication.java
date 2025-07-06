package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Основной класс приложения Filmorate, запускающий Spring Boot-приложение.
 *
 * <p>Этот класс содержит точку входа ({@code main}) и аннотирован как {@link SpringBootApplication},
 * что активирует автоматическую настройку Spring и компонент-сканирование.</p>
 *
 * <p>При запуске приложение загружает контекст Spring, инициализирует бины и запускает встроенный веб-сервер.</p>
 *
 * @see SpringApplication
 * @see SpringBootApplication
 */
@SpringBootApplication
public class FilmorateApplication {
    public static void main(String[] args) {
        SpringApplication.run(FilmorateApplication.class, args);
    }

}
