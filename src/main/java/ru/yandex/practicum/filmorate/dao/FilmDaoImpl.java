package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

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
        List<Film> mostPopularFilms;

        mostPopularFilms = jdbcTemplate.query("SELECT *, COUNT(?) FROM FILMS AS f " +
                        "LEFT OUTER JOIN FILM_LIKES AS fl ON fl.FILM_ID = f.FILM_ID " +
                        "LEFT OUTER JOIN MPA AS m ON m.MPA_ID = f.MPA_ID " +
                        "GROUP BY f.FILM_ID " +
                        "ORDER BY fl.FILM_ID DESC " +
                        "LIMIT(?)",
                (rs, rowNum) ->
                        createFilmModel(rs.getInt("FILM_ID"),
                                rs.getString("NAME"),
                                rs.getString("DESCRIPTION"),
                                rs.getDate("RELEASEDATE").toLocalDate(),
                                rs.getInt("DURATION"),
                                rs.getInt("MPA_ID"),
                                rs.getString("MPA_NAME")
                        ),
                count, count);

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

    @Override
    public List<Film> showFilmRecommendations(int userId) {
        List<Integer> idUsersWithCommonInterests = jdbcTemplate.queryForList(    /// id юзеров, которые ставили лайки тем же фильмам, что и интересующий юзер
                "SELECT FL2.USER_ID " +
                        "FROM FILM_LIKES AS FL1 " +
                        "JOIN FILM_LIKES AS FL2 " +
                        "ON FL1.FILM_ID = FL2.FILM_ID " +
                        "WHERE FL1.USER_ID = ? AND  FL1.USER_ID != FL2.USER_ID " +
                        "GROUP BY FL2.USER_ID " +
                        "ORDER BY COUNT(FL2.USER_ID) DESC", Integer.class, userId
        );

        if (idUsersWithCommonInterests.isEmpty()) {
            return new ArrayList<>();
        }

        List<Film> recommendedFilms = jdbcTemplate.query(
                "SELECT * " +
                        "FROM FILMS f " +
                        "JOIN MPA m ON f.MPA_ID= m.MPA_ID " +
                        "JOIN FILM_LIKES fl ON fl.FILM_ID = f.FILM_ID " +
                        "WHERE f.FILM_ID NOT IN ( " +
                        "SELECT FILM_ID " +
                        "FROM FILM_LIKES " +
                        "WHERE USER_ID = ?) AND fl.USER_ID = ?",
                (rs, rowNum) ->
                        createFilmModel(rs.getInt("FILM_ID"),
                                rs.getString("NAME"),
                                rs.getString("DESCRIPTION"),
                                rs.getDate("RELEASEDATE").toLocalDate(),
                                rs.getInt("DURATION"),
                                rs.getInt("MPA_ID"),
                                rs.getString("MPA_NAME")
                        ),
                userId, idUsersWithCommonInterests.get(0));

        setGenresForFilmIdList(recommendedFilms);

        return recommendedFilms;
    }
}
