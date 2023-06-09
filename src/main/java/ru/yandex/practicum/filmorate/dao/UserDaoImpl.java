package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final LocalDate controlDate = LocalDate.now();

    @Override
    public User getUser(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from USERS where USER_ID = ?", id);

        checkMaxId(id);

        User user = null;

        if (userRows.next()) {
            user = new User(
                    userRows.getInt("USER_ID"),
                    userRows.getString("EMAIL"),
                    userRows.getString("LOGIN"),
                    userRows.getString("NAME"),
                    userRows.getDate("BIRTHDAY").toLocalDate());
        }
        return user;
    }

    @Override
    public List<User> getUsers() {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from USERS");
        List<User> users = new ArrayList<>();

        while (userRows.next()) {

            int userId = userRows.getInt("USER_ID");

            User user = new User(
                    userRows.getInt("USER_ID"),
                    userRows.getString("EMAIL"),
                    userRows.getString("LOGIN"),
                    userRows.getString("NAME"),
                    userRows.getDate("BIRTHDAY").toLocalDate());

            users.add(user);
        }
        return users;
    }

    @Override
    public User createUser(User user) {
        validationUser(user);
        SqlRowSet userRows;

        String sqlQuery = "INSERT INTO USERS(EMAIL, LOGIN, NAME, BIRTHDAY) " +
                "VALUES(?, ?, ?, ?)";
        if (user.getName().equals(user.getLogin())) {
            jdbcTemplate.update(sqlQuery,
                    user.getEmail(),
                    user.getLogin(),
                    user.getLogin(),
                    user.getBirthday());

            userRows = jdbcTemplate.queryForRowSet("select * from USERS where " +
                            " EMAIL = ?" +
                            " AND LOGIN = ?" +
                            " AND NAME = ?" +
                            " AND BIRTHDAY = ?",
                    user.getEmail(),
                    user.getLogin(),
                    user.getLogin(),
                    user.getBirthday());

        } else {
            jdbcTemplate.update(sqlQuery,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday());

            userRows = jdbcTemplate.queryForRowSet("select * from USERS where " +
                            " EMAIL = ?" +
                            " AND LOGIN = ?" +
                            " AND NAME = ?" +
                            " AND BIRTHDAY = ?",
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday());
        }

        if (userRows.next()) {
            user.setId(userRows.getInt("USER_ID"));
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        checkMaxId(user.getId());
        validationUser(user);

        String sqlQuery = "UPDATE USERS SET USER_ID = ?, EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE USER_ID = ?";
        if (user.getName().equals(user.getLogin())) {
            jdbcTemplate.update(sqlQuery,
                    user.getId(),
                    user.getEmail(),
                    user.getLogin(),
                    user.getLogin(),
                    user.getBirthday(),
                    user.getId());
        } else {
            jdbcTemplate.update(sqlQuery,
                    user.getId(),
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());
        }
        return user;
    }

    public List<User> getCommonFriends(int id, int otherUserId) {
        checkMaxId(id);
        checkMaxId(otherUserId);
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from FRIENDSHIP where USER_ID = ?" +
                " AND FRIENDS = 1", id);

        SqlRowSet otherUserRows = jdbcTemplate.queryForRowSet("select * from FRIENDSHIP where USER_ID = ?" +
                " AND FRIENDS = 1", otherUserId);

        List<Integer> userFriends = new ArrayList<>();

        List<Integer> otherUserFriends = new ArrayList<>();

        while (userRows.next()) {
            userFriends.add(userRows.getInt("FRIEND_ID"));
        }

        while (otherUserRows.next()) {
            otherUserFriends.add(otherUserRows.getInt("FRIEND_ID"));
        }

        List<Integer> commonFriends = new ArrayList<>();

        for (Integer userId : userFriends) {
            if (otherUserFriends.contains(userId)) {
                commonFriends.add(userId);
            }
        }

        List<User> finallyCommonFriends = new ArrayList<>();

        for (Integer commonFriendsId : commonFriends) {
            finallyCommonFriends.add(getUser(commonFriendsId));
        }
        return finallyCommonFriends;
    }

    public void addFriend(int id, int friendId) {
        checkMaxId(id);
        checkMaxId(friendId);
        String sqlQuery = "INSERT INTO FRIENDSHIP(USER_ID, FRIEND_ID, FRIENDS) " +
                "VALUES(?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                id,
                friendId,
                1);
    }

    public List<User> getFriends(int id) {

        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from FRIENDSHIP where USER_ID = ?" +
                " AND FRIENDS = 1", id);

        List<User> userFriends = new ArrayList<>();

        while (userRows.next()) {
            userFriends.add(getUser(userRows.getInt("FRIEND_ID")));
        }
        return userFriends;
    }

    public void deleteFriend(int id, int friendId) {
        checkMaxId(id);
        checkMaxId(friendId);
        String sqlQuery = "UPDATE FRIENDSHIP SET FRIENDS = ? WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery,
                0,
                id,
                friendId);
    }

    public void checkMaxId(int id) {
        SqlRowSet userIdRows = jdbcTemplate.queryForRowSet("SELECT MAX(USER_ID) FROM USERS");
        int maxId = 0;
        if (userIdRows.next()) {
            maxId = userIdRows.getInt("MAX(USER_ID)");
        }
        if (maxId < id || id < 0) {
            throw new NotFoundException("Введен некорректный идентификатор");
        }
    }

    public void validationUser(User user) {
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
