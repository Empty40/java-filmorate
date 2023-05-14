package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Set<Integer> userLike = new HashSet<>();

    public Integer getUserLikeCount() {
        return userLike.size();
    }

    public boolean addLike(int id) {
        return userLike.add(id);
    }

    public boolean removeLike(int id) {
        return userLike.remove(id);
    }
}
