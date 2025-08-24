package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.base.BaseNamedParameterRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class JdbcEventRepository extends BaseNamedParameterRepository<Event> implements EventRepository {
    private final int DEFAULT_LIMIT = 25;
    private final String FIND_EVENTS_BY_USER_ID_QUERY = """
        SELECT e.event_id, e.user_id, e.entity_id, e.event_timestamp, o.name AS operation_name, et.name AS event_type_name
        FROM event AS e
            INNER JOIN event_type AS et ON e.event_type_id = et.type_id
            INNER JOIN operation AS o ON e.operation_id = o.operation_id
        WHERE e.user_id = :userId
        ORDER BY e.event_timestamp
        LIMIT :limit;
        """;

    private final String INSERT_EVENT_QUERY = """
        INSERT INTO event(event_timestamp, entity_id, user_id, event_type_id, operation_id)
        VALUES(:eventTimestamp, :entityId, :userId,
            (SELECT type_id FROM event_type WHERE name = :eventName),
            (SELECT operation_id FROM operation WHERE name = :operationName));
        """;

    public JdbcEventRepository(NamedParameterJdbcOperations jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Event> findEventsByUserId(Long userId, int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("limit", limit == 0 ? DEFAULT_LIMIT : limit);
        return findMany(FIND_EVENTS_BY_USER_ID_QUERY, params);
    }

    @Override
    public void insertEvent(Long userId, Long entityId, String eventType, String operation) {
        Map<String, Object> params = new HashMap<>();
        params.put("eventTimestamp", Instant.now().toEpochMilli());
        params.put("entityId", entityId);
        params.put("userId", userId);
        params.put("eventName", eventType);
        params.put("operationName", operation);
        insert(INSERT_EVENT_QUERY, params);
    }
}