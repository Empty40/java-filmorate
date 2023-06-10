package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class Genres {
    private Integer id;
    @NonNull
    private String name;

    public Genres(int id) {
        this.id = id;
    }
}
