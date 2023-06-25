package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@Data
public class UserService {
    private final UserDao userDao;
    private final EventDao eventDao;

    public UserService(UserDao userDao, EventDao eventDao) {
        this.userDao = userDao;
        this.eventDao = eventDao;
    }

    public User getUser(int id) {
        return userDao.getUser(id);
    }

    public List<User> getUsers() {
        return userDao.getUsers();
    }

    public User createUser(User user) {
        return userDao.createUser(user);
    }

    public User updateUser(User user) {
        return userDao.updateUser(user);
    }

    public List<User> getCommonFriends(int id, int otherUserId) {
        return userDao.getCommonFriends(id, otherUserId);
    }

    public void addFriend(int id, int friendId) {
        eventDao.addEvent(new Event("ADD", "FRIEND", id, friendId));
        userDao.addFriend(id, friendId);
    }

    public List<User> getFriends(int id) {
        return userDao.getFriends(id);
    }

    public void deleteFriend(int id, int friendId) {
        userDao.deleteFriend(id, friendId);
    }

    public void deleteUser(int userId) {
        userDao.deleteUser(userId);
    }
}
