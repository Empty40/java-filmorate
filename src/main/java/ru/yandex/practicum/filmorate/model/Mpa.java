package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class Mpa {
    private Integer id;
    @NonNull
    private String name;

    public Mpa(int id) {
        this.id = id;
    }
}
