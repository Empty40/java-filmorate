package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@Slf4j
public class ReviewsController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewsController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review addReview(@RequestBody @Valid Review review) {
        log.info("Добавление отзыва");
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody Review review) {
        log.info("Обновление отзыва");
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable int id) {
        log.info("Удаление отзыва");
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable int id) {
        log.info("Получение отзыва по айди");
        return reviewService.getReview(id);
    }

    @GetMapping()
    public List<Review> getFilmsReviews(@RequestParam(defaultValue = "0") Integer filmId,
                                        @RequestParam(defaultValue = "10") int count) {
        log.info("Получение отзывов");
        return reviewService.getFilmsReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToReview(@PathVariable int id, @PathVariable int userId) {
        log.info("Добавление лайка к отзыву");
        reviewService.addLikeToReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable int id, @PathVariable int userId) {
        log.info("Добавление дизлайка к отзыву");
        reviewService.addDislikeToReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeToReview(@PathVariable int id, @PathVariable int userId) {
        log.info("Удаление лайка с отзыва");
        reviewService.deleteLikeToReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislikeToReview(@PathVariable int id, @PathVariable int userId) {
        log.info("Удаление дизлайка с отзыва");
        reviewService.deleteDislikeToReview(id, userId);
    }
}