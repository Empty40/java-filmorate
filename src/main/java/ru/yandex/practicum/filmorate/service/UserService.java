package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.Entity;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
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
        User user = userDao.getUser(id);
        User friend = userDao.getUser(friendId);
        userDao.addFriend(user.getId(), friend.getId());
        eventDao.addEvent(new Event(Operation.ADD, Entity.FRIEND, id, friendId));
    }

    public List<User> getFriends(int id) {
        return userDao.getFriends(id);
    }

    public void deleteFriend(int id, int friendId) {
        userDao.deleteFriend(id, friendId);
        eventDao.addEvent(new Event(Operation.REMOVE, Entity.FRIEND, id, friendId));
    }

    public void deleteUser(int userId) {
        userDao.deleteUser(userId);
    }

    //Получение коллекции событий
    public Collection<Event> getUserFeed(int userId) {
        return eventDao.getEventUser(userId);
    }
}
