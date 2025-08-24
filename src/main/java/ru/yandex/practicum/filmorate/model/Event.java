package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
public class Event {
    private Long userId;
    private Long timestamp;
    private Long eventId;
    private Long entityId;
    private String operation;
    private String eventType;
}
