package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewDao reviewDao;
    private final EventDao eventDao;

    public ReviewService(ReviewDao reviewDao, EventDao eventDao) {
        this.reviewDao = reviewDao;
        this.eventDao = eventDao;
    }

    public Review addReview(Review review) {
        eventDao.addEvent(new Event("ADD", "REVIEW", review.getUserId(), review.getReviewId()));
        return reviewDao.addReview(review);
    }

    public Review updateReview(Review review) {
        eventDao.addEvent(new Event("UPDATE", "REVIEW", review.getUserId(), review.getReviewId()));
        return reviewDao.updateReview(review);
    }

    public void deleteReview(int id) {
        eventDao.addEvent(new Event("REMOVE", "REVIEW", getReview(id).getUserId(), id));
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

    public void deleteDislikeToReview(int id, int userId) {
        reviewDao.deleteDislikeToReview(id, userId);
    }
}
