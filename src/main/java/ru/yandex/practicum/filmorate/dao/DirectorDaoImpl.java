package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class DirectorDaoImpl implements DirectorDao {

    private final JdbcTemplate jdbcTemplate;

    public DirectorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Director addDirector(Director director) {
        int id = director.getId();
        String name = director.getName();

        if (id < 0) {
            throw new ValidationException("Id режиссера не может быть отрицательным.");
        }

        if (name.isBlank()) {
            throw new ValidationException("Имя режиссера не может быть пустым.");
        }

        SqlRowSet idCheck = jdbcTemplate
                .queryForRowSet("select director_id from Directors where director_id = ?", director.getId());

        if (idCheck.next()) {
            throw new ValidationException("Режиссер с id = " + id + " уже существует.");
        }

        SqlRowSet nameCheck = jdbcTemplate
                .queryForRowSet("select director_name from Directors where director_name = ?", director.getName());
        if (nameCheck.next()) {
            throw new ValidationException("Режиссер " + name + " уже существует.");
        }

        String sqlQuery = "insert into Directors(director_name) " +
                "values (?)";
        jdbcTemplate.update(sqlQuery,
                director.getName());
        SqlRowSet idUpd = jdbcTemplate
                .queryForRowSet("select director_id from Directors where director_name = ?", director.getName());

        idUpd.next();
        director.setId(idUpd.getInt("director_id"));
        log.info("Режиссер добавлен.");
        return director;
    }

    @Override
    public List<Director> getAllDirectors() {
        List<Director> directors = new ArrayList<>();
        SqlRowSet directorRows = jdbcTemplate
                .queryForRowSet("select * from Directors ORDER BY director_id");
        while (directorRows.next()) {
            directors.add(new Director(
                    directorRows
                            .getInt("director_id"),
                    directorRows.getString("director_name")));
        }
        log.info("Сформирован список режиссеров.");
        return directors;
    }

    @Override
    public Director getDirector(int directorId) {
        checkDirectorExists(directorId);
        SqlRowSet directorRow = jdbcTemplate
                .queryForRowSet("select * from Directors where director_id = ?", directorId);

        directorRow.next();
        Director director = new Director(directorId, directorRow.getString("director_name"));
        log.info("Найден режиссер: " + director.getName());
        return director;
    }

    @Override
    public void deleteDirector(int directorId) {
        checkDirectorExists(directorId);
        try {
            String sqlQuery = "delete from Directors where director_id = ?";
            jdbcTemplate.update(sqlQuery, directorId);
        } catch (RuntimeException r) {
            throw new ValidationException("Ошибка при удалении режиссера.");
        }
        log.info("Удален режиссер id: " + directorId);
    }

    @Override
    public Director updateDirector(Director director) {
        checkDirectorExists(director.getId());
        int id = director.getId();
        String name = director.getName();
        deleteDirector(director.getId());

        String sqlQuery = "insert into Directors(director_id, director_name) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery,
                id, name);
        log.info("Обновлены данные режиссера с идентификатором {}.", id);
        return director;
    }

    private void checkDirectorExists(int directorId) {
        SqlRowSet checkDirectorExists = jdbcTemplate
                .queryForRowSet("select director_id from Directors where director_id = ?", directorId);
        if (!checkDirectorExists.next()) {
            throw new NotFoundException("Не найден режиссер с id = " + directorId);
        }
    }
}
