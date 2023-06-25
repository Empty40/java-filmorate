package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.util.List;

@Component
public class ReviewDaoImpl implements ru.yandex.practicum.filmorate.dao.ReviewDao {

    private final JdbcTemplate jdbcTemplate;

    public ReviewDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review addReview(Review review) {
        validationReviews(review);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sqlQuery = "INSERT INTO REVIEWS(CONTENT, ISPOSITIVE, USER_ID, FILM_ID, USEFUL) " +
                "VALUES(?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sqlQuery, new String[]{"REVIEW_ID"});
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setInt(3, review.getUserId());
            ps.setInt(4, review.getFilmId());
            ps.setInt(5, review.getUseful());
            return ps;
        }, keyHolder);

        review.setReviewId(keyHolder.getKey().intValue());

        return review;
    }

    @Override
    public Review updateReview(Review review) {
        checkMaxReviewId(review.getReviewId());
        validationReviews(review);
        String sqlQuery = "UPDATE REVIEWS SET CONTENT = ?, ISPOSITIVE = ?" +
                " WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        return getReview(review.getReviewId());
    }

    @Override
    public void deleteReview(int id) {
        checkMaxReviewId(id);
        jdbcTemplate.update("DELETE FROM REVIEWS where REVIEW_ID = ?",
                id);
    }

    @Override
    public Review getReview(int id) {
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet("SELECT *" +
                "FROM REVIEWS " +
                "WHERE REVIEW_ID = ?", id);

        checkMaxReviewId(id);

        Review review = null;

        if (reviewRows.next()) {
            review = createReviewModel(reviewRows.getInt("REVIEW_ID"),
                    reviewRows.getString("CONTENT"), reviewRows.getBoolean("ISPOSITIVE"),
                    reviewRows.getInt("USER_ID"), reviewRows.getInt("FILM_ID"),
                    reviewRows.getInt("USEFUL"));
        }

        if (review == null) {
            throw new NotFoundException("Введен некорректный идентификатор отзыва");
        }

        return review;
    }

    @Override
    public List<Review> getFilmsReviews(String filmId, int count) {
        List<Review> filmsReviewList;

        if (filmId.equals("*")) {
            filmsReviewList = jdbcTemplate.query("SELECT *, FROM REVIEWS GROUP BY REVIEW_ID" +
                            " ORDER BY USEFUL DESC LIMIT(?)",
                    (rs, rowNum) ->
                            createReviewModel(rs.getInt("REVIEW_ID"),
                                    rs.getString("CONTENT"),
                                    rs.getBoolean("ISPOSITIVE"),
                                    rs.getInt("USER_ID"),
                                    rs.getInt("FILM_ID"),
                                    rs.getInt("USEFUL")
                            ),
                    count);
        } else {
            filmsReviewList = jdbcTemplate.query("SELECT *, FROM REVIEWS WHERE FILM_ID = (?) " +
                            "ORDER BY USEFUL DESC LIMIT(?)",
                    (rs, rowNum) ->
                            createReviewModel(rs.getInt("REVIEW_ID"),
                                    rs.getString("CONTENT"),
                                    rs.getBoolean("ISPOSITIVE"),
                                    rs.getInt("USER_ID"),
                                    rs.getInt("FILM_ID"),
                                    rs.getInt("USEFUL")
                            ),
                    filmId, count);
        }
        return filmsReviewList;
    }

    private Review createReviewModel(int reviewId, String content, boolean ispositive,
                                     int userId, int filmId, int useful) {
        Review review;

        review = new Review(
                reviewId,
                content,
                ispositive,
                userId,
                filmId,
                useful);

        return review;
    }

    @Override
    public void addLikeToReview(int id, int userId) {
        checkMaxReviewId(id);
        checkMaxUserId(userId);
        String sqlQueryGenres = "UPDATE REVIEWS SET USEFUL = USEFUL + 1 " +
                "WHERE REVIEW_ID = (?)";
        jdbcTemplate.update(sqlQueryGenres,
                id);
    }

    @Override
    public void addDislikeToReview(int id, int userId) {
        checkMaxReviewId(id);
        checkMaxUserId(userId);
        String sqlQueryGenres = "UPDATE REVIEWS SET USEFUL = USEFUL - 1 " +
                "WHERE REVIEW_ID = (?)";
        jdbcTemplate.update(sqlQueryGenres,
                id);
    }

    @Override
    public void deleteLikeToReview(int id, int userId) {
        checkMaxReviewId(id);
        checkMaxUserId(userId);
        String sqlQueryGenres = "UPDATE REVIEWS SET USEFUL = USEFUL - 1 " +
                "WHERE REVIEW_ID = (?)";
        jdbcTemplate.update(sqlQueryGenres,
                id);
    }

    @Override
    public void deleteDislikeToReview(int id, int userId) {
        checkMaxReviewId(id);
        checkMaxUserId(userId);
        String sqlQueryGenres = "UPDATE REVIEWS SET USEFUL = USEFUL + 1 " +
                "WHERE REVIEW_ID = (?)";
        jdbcTemplate.update(sqlQueryGenres,
                id);
    }

    private void checkMaxReviewId(int id) {
        SqlRowSet reviewIdRows = jdbcTemplate.queryForRowSet("SELECT MAX(REVIEW_ID) FROM REVIEWS");
        int maxId = 0;
        if (reviewIdRows.next()) {
            maxId = reviewIdRows.getInt("MAX(REVIEW_ID)");
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

    private void validationReviews(Review review) {
        if (review.getUserId() <= 0) {
            throw new NotFoundException("Введен некорректный идентификатор пользователя");
        }
        if (review.getFilmId() <= 0) {
            throw new NotFoundException("Введен некорректный идентификатор фильма");
        }
    }
}
