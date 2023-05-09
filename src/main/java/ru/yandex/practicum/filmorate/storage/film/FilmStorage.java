package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    //в которых будут определены методы добавления, удаления и модификации объектов

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film deleteFilm(Film film);

    List<Film> getFilms();

    Film getFilm(int id);
}
