package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private HashMap<Integer, User> users = new HashMap<>();

    private final LocalDate controlDate = LocalDate.now();

    private int idCount = 1;

    @GetMapping
    public ArrayList<User> allUsers() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@RequestBody User user) throws ValidationException {
        validationUser(user);
            user.setId(idCount);
            log.info("Был создан пользователь: {}", user);
            users.put(idCount, user);
            idCount++;
            return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) throws ValidationException {
        int userId = user.getId();
        if (users.containsKey(userId)) {
            log.debug("Данные пользователя были обновлены - : {}", user);
            users.put(userId, user);
        } else {
            throw new NotFoundException("Пользователь с введенным идентификатором не найден");
        }
        return user;
    }

    public void validationUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Был введен некорректный E-mail");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
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
