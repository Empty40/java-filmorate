package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
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
        filmDao.deleteLike(id, userId);
        eventDao.addEvent(new Event("REMOVE", "LIKE", userId, id));
    }

    public List<Film> search(String query, String by) {
        int count = 5;

        if (query == null) {
            return mostPopularFilms(count, null, null);
        } else {
            if (by != null) {
                String[] onlyWordsFromBy = by.toLowerCase().replaceAll(" ", "").split(",");
                if (onlyWordsFromBy.length == 1 && onlyWordsFromBy[0].equals("title")) {
                    List<Film> films = filmDao.searchByTitle(query);
                    return films;
                }
                if (onlyWordsFromBy.length == 1 && onlyWordsFromBy[0].equals("director")) {
                    List<Film> films = filmDao.searchByDirector(query);
                    return films;
                } else if (onlyWordsFromBy.length > 1) {
                    if ((onlyWordsFromBy[0].equals("director") && onlyWordsFromBy[1].equals("title"))) {
                        List<Film> all = new ArrayList<>(filmDao.searchByTitle(query));
                        all.addAll(filmDao.searchByDirector(query));
                        return all;
                    }
                    if ((onlyWordsFromBy[0].equals("title") && onlyWordsFromBy[1].equals("director"))) {
                        List<Film> all = new ArrayList<>(filmDao.searchByDirector(query));
                        all.addAll(filmDao.searchByTitle(query));
                        return all;
                    }
                } else {
                    throw new NotFoundException("Такое сочетание параметров не предусмотренно");
                }
            }
            throw new NotFoundException("Такое сочетание параметров не предусмотренно");
        }
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

    public List<Film> getCommonFilms(int userId, int friendId) {
        return filmDao.getCommonFilms(userId, friendId);
    }
}