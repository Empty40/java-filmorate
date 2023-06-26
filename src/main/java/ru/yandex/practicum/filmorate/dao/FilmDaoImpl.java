package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
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

        checkMaxFilmId(id);

        Film film = null;

        List<Film> filmList = new ArrayList<>();

        if (filmRows.next()) {
            int mpaId = filmRows.getInt("MPA_ID");

            film = createFilmModel(filmRows.getInt("FILM_ID"), filmRows.getString("NAME"),
                    filmRows.getString("DESCRIPTION"),
                    filmRows.getDate("RELEASEDATE").toLocalDate(),
                    filmRows.getInt("DURATION"), mpaId, filmRows.getString("MPA_NAME")
            );
        }
        filmList.add(film);

        setGenresForFilmIdList(filmList);

        return film;
    }

    @Override
    public List<Film> getFilms() {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT *" +
                "FROM FILMS AS f " +
                "JOIN MPA AS m ON m.MPA_ID = f.MPA_ID " +
                "GROUP BY f.FILM_ID");

        List<Film> films = new ArrayList<>();

        List<Integer> filmIdList = new ArrayList<>();

        while (filmRows.next()) {

            int filmId = filmRows.getInt("FILM_ID");

            Film film = createFilmModel(filmRows.getInt("FILM_ID"), filmRows.getString("NAME"),
                    filmRows.getString("DESCRIPTION"),
                    filmRows.getDate("RELEASEDATE").toLocalDate(),
                    filmRows.getInt("DURATION"),
                    filmRows.getInt("MPA_ID"),
                    filmRows.getString(("MPA_NAME"))
            );

            filmIdList.add(filmId);

            films.add(film);
        }
        setGenresForFilmIdList(films);

        return films;
    }

    @Override
    public Film addFilm(Film film) {
        validationFilm(film);
        int mpaId = film.getMpa().getId();
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sqlQuery = "INSERT INTO FILMS(NAME, DESCRIPTION, RELEASEDATE, DURATION, MPA_ID) " +
                "VALUES(?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, mpaId);
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().intValue());

        List<Genres> listGenres = film.getGenres();

        batchUpdateTest(film.getId(), listGenres);

        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from MPA where " +
                " MPA_ID = ?", mpaId);
        if (mpaRows.next()) {
            String values = mpaRows.getString("MPA_NAME");
            film.getMpa().setName(values);
        }

        List<Film> filmList = new ArrayList<>();

        filmList.add(film);

        listGenres.clear();

        setGenresForFilmIdList(filmList);

        String sqlQueryGenre = "INSERT INTO FILM_MPA(FILM_ID, MPA_ID)" +
                "VALUES(?, ?)";
        jdbcTemplate.update(sqlQueryGenre,
                film.getId(),
                film.getMpa().getId());

        addDirectors(film.getId(), film.getDirectors());
        film.getDirectors().removeIf(element -> element.getName() == null);

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

        jdbcTemplate.update("DELETE FROM FILM_GENRES where FILM_ID = ?", film.getId());

        List<Film> filmList = new ArrayList<>();

        filmList.add(film);

        Set<Genres> genres = new LinkedHashSet<>(film.getGenres());

        film.getGenres().clear();

        film.getGenres().addAll(genres);

        batchUpdateTest(film.getId(), film.getGenres());

        film.getGenres().clear();

        setGenresForFilmIdList(filmList);

        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from MPA where " +
                " MPA_ID = ?", mpaId);
        if (mpaRows.next()) {
            String values = mpaRows.getString("MPA_NAME");
            film.getMpa().setName(values);
        }

        String oldDirectors = "delete from Film_director where film_id = ?";
        jdbcTemplate.update(oldDirectors, film.getId());
        addDirectors(film.getId(), film.getDirectors());

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
    public List<Film> mostPopularFilms(int count, Integer genreId, Integer year) {

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT *, COUNT(fl.FILM_ID) AS LIKES_COUNT FROM FILMS AS f ");
        queryBuilder.append("LEFT OUTER JOIN FILM_LIKES AS fl ON fl.FILM_ID = f.FILM_ID ");
        queryBuilder.append("LEFT OUTER JOIN MPA AS m ON m.MPA_ID = f.MPA_ID ");

        if (genreId != null) {
            queryBuilder.append("LEFT OUTER JOIN FILM_GENRES AS fg ON fg.FILM_ID = f.FILM_ID ");
            queryBuilder.append("WHERE fg.GENRE_ID = ? ");
        } else {
            queryBuilder.append("WHERE 1=1 ");
        }

        if (year != null) {
            queryBuilder.append("AND YEAR(f.RELEASEDATE) = ? ");
        }

        queryBuilder.append("GROUP BY f.FILM_ID ");
        queryBuilder.append("ORDER BY LIKES_COUNT DESC ");
        queryBuilder.append("LIMIT ?");

        Object[] queryParams;
        if (genreId != null && year != null) {
            queryParams = new Object[]{genreId, year, count};
        } else if (genreId != null) {
            queryParams = new Object[]{genreId, count};
        } else if (year != null) {
            queryParams = new Object[]{year, count};
        } else {
            queryParams = new Object[]{count};
        }
        List<Film> mostPopularFilms = jdbcTemplate.query(queryBuilder.toString(), queryParams,
                (rs, rowNum) ->
                        createFilmModel(rs.getInt("FILM_ID"),
                                rs.getString("NAME"),
                                rs.getString("DESCRIPTION"),
                                rs.getDate("RELEASEDATE").toLocalDate(),
                                rs.getInt("DURATION"),
                                rs.getInt("MPA_ID"),
                                rs.getString("MPA_NAME")
                        ));

        setGenresForFilmIdList(mostPopularFilms);

        return mostPopularFilms;
    }

    @Override
    public void deleteLike(int id, int userId) {
        checkMaxFilmId(id);
        checkMaxUserId(userId);
        jdbcTemplate.update("DELETE FROM FILM_LIKES where FILM_ID = ? and USER_ID = ?",
                id, userId);
    }

    @Override
    public void deleteFilm(int filmId) {
        SqlRowSet checkFilmExists = jdbcTemplate
                .queryForRowSet("select film_id from Films where film_id = ?", filmId);
        if (!checkFilmExists.next()) {
            throw new NotFoundException("Не найден фильм с id = " + filmId);
        }
        try {
            String sqlQuery = "delete from Films where Film_id = ?";
            jdbcTemplate.update(sqlQuery, filmId);
        } catch (RuntimeException r) {
            throw new ValidationException("Ошибка при удалении фильма.");
        }
    }

    @Override
    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        SqlRowSet dirExistsCheck = jdbcTemplate
                .queryForRowSet("select director_id from Directors where director_id = ?", directorId);
        if (!dirExistsCheck.next()) {
            throw new NotFoundException("Режиссер не существует.");
        }

        if (!sortBy.equals("year") && !sortBy.equals("likes")) {
            sortBy = "year";
        }
        log.info("Параметр сортировки фильмов = {}.", sortBy);

        List<Film> films = new ArrayList<>();
        List<Integer> filmIdList = new ArrayList<>();

        String requestByDirector = "SELECT f.*, m.MPA_NAME FROM Films AS f\n" +
                "INNER JOIN film_director AS d ON f.film_id = d.film_id\n" +
                "INNER JOIN MPA AS m ON m.MPA_ID = f.MPA_ID\n" +
                "WHERE d.director_id = ?\n" +
                "ORDER BY f.RELEASEDATE";

        if (sortBy.equals("likes")) {
            requestByDirector = "SELECT f.*, m.MPA_NAME, count(f.film_id) FROM Films AS f\n" +
                    "INNER JOIN film_director AS d ON f.film_id = d.film_id\n" +
                    "INNER JOIN MPA AS m ON m.MPA_ID = f.MPA_ID\n" +
                    "LEFT OUTER JOIN FILM_LIKES AS fl ON f.FILM_ID = fl.FILM_ID \n" +
                    "WHERE d.director_id = ?\n" +
                    "GROUP BY f.FILM_ID \n" +
                    "ORDER BY count(f.FILM_ID)";
        }

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(requestByDirector, directorId);
        while (filmRows.next()) {

            int filmId = filmRows.getInt("FILM_ID");

            Film film = createFilmModel(filmRows.getInt("FILM_ID"), filmRows.getString("NAME"),
                    filmRows.getString("DESCRIPTION"),
                    filmRows.getDate("RELEASEDATE").toLocalDate(),
                    filmRows.getInt("DURATION"),
                    filmRows.getInt("MPA_ID"),
                    filmRows.getString(("MPA_NAME"))
            );

            filmIdList.add(filmId);
            films.add(film);
        }
        setGenresForFilmIdList(films);

        return films;
    }

    private void setDirectors(Film film) {
        int filmId = film.getId();
        SqlRowSet filmDirectors = jdbcTemplate
                .queryForRowSet("SELECT * FROM Film_director WHERE film_id = ?", filmId);
        while (filmDirectors.next()) {
            int dirId = filmDirectors.getInt("director_id");
            SqlRowSet directors = jdbcTemplate
                    .queryForRowSet("SELECT * FROM Directors WHERE director_id = ?", dirId);
            directors.next();
            Director director = new Director(directors.getInt("director_id"), directors.getString("director_name"));

            film.getDirectors().add(director);
        }
    }

    private void addDirectors(int filmId, List<Director> directors) {
        for (Director d : directors) {
            SqlRowSet checkDirectorExists = jdbcTemplate
                    .queryForRowSet("select director_id from Directors where director_id = ?", d.getId());
            if (checkDirectorExists.next()) {
                String sqlQueryDirectors = "INSERT INTO FILM_DIRECTOR(FILM_ID, DIRECTOR_ID)" +
                        "VALUES(?, ?)";
                jdbcTemplate.update(sqlQueryDirectors,
                        filmId,
                        d.getId());

                SqlRowSet directorName = jdbcTemplate
                        .queryForRowSet("select director_name from Directors where Director_id = ?", d.getId());
                if (directorName.next()) {
                    String name = directorName.getString("Director_name");
                    d.setName(name);
                }
            }
        }
    }

    private Film createFilmModel(int filmId, String name, String description, LocalDate releaseDate, int duration,
                                 int mpaId, String mpaName) {

        HashMap<String, Integer> mpaIdMap = new HashMap<>();
        mpaIdMap.put("id", mpaId);

        Film film;

        film = new Film(
                filmId,
                name,
                description,
                releaseDate,
                duration,
                mpaIdMap,
                null);

        film.getMpa().setName(mpaName);
        setDirectors(film);
        return film;
    }

    public void batchUpdateTest(int id, List<Genres> listGenres) {
        jdbcTemplate.batchUpdate("INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES(?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setInt(1, id);
                preparedStatement.setInt(2, listGenres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return listGenres.size();
            }
        });
    }

    private void setGenresForFilmIdList(List<Film> ids) {
        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));

        List<Integer> filmId = new ArrayList<>();

        for (Film film : ids) {
            filmId.add(film.getId());
        }

        jdbcTemplate.query(
                String.format("SELECT * FROM GENRES g, FILM_GENRES fg WHERE g.genre_id = fg.genre_id AND fg.film_id IN (%s)", inSql),
                filmId.toArray(),
                (rs, rowNum) -> this.createGenre(rs, ids));
    }

    private List<Film> createGenre(ResultSet rs, List<Film> ids) throws SQLException {
        for (Film film : ids) {
            if (film.getId() == rs.getInt("FILM_ID")) {
                film.addGenres(new Genres(rs.getInt("GENRE_ID"),
                        rs.getString("NAME")));
            }
        }
        return ids;
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