package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;

@Component
@Slf4j
@AllArgsConstructor
@RequiredArgsConstructor
public class EventDaoImpl implements EventDao {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    UserDao userDao;

    //Добавление события с генерацией ключа
    @Override
    public void addEvent(Event event) {
        log.debug("Запрос на добавление {} запущен", event);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO EVENT_FEED\n" +
                "(EVENT_TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID)\n" +
                "VALUES(?, ?, ?, ?, ?);";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sql, new String[]{"EVENT_Id"});
            ps.setTimestamp(1, Timestamp.from(Instant.ofEpochMilli(event.getTimestamp())));
            ps.setInt(2, event.getUserId());
            ps.setString(3, event.getEventType());
            ps.setString(4, event.getOperation());
            ps.setInt(5, event.getEntityId());
            return ps;
        }, keyHolder);

        int id = keyHolder.getKey().intValue();
        event.setEventId(id);
        log.debug("Запрос на добавление события {} выполнен", event);
    }

    //Получение списка событий по id пользователя
    @Override
    public Collection<Event> getEventUser(int userId) {
        log.debug("Запрос на получение списка событий по userId = {} получен", userId);
        User user = userDao.getUser(userId);
        String sql = "SELECT EVENT_ID, EVENT_TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID\n" +
                "FROM EVENT_FEED " +
                "WHERE USER_ID = ?;";
        log.debug("Запрос на получение списка событий по userId = {} выполнен", user.getId());
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeEvent(rs), user.getId());
    }

    //Собираем событие
    private Event makeEvent(ResultSet rs) throws SQLException {
        return Event.builder()
                .eventId(rs.getInt("EVENT_ID"))
                .timestamp(rs.getTimestamp("EVENT_TIMESTAMP").toInstant().toEpochMilli())
                .userId(rs.getInt("USER_ID"))
                .eventType(rs.getString("EVENT_TYPE"))
                .operation(rs.getString("OPERATION"))
                .entityId(rs.getInt("ENTITY_ID"))
                .build();
    }
}
