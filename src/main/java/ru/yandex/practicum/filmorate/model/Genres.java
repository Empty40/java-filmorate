package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class Genres {
    private Integer id;
    @NotBlank
    private String name;

    public Genres(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
