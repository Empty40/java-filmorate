package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorDao {

    Director addDirector(Director director);

    List<Director> getAllDirectors();

    Director getDirector(int id);

    void deleteDirector(int id);

    Director updateDirector(Director director);
}