package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private HashMap<Integer, User> users = new HashMap<>();

    private final LocalDateTime controlDate = LocalDateTime.now();

    private int idCount = 1;

    @GetMapping
    public Integer allUsers() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return users.size();
    }

    @PostMapping
    public User createUser(@RequestBody User user) throws ValidationException {
        user.setId(idCount);
        if (validationUser(user)) {
            if (user.getName() == null) {
                user.setName(user.getLogin());
            }
            log.info("Был создан пользователь: {}", user);
            users.put(idCount, user);
            idCount++;
            return user;
        } else {
            throw new ValidationException("Ошибка в валидации данных, проверьте корректность данных");
        }
    }

    @PutMapping
    public User updateUser(@RequestBody User user) throws ValidationException {
        for (int i = 0; i < users.size(); i++) {
            if (user.getId() == users.get(i).getId()) {
                log.debug("Данные пользователя были обновлены - : {}", user);
                users.put(i, user);
                break;
            } else {
                throw new ValidationException("Ошибка в валидации данных, проверьте корректность данных");
            }
        }
        return user;
    }

    public boolean validationUser(User user) {
        if (user.getEmail() != null && user.getEmail().contains("@")) {
        } else {
            throw new ValidationException("Был введен некорректный E-mail");
        }

        if (user.getLogin() != null && !user.getLogin().isBlank()) {
        } else {
            throw new ValidationException("Был введен некорректный логин");
        }

        if (user.getBirthday() != null && !user.getBirthday().isAfter(controlDate)) {
        } else {
            throw new ValidationException("Введенная дата позже чем текущая");
        }
        return true;
    }
}
