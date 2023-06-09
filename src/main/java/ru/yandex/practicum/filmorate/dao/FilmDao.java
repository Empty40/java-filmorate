package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.sql.SQLException;
import java.util.List;

public interface FilmDao {
    Film getFilmById(int id);

    Film addFilm(Film film) throws SQLException;

    List<Film> getFilms();

    Film updateFilm(Film film);

    void addLike(int id, int userId);

    List<Film> mostPopularFilms(String count);

    void deleteLike(int id, int userId);

    List<Film> allPopularFilms();
}
