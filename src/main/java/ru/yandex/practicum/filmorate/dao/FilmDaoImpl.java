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
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
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

        List<HashMap<String, Integer>> genreList = createGenreList(id);

        if (filmRows.next()) {
            int mpaId = filmRows.getInt("MPA_ID");

            film = createFilmModel(filmRows.getInt("FILM_ID"), filmRows.getString("NAME"),
                    filmRows.getString("DESCRIPTION"),
                    filmRows.getDate("RELEASEDATE").toLocalDate(),
                    filmRows.getInt("DURATION"), createMpaHashMap(mpaId), genreList);

            String values = filmRows.getString("MPA_NAME");
            film.getMpa().setName(values);

            getNameForGenres(film);
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

            Film film = createFilmModel(filmRows.getInt("FILM_ID"), filmRows.getString("NAME"),
                    filmRows.getString("DESCRIPTION"),
                    filmRows.getDate("RELEASEDATE").toLocalDate(),
                    filmRows.getInt("DURATION"),
                    createMpaHashMap(filmRows.getInt("MPA_ID")),
                    createGenreList(filmId));

            getNameForGenres(film);

            String values = filmRows.getString("MPA_NAME");
            film.getMpa().setName(values);

            films.add(film);
        }
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

        List<Integer> listIdGenres = new ArrayList<>();
        for (Genres genre : listGenres) {
            listIdGenres.add(genre.getId());
        }

        listGenres.clear();
        listGenres.addAll(getGenresFromIdList(listIdGenres));

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

        jdbcTemplate.update("DELETE FROM FILM_GENRES where FILM_ID = ?", film.getId());

        List<Integer> listIdGenres = new ArrayList<>();
        for (Genres genre : listGenres) {
            listIdGenres.add(genre.getId());
        }
        listGenres.clear();
        listGenres.addAll(getGenresFromIdList(listIdGenres));

        Set<Genres> genres = new LinkedHashSet<>(listGenres);

        listGenres.clear();

        listGenres.addAll(genres);

        batchUpdateTest(film.getId(), listGenres);

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
                        "GROUP BY f.FILM_ID " +
                        "ORDER BY fl.FILM_ID DESC " +
                        "LIMIT(?)",
                (rs, rowNum) ->
                        new Film(rs.getInt("FILM_ID"),
                                rs.getString("NAME"),
                                rs.getString("DESCRIPTION"),
                                rs.getDate("RELEASEDATE").toLocalDate(),
                                rs.getInt("DURATION"),
                                createMpaHashMap(rs.getInt("MPA_ID")),
                                createGenreList(rs.getInt("FILM_ID"))
                        ),
                count, count);

        List<Integer> mpaId = new ArrayList<>();

        for (Film film : mostPopularFilms) {
            mpaId.add(film.getMpa().getId());
        }

        List<Mpa> mpaList = getNameForMpa(mpaId);

        ListIterator<Film> filmListIterator = mostPopularFilms.listIterator();

        ListIterator<Mpa> mpaListIterator = mpaList.listIterator();

        while (filmListIterator.hasNext()) {
            Film film = filmListIterator.next();
            film.setMpa(mpaListIterator.next());
        }
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

    private List<Genres> getGenresFromIdList(List<Integer> ids) {
        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));

        return jdbcTemplate.query(
                String.format("SELECT * FROM GENRES WHERE GENRE_ID IN (%s)", inSql),
                ids.toArray(),
                (rs, rowNum) -> new Genres(rs.getInt("GENRE_ID"), rs.getString("NAME")));
    }

    private HashMap<String, Integer> createMpaHashMap(int mpaId) {
        HashMap<String, Integer> mpaIdMap = new HashMap<>();
        mpaIdMap.put("id", mpaId);
        return mpaIdMap;
    }

    private List<Mpa> getNameForMpa(List<Integer> ids) {
        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));

        return jdbcTemplate.query(
                String.format("SELECT * FROM MPA WHERE MPA_ID IN (%s)", inSql),
                ids.toArray(),
                (rs, rowNum) -> new Mpa(rs.getInt("MPA_ID"), rs.getString("MPA_NAME")));
    }

    private List<HashMap<String, Integer>> createGenreList(int filmId) {
        List<HashMap<String, Integer>> genreList = new ArrayList<>();

        List<Genres> genreIdList;

        genreIdList = jdbcTemplate.query("select * from FILM_GENRES where FILM_ID = ?",
                (rs, rowNum) -> new Genres(rs.getInt("GENRE_ID"), ""), filmId);

        for (Genres genres : genreIdList) {
            HashMap<String, Integer> genreId = new HashMap<>();
            genreId.put("id", genres.getId());
            genreList.add(genreId);
        }
        return genreList;
    }

    private void getNameForGenres(Film film) {
        List<Genres> listGenres = film.getGenres();
        List<Integer> listIdGenres = new ArrayList<>();
        for (Genres genre : listGenres) {
            listIdGenres.add(genre.getId());
        }
        listGenres.clear();
        listGenres.addAll(getGenresFromIdList(listIdGenres));
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
