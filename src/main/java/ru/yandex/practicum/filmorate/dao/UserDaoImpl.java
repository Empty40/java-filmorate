package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;

@Component
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final LocalDate controlDate = LocalDate.now();

    @Override
    public User getUser(int id) {
        checkMaxId(id);
        List<User> users;

        users = jdbcTemplate.query("select * from USERS where USER_ID = ?",
                (rs, rowNum) ->
                        createUserModel(
                                rs.getInt("USER_ID"),
                                rs.getString("EMAIL"),
                                rs.getString("LOGIN"),
                                rs.getString("NAME"),
                                rs.getDate("BIRTHDAY").toLocalDate()
                        ),
                id);
        return users.get(0);
    }

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query("select * from USERS",
                (rs, rowNum) ->
                        createUserModel(
                                rs.getInt("USER_ID"),
                                rs.getString("EMAIL"),
                                rs.getString("LOGIN"),
                                rs.getString("NAME"),
                                rs.getDate("BIRTHDAY").toLocalDate()
                        )
        );
    }

    @Override
    public User createUser(User user) {
        validationUser(user);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sqlQuery = "INSERT INTO USERS(EMAIL, LOGIN, NAME, BIRTHDAY) " +
                "VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sqlQuery, new String[]{"USER_ID"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        checkMaxId(user.getId());
        validationUser(user);

        String sqlQuery = "UPDATE USERS SET USER_ID = ?, EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery,
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    public List<User> getCommonFriends(int id, int otherUserId) {
        checkMaxId(id);
        checkMaxId(otherUserId);

        return jdbcTemplate.query("SELECT * " +
                        "FROM USERS AS u " +
                        "JOIN FRIENDSHIP AS f ON u.user_id = f.friend_id " +
                        "JOIN FRIENDSHIP AS fr ON u.user_id = fr.friend_id " +
                        "WHERE f.friends = true AND (f.user_id = ? AND fr.user_id = ?)",
                (rs, rowNum) ->
                        createUserModel(
                                rs.getInt("USER_ID"),
                                rs.getString("EMAIL"),
                                rs.getString("LOGIN"),
                                rs.getString("NAME"),
                                rs.getDate("BIRTHDAY").toLocalDate()
                        ), id, otherUserId
        );
    }

    public void addFriend(int id, int friendId) {
        checkMaxId(id);
        checkMaxId(friendId);
        String sqlQuery = "INSERT INTO FRIENDSHIP(USER_ID, FRIEND_ID, FRIENDS) " +
                "VALUES(?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                id,
                friendId,
                true);
    }

    public List<User> getFriends(int id) {
        SqlRowSet checkUserExists = jdbcTemplate.queryForRowSet("select * from Users where user_id = ?", id);

        if (!checkUserExists.next()) {
            throw new NotFoundException("Позльзователь не найден.");
        }
        return jdbcTemplate.query("select * from USERS AS u JOIN FRIENDSHIP AS f ON u.user_id = f.friend_id where f.USER_ID = ?" +
                        " AND f.FRIENDS = true",
                (rs, rowNum) ->
                        createUserModel(
                                rs.getInt("USER_ID"),
                                rs.getString("EMAIL"),
                                rs.getString("LOGIN"),
                                rs.getString("NAME"),
                                rs.getDate("BIRTHDAY").toLocalDate()
                        ),
                id);

    }

    public void deleteFriend(int id, int friendId) {
        checkMaxId(id);
        checkMaxId(friendId);
        String sqlQuery = "UPDATE FRIENDSHIP SET FRIENDS = ? WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery,
                false,
                id,
                friendId);
    }

    @Override
    public void deleteUser(int userId) {
        SqlRowSet checkUserExists = jdbcTemplate
                .queryForRowSet("select user_id from Users where user_id = ?", userId);
        if (!checkUserExists.next()) {
            throw new NotFoundException("Не найден пользователь с id = " + userId);
        }

        try {
            String sqlQuery = "delete from Users where User_id = ?";
            jdbcTemplate.update(sqlQuery, userId);
        } catch (RuntimeException r) {
            throw new ValidationException("Ошибка при удалении пользователя.");
        }
    }

    private User createUserModel(int userid, String email, String login, String name, LocalDate birthday) {
        return new User(
                userid,
                email,
                login,
                name,
                birthday);
    }

    private void checkMaxId(int id) {
        SqlRowSet userIdRows = jdbcTemplate.queryForRowSet("SELECT MAX(USER_ID) FROM USERS");
        int maxId = 0;
        if (userIdRows.next()) {
            maxId = userIdRows.getInt("MAX(USER_ID)");
        }
        if (maxId < id || id < 0) {
            throw new NotFoundException("Введен некорректный идентификатор");
        }
    }

    private void validationUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Был введен некорректный E-mail");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Был введен некорректный логин");
        }

        if (user.getBirthday() == null || user.getBirthday().isAfter(controlDate)) {
            throw new ValidationException("Введенная дата позже чем текущая");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}