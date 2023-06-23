package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewDao reviewDao;

    public ReviewService(ReviewDao reviewDao) {
        this.reviewDao = reviewDao;
    }

    public Review addReview(Review review) {
        return reviewDao.addReview(review);
    }

    public Review updateReview(Review review) {
        return reviewDao.updateReview(review);
    }

    public void deleteReview(int id) {
        reviewDao.deleteReview(id);
    }

    public Review getReview(int id) {
        return reviewDao.getReview(id);
    }

    public List<Review> getFilmsReviews(String filmId, int count) {
        return reviewDao.getFilmsReviews(filmId, count);
    }

    public void addLikeToReview(int id, int userId) {
        reviewDao.addLikeToReview(id, userId);
    }

    public void addDislikeToReview(int id, int userId) {
        reviewDao.addDislikeToReview(id, userId);
    }

    public void deleteLikeToReview(int id, int userId) {
        reviewDao.deleteLikeToReview(id, userId);
    }

    public void deleteDisikeToReview(int id, int userId) {
        reviewDao.deleteDisikeToReview(id, userId);
    }


}
