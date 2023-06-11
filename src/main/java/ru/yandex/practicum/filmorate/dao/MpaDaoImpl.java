package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class MpaDaoImpl implements MpaDao {

    private final JdbcTemplate jdbcTemplate;

    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Mpa> getMpaById(int id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from MPA where MPA_ID = ?", id);

        if (mpaRows.next()) {
            Mpa mpa = new Mpa(
                    mpaRows.getInt("MPA_ID"), mpaRows.getString("MPA_NAME")
            );

            log.info("Найден mpa: {} {}", mpa.getId(),
                    mpa.getName());

            return Optional.of(mpa);
        } else {
            log.info("Фильм с идентификатором {} не найден.", id);
            throw new NotFoundException("Введен некорректный идентификатор");
        }
    }

    public List<Mpa> getAllMpa() {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from MPA");
        List<Mpa> allMpa = new ArrayList<>();

        while (mpaRows.next()) {
            Mpa mpa = new Mpa(
                    mpaRows.getInt("MPA_ID"), mpaRows.getString("MPA_NAME")
            );

            log.info("Найден mpa: {} {}", mpa.getId(),
                    mpa.getName());

            allMpa.add(mpa);
        }
        return allMpa;
    }

}
