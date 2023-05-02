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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private int j = 1;

    private final LocalDate controlDate = LocalDate.of(1895, 12, 28);

    private List<Film> films = new ArrayList<>();

    @GetMapping
    public List<Film> allFilms() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films;
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) throws Exception {
        LocalDate date = LocalDate.parse(film.getReleaseDate());
        film.setId(j);
        if (validationFilmName(film.getName()) && validationLengthDescriptions(film.getDescription()) &&
                validationDateRelease(date) && (film.getDuration() > 0)) {
            log.info("В список добавлен фильм: {}", film);
            films.add(film);
            j++;
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
                films.set(i, film);
                break;
            }
        }
        return film;
    }

    public boolean validationFilmName(String filmName) {
        if (filmName != null && !filmName.isEmpty()) {
            return true;
        }
        log.debug("Введено пустое название фильма: {}", filmName);
        return false;
    }

    public boolean validationLengthDescriptions(String descriptions) {
        int count = 0;
        for (int i = 0; i < descriptions.length(); i++) {
            count++;
        }
        if (count <= 200) {
            return true;
        }
        log.debug("Длинна описания больше разрешенной, текущая длинна - : {}", count);
        return false;
    }

    public boolean validationDateRelease(LocalDate date) {
        if (!date.isBefore(controlDate)) {
            return true;
        }
        log.debug("Дата выхода фильма раньше чем - : {}", controlDate);
        return false;
    }

}
