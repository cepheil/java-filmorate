package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorRepository;

import java.util.Collection;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final ValidationService validationService;
    private final DirectorRepository directorRepository;

    public Collection<Director> findAllDirectors() {
        log.info("Попытка получения всех режиссеров");
        return directorRepository.findAllDirectors();
    }

    public Director findDirectorById(Long directorId) {
        log.info("Попытка получения режиссера по ID"); //{}, directorId лучше не использовать
        validationService.validateDirectorExists(directorId);
        return directorRepository.findDirectorById(directorId)
                .orElseThrow(() -> new NotFoundException("Режиссер с ID " + directorId + " не найден"));
    }

    public Director createDirector(Director director) {
        log.info("Попытка создания режиссера: {}", director.getName());
        Director createdDirector = directorRepository.createDirector(director);
        log.info("Создан режиссер с ID: {}", createdDirector.getId());
        return createdDirector;
    }

    public Director updateDirector(Director newDirector) {
        log.info("Попытка обновления режиссера");
        validationService.validateDirectorExists(newDirector.getId());
        Director updatedDirector = directorRepository.updateDirector(newDirector);
        log.info("Режиссер с ID {} обновлен", newDirector.getId());
        return updatedDirector;
    }

    public void deleteDirector(Long directorId) {
        log.info("Попытка удаления режиссера");
        validationService.validateDirectorExists(directorId);
        if (!directorRepository.deleteDirector(directorId)) {
            log.warn("Режиссер с ID={} не найден при попытке удаления", directorId);
            throw new NotFoundException("Режиссер с ID=" + directorId + " не найден");
        }
        log.info("Режиссер  с ID {} успешно удален", directorId);
    }
}
