package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDao {

    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReview(int id);

    Review getReview(int id);

    List<Review> getFilmsReviews(Integer filmId, int count);

    void addLikeToReview(int id, int userId);

    void addDislikeToReview(int id, int userId);

    void deleteLikeToReview(int id, int userId);

    void deleteDislikeToReview(int id, int userId);
}
