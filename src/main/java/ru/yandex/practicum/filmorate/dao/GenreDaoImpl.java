package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genres;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Genres> getGenreById(int id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select * from GENRES where GENRE_ID = ?", id);

        if (id > 6 || id < 0) {
            throw new NotFoundException("Введен некорректный идентификатор");
        }

        if (genreRows.next()) {
            String values = genreRows.getString("NAME");
            Genres genres = new Genres(
                    genreRows.getInt("GENRE_ID"),
                    values);


            log.info("Найден mpa: {} {}", genres.getId(),
                    genres.getName());

            return Optional.of(genres);
        } else {
            log.info("Жанр с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    public List<Genres> getAllGenre() {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select * from GENRES");
        List<Genres> allGenres = new ArrayList<>();

        while (genreRows.next()) {
            String values = genreRows.getString("NAME");

            Genres genres = new Genres(
                    genreRows.getInt("GENRE_ID"),
                    values);


            log.info("Найден mpa: {} {}", genres.getId(),
                    genres.getName());

            allGenres.add(genres);
        }
        return allGenres;
    }
}
