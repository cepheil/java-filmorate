package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.event.EventRepository;

import java.util.Collection;


@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final ValidationService validationService;

    public Collection<Event> findEventsByUserId(Long userId, int limit) {
        validationService.validateUserExists(userId);
        return eventRepository.findEventsByUserId(userId, limit);
    }

    public void addEvent(Long userId, Long entityId, String eventType, String operation) {
        eventRepository.insertEvent(userId, entityId, eventType, operation);
    }
}