package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Service
@Slf4j
public class DirectorService {

    private final DirectorDao directorDao;

    @Autowired
    public DirectorService(DirectorDao directorDao) {
        this.directorDao = directorDao;
    }

    public Director addDirector(Director director) {
        return directorDao.addDirector(director);
    }

    public List<Director> getAllDirectors() {
        return directorDao.getAllDirectors();
    }

    public Director getDirector(int id) {
        return directorDao.getDirector(id);
    }

    public void deleteDirector(int id) {
        directorDao.deleteDirector(id);
    }

    public Director updateDirector(Director director) {
        return directorDao.updateDirector(director);
    }
}