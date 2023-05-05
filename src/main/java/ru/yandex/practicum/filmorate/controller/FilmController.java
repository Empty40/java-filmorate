package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private int idCount = 1;

    private static final LocalDate CONTROL_DATE = LocalDate.of(1895, 12, 28);

    private HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Integer allFilms() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films.size();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) throws Exception {
        film.setId(idCount);
        if (validationFilm(film) && (film.getDuration() > 0)) {
            log.info("В список добавлен фильм: {}", film);
            films.put(idCount, film);
            idCount++;
            return film;
        } else {
            throw new ValidationException("Ошибка в валидации данных, проверьте корректность данных");
        }
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        for (int i = 0; i < films.size(); i++) {
            if (film.getId() > films.size()) {
                throw new ValidationException("Ошибка в валидации данных, проверьте корректность данных");
            }
            if (film.getId() == films.get(i).getId()) {
                log.debug("Произошло обновление фильма - : {}", films.get(i));
                films.put(i, film);
                break;
            }
        }
        return film;
    }

    public boolean validationFilm(Film filmName) {
            if (filmName.getName() != null && !filmName.getName().isBlank()) {
            } else {
                throw new ValidationException("Введено некорректное название фильма");
            }

            if (filmName.getDescription() != null && !filmName.getDescription().isBlank() &&
                    filmName.getDescription().length() <= 200) {
            } else {
                throw new ValidationException("Введено некорректное описание фильма");
            }

            if (filmName.getReleaseDate() != null && !filmName.getReleaseDate().isBefore(CONTROL_DATE.atStartOfDay())) {
            } else {
                throw new ValidationException("Дата выхода фильма раньше чем - " + CONTROL_DATE);
            }
        return true;
    }

}
