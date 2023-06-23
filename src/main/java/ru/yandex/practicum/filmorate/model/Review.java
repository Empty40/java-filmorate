package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
public class Review {

    private int reviewId;

    private String content;

    private Boolean isPositive;

    private int userId;

    private int filmId;

    private int useful = 0;

    public Review(int reviewId, @NonNull String content, @NonNull Boolean isPositive, @NonNull Integer userId,
                  @NonNull Integer filmId, int useful) {
        this.reviewId = reviewId;
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
        this.useful = useful;
    }

    public void addLikeToReview() {
        useful++;
    }

}
