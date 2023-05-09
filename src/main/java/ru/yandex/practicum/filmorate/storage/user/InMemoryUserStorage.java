package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final LocalDate controlDate = LocalDate.now();

    private int idCount = 1;

    private HashMap<Integer, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        validationUser(user);
        user.setId(idCount);
        users.put(idCount, user);
        idCount++;
        log.info("Добавлен пользователь: {} ", user);
        return user;
    }

    @Override
    public User update(User user) {
        validationUser(user);
        int userId = user.getId();
        if (users.containsKey(userId)) {
            users.put(userId, user);
        } else {
            throw new NotFoundException("Пользователь с введенным идентификатором не найден");
        }
        log.info("Обновлен пользователь: {} ", user);
        return user;
    }

    @Override
    public User delete(User user) {
        if (users.containsKey(user.getId())) {
            users.remove(user.getId());
            log.info("Пользователь удален: {} ", user);
            return user;
        } else {
            throw new NotFoundException("Пользователь с введенным идентификатором не найден");
        }
    }

    @Override
    public ArrayList<User> getUsers() {
        log.info("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(int id) {
        if (users.containsKey(id)) {
            log.info("Получен пользователь по айди: {}", users.get(id));
            return users.get(id);
        } else {
            throw new NotFoundException("Пользователь с введенным идентификатором не найден");
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
