package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class FilmDaoImpl implements FilmDao {
    private final JdbcTemplate jdbcTemplate;

    private static final LocalDate CONTROL_DATE = LocalDate.of(1895, 12, 28);

    public FilmDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film getFilmById(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT *" +
                "FROM FILMS AS f " +
                "JOIN MPA AS m ON m.MPA_ID = f.MPA_ID " +
                "WHERE f.FILM_ID = ?" +
                "GROUP BY f.FILM_ID", id);

        SqlRowSet genresRows = jdbcTemplate.queryForRowSet("select * from FILM_GENRES where FILM_ID = ?", id);

        checkMaxFilmId(id);

        List<HashMap<String, Integer>> genreList = new ArrayList<>();

        Film film = null;

        while (genresRows.next()) {
            if (genresRows.getInt("GENRE_ID") != 0) {
                HashMap<String, Integer> genreId = new HashMap<>();
                genreId.put("id", genresRows.getInt("GENRE_ID"));
                genreList.add(genreId);
            }
        }

        if (filmRows.next()) {
            HashMap<String, Integer> mpaId = new HashMap<>();
            mpaId.put("id", filmRows.getInt("MPA_ID"));

            film = createFilmModel(filmRows.getInt("FILM_ID"), filmRows.getString("NAME"),
                    filmRows.getString("DESCRIPTION"),
                    filmRows.getDate("RELEASEDATE").toLocalDate(),
                    filmRows.getInt("DURATION"), mpaId, genreList);

            String values = filmRows.getString("MPA_NAME");
            film.getMpa().setName(values);

            if (genreList.size() != 0) {
                List<Genres> listGenres = film.getGenres();
                for (Genres genre : listGenres) {
                    SqlRowSet genreNameRows = jdbcTemplate.queryForRowSet("select * from GENRES where " +
                            " GENRE_ID = ?", genre.getId());
                    if (genreNameRows.next()) {
                        genre.setName(genreNameRows.getString("NAME"));
                    }
                }
            }
            log.info("Найден фильм: {} {}", film.getId(),
                    film.getName());
        } else {
            log.info("Фильм с идентификатором {} не найден.", id);
        }
        return film;
    }

    @Override
    public List<Film> getFilms() {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT *" +
                "FROM FILMS AS f " +
                "JOIN MPA AS m ON m.MPA_ID = f.MPA_ID " +
                "GROUP BY f.FILM_ID");
        List<Film> films = new ArrayList<>();

        while (filmRows.next()) {

            int filmId = filmRows.getInt("FILM_ID");

            SqlRowSet genresRows = jdbcTemplate.queryForRowSet("select * from FILM_GENRES where FILM_ID = ?",
                    filmId);

            List<HashMap<String, Integer>> genreList = new ArrayList<>();

            while (genresRows.next()) {
                if (genresRows.getInt("GENRE_ID") != 0) {
                    HashMap<String, Integer> genreId = new HashMap<>();
                    genreId.put("id", genresRows.getInt("GENRE_ID"));
                    genreList.add(genreId);
                }
            }

            HashMap<String, Integer> mpaId = new HashMap<>();
            mpaId.put("id", filmRows.getInt("MPA_ID"));

            Film film = createFilmModel(filmRows.getInt("FILM_ID"), filmRows.getString("NAME"),
                    filmRows.getString("DESCRIPTION"),
                    filmRows.getDate("RELEASEDATE").toLocalDate(),
                    filmRows.getInt("DURATION"), mpaId, genreList);

            if (genreList.size() != 0) {
                List<Genres> listGenres = film.getGenres();
                for (Genres genre : listGenres) {
                    SqlRowSet genreNameRows = jdbcTemplate.queryForRowSet("select * from GENRES where " +
                            " GENRE_ID = ?", genre.getId());
                    if (genreNameRows.next()) {
                        genre.setName(genreNameRows.getString("NAME"));
                    }
                }
            }

            films.add(film);

            String values = filmRows.getString("MPA_NAME");
            film.getMpa().setName(values);
        }
        return films;
    }

    @Override
    public Film addFilm(Film film) {
        validationFilm(film);
        SqlRowSet filmRows;
        int mpaId = film.getMpa().getId();
        //KeyHolder keyHolder = new GeneratedKeyHolder();

        String sqlQuery = "INSERT INTO FILMS(NAME, DESCRIPTION, RELEASEDATE, DURATION, MPA_ID) " +
                "VALUES(?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                mpaId);

        /*jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sqlQuery);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, mpaId);
            return ps;
        }, keyHolder);*/
        //Не могу от слова совсем понять как корректно использовать keyHolder, запрос обрабатывается, данные попадают
        //в БД, но этот холдер возвращается пустым, уже даже стал смотреть индусов которые обьясняют про keyHolder :D,
        //оставлю это на следующую итерацию правок

        filmRows = jdbcTemplate.queryForRowSet("select * from FILMS where " +
                        " NAME = ?" +
                        " AND DESCRIPTION = ?" +
                        " AND RELEASEDATE = ?" +
                        " AND DURATION = ?" +
                        " AND MPA_ID = ?",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                mpaId);

        if (filmRows.next()) {
            film.setId(filmRows.getInt("FILM_ID"));
        }

        List<Genres> listGenres = film.getGenres();
        for (Genres genre : listGenres) {
            String sqlQueryGenres = "INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID)" +
                    "VALUES(?, ?)";
            jdbcTemplate.update(sqlQueryGenres,
                    film.getId(),
                    genre.getId());
        }

        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from MPA where " +
                " MPA_ID = ?", mpaId);
        if (mpaRows.next()) {
            String values = mpaRows.getString("MPA_NAME");
            film.getMpa().setName(values);
        }

        for (Genres genre : listGenres) {
            int genreId = genre.getId();
            SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select * from GENRES where " +
                    " GENRE_ID = ?", genreId);
            if (genreRows.next()) {
                String values = genreRows.getString("NAME");
                genre.setName(values);
            }
        }

        String sqlQueryGenre = "INSERT INTO FILM_MPA(FILM_ID, MPA_ID)" +
                "VALUES(?, ?)";
        jdbcTemplate.update(sqlQueryGenre,
                film.getId(),
                film.getMpa().getId());

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        checkMaxFilmId(film.getId());
        validationFilm(film);
        String sqlQuery = "UPDATE FILMS SET FILM_ID = ?, NAME = ?, DESCRIPTION = ?, RELEASEDATE = ?, DURATION = ?" +
                ", MPA_ID = ? WHERE FILM_ID = ?";
        int mpaId = film.getMpa().getId();
        jdbcTemplate.update(sqlQuery,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                mpaId,
                film.getId());

        List<Genres> listGenres = film.getGenres();

        List<Integer> idGenres = new ArrayList<>();

        for (Genres genre : listGenres) {
            SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select * from FILM_GENRES where " +
                    " GENRE_ID = ?", genre.getId());
            if (genreRows.next()) {
                SqlRowSet genreNameRows = jdbcTemplate.queryForRowSet("select * from GENRES where " +
                        " GENRE_ID = ?", genre.getId());
                if (genreNameRows.next()) {
                    String values = genreNameRows.getString("NAME");
                    genre.setName(values);
                }
                idGenres.add(genre.getId());
            } else {
                String sqlQueryGenres = "INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID)" +
                        "VALUES(?, ?)";
                jdbcTemplate.update(sqlQueryGenres,
                        film.getId(),
                        genre.getId());

                SqlRowSet genreNameRows = jdbcTemplate.queryForRowSet("select * from GENRES where " +
                        " GENRE_ID = ?", genre.getId());
                if (genreNameRows.next()) {
                    String values = genreNameRows.getString("NAME");
                    genre.setName(values);
                }
                idGenres.add(genre.getId());
            }
        }

        SqlRowSet genreForDeleteRows = jdbcTemplate.queryForRowSet("select * from FILM_GENRES where FILM_ID = ?",
                film.getId());

        while (genreForDeleteRows.next()) {
            int idGenre = genreForDeleteRows.getInt("GENRE_ID");
            if (!idGenres.contains(idGenre)) {
                jdbcTemplate.update("DELETE FROM FILM_GENRES where GENRE_ID = ? and FILM_ID = ?",
                        idGenre, film.getId());
            }
        }

        Set<Genres> set = new HashSet<>(listGenres);

        listGenres.clear();

        listGenres.addAll(set);

        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from MPA where " +
                " MPA_ID = ?", mpaId);
        if (mpaRows.next()) {
            String values = mpaRows.getString("MPA_NAME");
            film.getMpa().setName(values);
        }
        return film;
    }

    @Override
    public void addLike(int id, int userId) {
        checkMaxFilmId(id);
        checkMaxUserId(userId);
        String sqlQueryGenres = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID)" +
                "VALUES(?, ?)";
        jdbcTemplate.update(sqlQueryGenres,
                id,
                userId);
    }

    @Override
    public List<Film> mostPopularFilms(int count) {
        SqlRowSet filmIdCheck = jdbcTemplate.queryForRowSet("SELECT f.FILM_ID, COUNT(?) FROM FILMS AS f " +
                "LEFT OUTER JOIN FILM_LIKES AS fl ON fl.FILM_ID = f.FILM_ID " +
                "GROUP BY f.FILM_ID " +
                "ORDER BY fl.FILM_ID DESC " +
                "LIMIT(?)", count, count);

        List<Film> popularFilms = new ArrayList<>();

        int filmId;
        while (filmIdCheck.next()) {
            filmId = filmIdCheck.getInt("FILM_ID");
            popularFilms.add(getFilmById(filmId));
        }
        return popularFilms;
    }

    @Override
    public void deleteLike(int id, int userId) {
        checkMaxFilmId(id);
        checkMaxUserId(userId);
        jdbcTemplate.update("DELETE FROM FILM_LIKES where FILM_ID = ? and USER_ID = ?",
                id, userId);
    }

    private Film createFilmModel(int filmId, String name, String description, LocalDate releaseDate, int duration,
                                 HashMap<String, Integer> mpaId, List<HashMap<String, Integer>> genreList) {
        return new Film(
                filmId,
                name,
                description,
                releaseDate,
                duration,
                mpaId,
                genreList);
    }

    private void checkMaxFilmId(int id) {
        SqlRowSet filmIdRows = jdbcTemplate.queryForRowSet("SELECT MAX(FILM_ID) FROM FILMS");
        int maxId = 0;
        if (filmIdRows.next()) {
            maxId = filmIdRows.getInt("MAX(FILM_ID)");
        }
        if (maxId < id || id < 0) {
            throw new NotFoundException("Введен некорректный идентификатор");
        }
    }

    private void checkMaxUserId(int id) {
        SqlRowSet userIdRows = jdbcTemplate.queryForRowSet("SELECT MAX(USER_ID) FROM USERS");
        int maxId = 0;
        if (userIdRows.next()) {
            maxId = userIdRows.getInt("MAX(USER_ID)");
        }
        if (maxId < id || id < 0) {
            throw new NotFoundException("Введен некорректный идентификатор");
        }
    }

    private void validationFilm(Film filmName) {
        if (filmName.getName() == null || filmName.getName().isBlank()) {
            throw new ValidationException("Введено некорректное название фильма");
        }
        if (filmName.getDescription() == null || filmName.getDescription().isBlank() ||
                filmName.getDescription().length() > 200) {
            throw new ValidationException("Введено некорректное описание фильма");
        }
        if (filmName.getReleaseDate() == null || filmName.getReleaseDate().isBefore(CONTROL_DATE)) {
            throw new ValidationException("Дата выхода фильма раньше чем - " + CONTROL_DATE);
        }
        if (filmName.getDuration() < 0) {
            throw new ValidationException("Указана некорректная длительность фильма");
        }
    }
}
