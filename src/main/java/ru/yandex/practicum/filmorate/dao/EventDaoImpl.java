package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.Entity;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;

@Component
@Slf4j
public class EventDaoImpl implements EventDao {

    private final JdbcTemplate jdbcTemplate;

    private final UserDao userDao;

    @Autowired
    public EventDaoImpl(JdbcTemplate jdbcTemplate, UserDao userDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDao = userDao;
    }

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
            ps.setString(3, event.getEventType().toString());
            ps.setString(4, event.getOperation().toString());
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
        String sql = "SELECT *\n" +
                "FROM EVENT_FEED " +
                "WHERE USER_ID = ?;";
        log.debug("Запрос на получение списка событий по userId = {} выполнен", user.getId());
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeEvent(rs), user.getId());
    }

    //Собираем событие
    private Event makeEvent(ResultSet rs) throws SQLException {
        log.debug("Началась сборка event по запросу sql");
        return Event.builder()
                .eventId(rs.getInt("EVENT_ID"))
                .timestamp(rs.getTimestamp("EVENT_TIMESTAMP").toInstant().toEpochMilli())
                .userId(rs.getInt("USER_ID"))
                .eventType(Entity.valueOf(rs.getString("EVENT_TYPE")))
                .operation(Operation.valueOf(rs.getString("OPERATION")))
                .entityId(rs.getInt("ENTITY_ID"))
                .build();
    }
}
