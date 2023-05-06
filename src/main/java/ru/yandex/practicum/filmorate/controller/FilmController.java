package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private int idCount = 1;

    private static final LocalDate CONTROL_DATE = LocalDate.of(1895, 12, 28);

    private HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping
    public ArrayList<Film> allFilms() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        validationFilm(film);
        film.setId(idCount);
        log.info("В список добавлен фильм: {}", film);
        films.put(idCount, film);
        idCount++;
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        int filmId = film.getId();
        if (films.containsKey(filmId)) {
            log.debug("Произошло обновление фильма - : {}", films.get(filmId));
            films.put(filmId, film);
        } else {
            throw new NotFoundException("Фильм с введенным идентификатором не найден");
        }
        return film;
    }

    public void validationFilm(Film filmName) {
        if (filmName.getName() == null || filmName.getName().isBlank()) {
            throw new ValidationException("Введено некорректное название фильма");
        }
        if (filmName.getDescription() == null || filmName.getDescription().isBlank() ||
                filmName.getDescription().length() > 200) {
            throw new ValidationException("Введено некорректное описание фильма");
        }
        if (filmName.getReleaseDate() == null || filmName.getReleaseDate().isBefore(CONTROL_DATE)) {
            throw new ValidationException("Дата выхода фильма раньше чем - " + CONTROL_DATE);
        }
        if (filmName.getDuration() < 0) {
            throw new ValidationException("Указана некорректная длительность фильма");
        }
    }

}
