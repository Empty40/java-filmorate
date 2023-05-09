package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    @Autowired
    FilmStorage filmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film getFilm(int id) {
        return filmStorage.getFilm(id);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film update(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film deleteFilm(Film film) {
        return filmStorage.deleteFilm(film);
    }

    public Film addUserLike(int id, int userId) {
        if (id < 0 || userId < 0) {
            throw new NotFoundException("Передан некорректный идентификатор");
        }
        Film film = filmStorage.getFilm(id);
        film.addLike(userId);
        return update(film);
    }

    public Film deleteUserLike(int id, int userId) {
        if (id < 0 || userId < 0) {
            throw new NotFoundException("Передан некорректный идентификатор");
        }
        Film film = filmStorage.getFilm(id);
        film.removeLike(userId);
        return update(film);
    }

    public List<Film> mostPopularFilms(Integer count) {
        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparing(Film::getUserLikeCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
