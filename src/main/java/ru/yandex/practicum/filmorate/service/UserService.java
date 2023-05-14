package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService {

    UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUser(int id) {
        return userStorage.getUser(id);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User delete(User user) {
        return userStorage.delete(user);
    }

    public User addFriend(int id, int friendId) {
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);

        user.addFriend(friend);

        friend.addFriend(user);

        log.debug("Пользователь {} стал другом пользователя: {}", userStorage.getUser(friendId),
                userStorage.getUser(id));
        return update(user);
    }

    public User deleteFriend(int id, int friendId) {
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);

        user.removeFriend(friend);

        friend.removeFriend(user);

        log.debug("Пользователи {} и {} больше не дружат: ", userStorage.getUser(friendId),
                userStorage.getUser(id));
        return update(user);
    }

    public ArrayList<User> getFriends(int id) {
        User user = userStorage.getUser(id);
        ArrayList<User> friends = new ArrayList<>();
        for (Integer friendsId : user.getFriends()) {
            friends.add(userStorage.getUser(friendsId));
        }
        log.debug("Получены друзья пользователя : {}", userStorage.getUser(id));
        return friends;
    }

    public ArrayList<User> getCommonFriends(int id, int otherUserId) {
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(otherUserId);

        ArrayList<User> commonFriends = new ArrayList<>();

        for (Integer i : user.getFriends()) {
            if (friend.getFriends().contains(i)) {
                commonFriends.add(userStorage.getUser(i));
            }
        }
        log.debug("Получены друзья пользователя : {}", userStorage.getUser(id));
        return commonFriends;
    }
}
