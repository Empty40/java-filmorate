package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        log.info("Получение фильма с айди : {}", id);
        return filmService.getFilmById(id);
    }

    @GetMapping
    public List<Film> allFilms() {
        log.info("Получение всех фильмов");
        return filmService.getFilms();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        log.info("Добавление фильма : {}", film);
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("Обновление фильма : {}", film);
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Добавление лайка фильму");
        filmService.addLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> mostPopularFilms(@RequestParam(defaultValue = "10") int count,
                                       @RequestParam(required = false) Integer genreId,
                                       @RequestParam(required = false) Integer year) {
        log.info("Получение списка популярных фильмов");
        return filmService.mostPopularFilms(count, genreId, year);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Удаление лайка фильму");
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/search")
    public List<Film> search(@RequestParam(required = false) String query, String by) {
        log.info("Производится поиск");
        return filmService.search(query, by);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable int filmId) {
        log.info("Запрос на удаление фильма id: {}", filmId);
        filmService.deleteFilm(filmId);

    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirector(@PathVariable int directorId, @RequestParam(defaultValue = "year") String sortBy) {
        log.info("Получение фильма по режиссёру");
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        log.info("Получение списка общих фильмов");
        return filmService.getCommonFilms(userId, friendId);
    }
}