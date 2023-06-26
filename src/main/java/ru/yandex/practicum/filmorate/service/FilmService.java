package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Service
public class FilmService {

    private final FilmDao filmDao;
    private final EventDao eventDao;

    public FilmService(FilmDao filmDao, EventDao eventDao) {
        this.filmDao = filmDao;
        this.eventDao = eventDao;
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
        eventDao.addEvent(new Event("ADD", "LIKE", userId, id));
        filmDao.addLike(id, userId);
    }

    public List<Film> mostPopularFilms(int count, Integer genreId, Integer year) {
        return filmDao.mostPopularFilms(count, genreId, year);
    }

    public void deleteLike(int id, int userId) {
        eventDao.addEvent(new Event("REMOVE", "LIKE", userId, id));
        filmDao.deleteLike(id, userId);
    }

    public void deleteFilm(int filmId) {
        filmDao.deleteFilm(filmId);
    }


    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        return filmDao.getFilmsByDirector(directorId, sortBy);
    }

    public List<Film> showFilmRecommendations(int userId) {
        return filmDao.showFilmRecommendations(userId);
    }
}