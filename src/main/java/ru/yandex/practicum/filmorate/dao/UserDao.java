package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDao {

    User createUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    User getUser(int id);

    List<User> getCommonFriends(int id, int otherUserId);

    void addFriend(int id, int friendId);

    List<User> getFriends(int id);

    void deleteFriend(int id, int friendId);
}
