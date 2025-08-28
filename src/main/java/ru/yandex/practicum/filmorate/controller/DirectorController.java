package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    /**
     * Получить список всех режиссёров.
     *
     * @return коллекция всех режиссёров
     */
    @GetMapping
    public Collection<Director> findAllDirectors() {
        log.info("Получен список всех режиссёров.");
        return directorService.findAllDirectors();
    }


    /**
     * Получить режиссёра по ID.
     *
     * @param id идентификатор режиссёра
     * @return режиссёр с указанным ID
     * NotFoundException если режиссёр не найден
     */
    @GetMapping("/{id}")
    public Director findDirectorById(@PathVariable Long id) {
        log.info("Получен запрос на получение режиссёра с ID: {}", id);
        return directorService.findDirectorById(id);
    }

    /**
     * Создать нового режиссёра.
     *
     * @param director объект режиссёра для создания
     * @return созданный режиссёр с присвоенным ID
     */
    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director) {
        log.info("Получен запрос на создание режиссёра: {}", director.getName());
        return directorService.createDirector(director);
    }

    /**
     * Обновить существующего режиссёра.
     *
     * @param newDirector объект режиссёра с обновлёнными данными
     * @return обновлённый режиссёр
     */
    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director newDirector) {
        log.info("Получен запрос на обновление режиссёра с ID: {}", newDirector.getId());
        return directorService.updateDirector(newDirector);
    }

    /**
     * Удалить режиссёра по ID.
     *
     * @param id идентификатор режиссёра для удаления
     */
    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable Long id) {
        log.info("Получен запрос на удаление режиссёра с ID: {}", id);
        directorService.deleteDirector(id);
    }
}
