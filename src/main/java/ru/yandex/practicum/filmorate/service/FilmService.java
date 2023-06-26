package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Service
public class FilmService {

    private final FilmDao filmDao;

    public FilmService(FilmDao filmDao) {
        this.filmDao = filmDao;
    }

    public Film getFilmById(int id) {
        return filmDao.getFilmById(id);
    }

    public List<Film> getFilms() {
        return filmDao.getFilms();
    }

    public Film addFilm(Film film) {
        return filmDao.addFilm(film);
    }

    public Film update(Film film) {
        return filmDao.updateFilm(film);
    }

    public void addLike(int id, int userId) {
        filmDao.addLike(id, userId);
    }

    public List<Film> mostPopularFilms(int count) {
        return filmDao.mostPopularFilms(count);
    }

    public void deleteLike(int id, int userId) {
        filmDao.deleteLike(id, userId);
    }

    public void deleteFilm(int filmId) {
        filmDao.deleteFilm(filmId);
    }


    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        return filmDao.getFilmsByDirector(directorId, sortBy);
    }
}