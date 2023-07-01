package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    private final FilmService filmService;

    @Autowired
    public UserController(UserService userService, FilmService filmService) {
        this.userService = userService;
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        log.info("Получение пользователя");
        return userService.getUser(id);
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Получение всех пользователей");
        return userService.getUsers();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Создание пользователя");
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Обновление пользователя");
        return userService.updateUser(user);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Получение общих друзей");
        return userService.getCommonFriends(id, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Добавление друга");
        userService.addFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        log.info("Получение списка друзей");
        return userService.getFriends(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Удаление друга");
        userService.deleteFriend(id, friendId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) {
        log.info("Запрос на удаление пользователя id: {}", userId);
        userService.deleteUser(userId);
    }

    //Получение ленты событий
    @GetMapping("/{userId}/feed")
    public Collection<Event> getUserFeed(@PathVariable int userId) {
        log.info("Получение списка событий пользователя");
        return userService.getUserFeed(userId);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> showFilmRecommendations(@PathVariable int id) {
        log.info("Получение списка рекомендаций");
        return filmService.showFilmRecommendations(id);
    }
}
