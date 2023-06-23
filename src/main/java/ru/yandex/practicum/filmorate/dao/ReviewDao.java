package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDao {

    public Review addReview(Review review);

    public Review updateReview(Review review);

    public void deleteReview(int id);

    public Review getReview(int id);

    public List<Review> getFilmsReviews(String filmId, int count);

    public void addLikeToReview(int id, int userId);

    public void addDislikeToReview(int id, int userId);

    public void deleteLikeToReview(int id, int userId);

    public void deleteDisikeToReview(int id, int userId);


}
