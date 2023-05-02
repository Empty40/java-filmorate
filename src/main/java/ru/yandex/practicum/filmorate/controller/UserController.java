package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private List<User> users = new ArrayList<>();

    private final LocalDate controlDate = LocalDate.of(2023, 5, 2);

    private int j = 1;

    @GetMapping
    public List<User> allUsers() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return users;
    }

    @PostMapping
    public User createUser(@RequestBody User user) throws ValidationException {
        LocalDate date = LocalDate.parse(user.getBirthday());
        user.setId(j);
        if (validationUserEmail(user.getEmail()) && validationUserLogin(user.getLogin()) &&
                validationUserBirth(date)) {
            if (user.getName() == null) {
                user.setName(user.getLogin());
            }
            log.info("Был создан пользователь: {}", user.toString());
            users.add(user);
            j++;
            return user;
        } else {
            throw new ValidationException("Ошибка в валидации данных, проверьте корректность данных");
        }
    }

    @PutMapping
    public User updateUser(@RequestBody User user) throws ValidationException {
        for (int i = 0; i < users.size(); i++) {
            if (user.getId() == users.get(i).getId()) {
                log.debug("Данные пользователя были обновлены - : {}", user.toString());
                users.set(i, user);
                break;
            } else {
                throw new ValidationException("Ошибка в валидации данных, проверьте корректность введенных данных");
            }
        }
        return user;
    }

    public boolean validationUserEmail(String email) {
        if (email.contains("@")) {
            return true;
        }
        log.debug("Был введен некорректный E-mail - : {}", email);
        return false;
    }

    public boolean validationUserLogin(String login) {
        if (login != null && !login.contains(" ")) {
            return true;
        }
        log.debug("Был введен некорректный логин: {}", login);
        return false;
    }

    public boolean validationUserBirth(LocalDate date) {
        if (!date.isAfter(controlDate)) {
            return true;
        }
        log.info("Введенная дата позже чем текущая: {}", date);
        return false;
    }
}
