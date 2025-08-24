package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

public interface EventRepository {
    Collection<Event> findEventsByUserId(Long userId, int limit);

    void insertEvent(Long userId, Long entityId, String eventType, String operation);
}
