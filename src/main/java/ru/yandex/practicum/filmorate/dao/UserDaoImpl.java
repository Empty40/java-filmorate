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
            user = createUserModel(userRows.getInt("USER_ID"), userRows.getString("EMAIL"),
                    userRows.getString("LOGIN"), userRows.getString("NAME"),
                    userRows.getDate("BIRTHDAY").toLocalDate());
        }
        return user;
    }

    @Override
    public List<User> getUsers() {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from USERS");
        List<User> users = new ArrayList<>();

        while (userRows.next()) {
            users.add(createUserModel(userRows.getInt("USER_ID"), userRows.getString("EMAIL"),
                    userRows.getString("LOGIN"), userRows.getString("NAME"),
                    userRows.getDate("BIRTHDAY").toLocalDate()));
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

        SqlRowSet commonFriendsRows = jdbcTemplate.queryForRowSet("SELECT * " +
                "FROM USERS AS u " +
                "JOIN FRIENDSHIP AS f ON u.user_id = f.friend_id " +
                "WHERE (f.user_id = ? OR f.user_id = ?) AND friends = true " +
                "ORDER BY f.friend_id", id, otherUserId);

        List<Integer> maybeCommonFriends = new ArrayList<>();

        List<Integer> exactlyFriends = new ArrayList<>();

        while (commonFriendsRows.next()) {
            int friendId = commonFriendsRows.getInt("FRIEND_ID");
            if (!maybeCommonFriends.contains(friendId)) {
                maybeCommonFriends.add(friendId);
            } else {
                exactlyFriends.add(friendId);
            }
        }

        List<User> commonFriends = new ArrayList<>();

        if (exactlyFriends.size() != 0) {
            commonFriends.add(getUser(exactlyFriends.get(0)));
        }

        return commonFriends;
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
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from FRIENDSHIP where USER_ID = ?" +
                " AND FRIENDS = true", id);

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
                false,
                id,
                friendId);
    }

    private User createUserModel(int userId, String userEmail, String userLogin, String userName, LocalDate birthday) {
        User user = new User(
                userId,
                userEmail,
                userLogin,
                userName,
                birthday);
        return user;
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
