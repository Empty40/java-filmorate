package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genres;

import java.util.List;
import java.util.Optional;

@Service
public class GenreService {

    private final GenreDao genreDao;

    @Autowired
    public GenreService(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public Optional<Genres> getGenreById(int id) {
        return genreDao.getGenreById(id);
    }

    public List<Genres> getAllGenre() {
        return genreDao.getAllGenre();
    }

}
