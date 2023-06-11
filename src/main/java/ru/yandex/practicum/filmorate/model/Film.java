package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
public class Film {

    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private List<Genres> genres = new ArrayList<>();
    private Mpa mpa;

    public Film(int id, String name, String description, LocalDate releaseDate, int duration,
                HashMap<String, Integer> mpa, List<HashMap<String, Integer>> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = new Mpa(mpa.get("id"), "");

        if (genres != null && genres.size() != 0) {
            if (genres.size() > 1) {
                for (HashMap<String, Integer> genre : genres) {
                    this.genres.add(new Genres(genre.get("id"), ""));
                }
            } else {
                this.genres.add(new Genres(genres.get(0).get("id"), ""));
            }
        }
    }
}
