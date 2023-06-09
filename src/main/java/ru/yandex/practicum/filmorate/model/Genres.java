package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Genres {
    private Integer id;
    private String name;

    public Genres(int id) {
        this.id = id;
    }
}
