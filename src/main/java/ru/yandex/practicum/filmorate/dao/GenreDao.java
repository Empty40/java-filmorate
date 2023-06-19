package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genres;

import java.util.List;
import java.util.Optional;

public interface GenreDao {

    Optional<Genres> getGenreById(int id);

    List<Genres> getAllGenre();
}
