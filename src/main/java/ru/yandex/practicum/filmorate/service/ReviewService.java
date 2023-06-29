package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.Entity;
import ru.yandex.practicum.filmorate.model.enums.Operation;

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
        Review review1 = reviewDao.addReview(review);
        eventDao.addEvent(new Event(Operation.ADD, Entity.REVIEW, review1.getUserId(), review1.getReviewId()));
        return review1;
    }

    public Review updateReview(Review review) {
        Review reviewUpdate = reviewDao.updateReview(review);
        eventDao.addEvent(new Event(Operation.UPDATE, Entity.REVIEW, reviewUpdate.getUserId(),
                reviewUpdate.getReviewId()));
        return reviewUpdate;
    }

    public void deleteReview(int id) {
        eventDao.addEvent(new Event(Operation.REMOVE, Entity.REVIEW, getReview(id).getUserId(), id));
        reviewDao.deleteReview(id);
    }

    public Review getReview(int id) {
        return reviewDao.getReview(id);
    }

    public List<Review> getFilmsReviews(Integer filmId, int count) {
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
