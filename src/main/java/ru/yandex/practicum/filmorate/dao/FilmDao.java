package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDao {
    Film getFilmById(int id);

    Film addFilm(Film film);

    List<Film> getFilms();

    Film updateFilm(Film film);

    void addLike(int id, int userId);

    List<Film> mostPopularFilms(int count, Integer genreId, Integer year);

    void deleteLike(int id, int userId);

    List<Film> searchByTitle(String query);

    void deleteFilm(int filmId);

    List<Film> searchByDirector(String query);

    List<Film> getFilmsByDirector(int directorId, String sortBy);

    List<Film> showFilmRecommendations(int userId);

    List<Film> getCommonFilms(int userId, int friendId);
}
