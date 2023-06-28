package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
public class ReviewDaoImpl implements ReviewDao {

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
        log.debug("id = {} Отзыв добавлен", review.getReviewId());
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        validationReviews(review);
        String sqlQuery = "UPDATE REVIEWS SET CONTENT = ?, ISPOSITIVE = ?" +
                " WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        log.debug("Отзыв c id = {} был обновлён", review.getReviewId());
        return getReview(review.getReviewId());
    }

    @Override
    public void deleteReview(int id) {
        jdbcTemplate.update("DELETE FROM REVIEWS where REVIEW_ID = ?",
                id);
        log.debug("Отзыв c id = {} был удалён", id);
    }

    @Override
    public Review getReview(int id) {
        List<Review> reviewList;

        reviewList = jdbcTemplate.query("SELECT *, FROM REVIEWS WHERE REVIEW_ID = ? ",
                ReviewDaoImpl::createReview,
                id);

        if (reviewList.isEmpty()) {
            throw new NotFoundException("Введен некорректный идентификатор отзыва");
        } else {
            log.debug("Был получен отзыв с id = {}", id);
            return reviewList.get(0);
        }
    }

    @Override
    public List<Review> getFilmsReviews(Integer filmId, int count) {
        List<Review> filmsReviewList;

        if (filmId == 0) {
            filmsReviewList = jdbcTemplate.query("SELECT *, FROM REVIEWS GROUP BY REVIEW_ID" +
                            " ORDER BY USEFUL DESC LIMIT(?)",
                    ReviewDaoImpl::createReview,
                    count);
            log.debug("Были получены все отзывы");
        } else {
            filmsReviewList = jdbcTemplate.query("SELECT *, FROM REVIEWS WHERE FILM_ID = (?) " +
                            "ORDER BY USEFUL DESC LIMIT(?)",
                    ReviewDaoImpl::createReview,
                    filmId, count);
            log.debug("Были получены отзывы фильма = {}", filmId);
        }
        return filmsReviewList;
    }

    private static Review createReview(ResultSet rs, int rowNum) throws SQLException {
        return new Review(rs.getInt("REVIEW_ID"),
                rs.getString("CONTENT"),
                rs.getBoolean("ISPOSITIVE"),
                rs.getInt("USER_ID"),
                rs.getInt("FILM_ID"),
                rs.getInt("USEFUL"));
    }

    @Override
    public void addLikeToReview(int id, int userId) {
        checkMaxUserId(userId);
        String sqlQueryReview = "UPDATE REVIEWS SET USEFUL = USEFUL + 1 " +
                "WHERE REVIEW_ID = (?)";
        jdbcTemplate.update(sqlQueryReview,
                id);
        String sqlQueryLike = "INSERT INTO REVIEW_LIKES(REVIEW_ID, USER_ID) VALUES(?, ?)";
        jdbcTemplate.update(sqlQueryLike, id, userId);
        log.debug("Добавлен лайк отзыву = {}", id);
    }

    @Override
    public void addDislikeToReview(int id, int userId) {
        checkMaxUserId(userId);
        String sqlQueryReview = "UPDATE REVIEWS SET USEFUL = USEFUL - 1 " +
                "WHERE REVIEW_ID = (?)";
        jdbcTemplate.update(sqlQueryReview,
                id);
        String sqlQueryLike = "INSERT INTO REVIEW_DISLIKES(REVIEW_ID, USER_ID) VALUES(?, ?)";
        jdbcTemplate.update(sqlQueryLike, id, userId);
        log.debug("Добавлен дизлайк отзыву = {}", id);
    }

    @Override
    public void deleteLikeToReview(int id, int userId) {
        checkMaxUserId(userId);
        String sqlQueryReview = "UPDATE REVIEWS SET USEFUL = USEFUL - 1 " +
                "WHERE REVIEW_ID = (?)";
        jdbcTemplate.update(sqlQueryReview,
                id);
        jdbcTemplate.update("DELETE FROM REVIEW_LIKES where REVIEW_ID = ? AND USER_ID = ?",
                id, userId);
        log.debug("Удалён лайк отзыву = {}", id);
    }

    @Override
    public void deleteDislikeToReview(int id, int userId) {
        checkMaxUserId(userId);
        String sqlQueryReview = "UPDATE REVIEWS SET USEFUL = USEFUL + 1 " +
                "WHERE REVIEW_ID = (?)";
        jdbcTemplate.update(sqlQueryReview,
                id);
        jdbcTemplate.update("DELETE FROM REVIEW_DISLIKES where REVIEW_ID = ? AND USER_ID = ?",
                id, userId);
        log.debug("Удалён дизлайк отзыву = {}", id);
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
