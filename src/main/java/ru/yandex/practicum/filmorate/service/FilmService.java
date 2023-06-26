package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

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

    public List<Film> search(String query, String by) {
        int count = 5;

        if (query == null) {
            return mostPopularFilms(count);
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
//
//                    if ((onlyWordsFromBy[0].equals("title") && onlyWordsFromBy[1].equals("director"))) {
//                        List<Film> all = new ArrayList<>(filmStorage.searchByDirector(query));
//                        all.addAll(replaceGenresByNull(filmStorage.searchByTitle(query)));
//                        return all;
//                    }

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
}
