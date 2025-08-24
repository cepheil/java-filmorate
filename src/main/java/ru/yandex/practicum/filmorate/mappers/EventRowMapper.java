package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventRowMapper implements RowMapper<Event> {
    public Event mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new Event().toBuilder()
                .eventId(resultSet.getLong("event_id"))
                .userId(resultSet.getLong("user_id"))
                .entityId(resultSet.getLong("entity_id"))
                .timestamp(resultSet.getLong("event_timestamp"))
                .operation(resultSet.getString("operation_name"))
                .eventType(resultSet.getString("event_type_name"))
                .build();
    }
}