package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@SpringBootTest
class FilmControllerTests {

    static Film film;
    static FilmController filmController;

    @BeforeEach
    public void beforeEach() {
        film = new Film();
        filmController = new FilmController();
    }

    @Test
    void validationFilmNameTest() {
        LocalDate testTime = LocalDate.of(1967, 3, 25);
        film.setName(null);
        film.setDescription("adipisicing");
        film.setReleaseDate(testTime);
        film.setDuration(100);

        ValidationException nameExceptionOne = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        Assertions.assertEquals("Введено некорректное название фильма", nameExceptionOne.getMessage(),
                "Ошибка произошла не на наименовании фильма, проверьте корректность валидации");

        film.setName("");
        ValidationException nameExceptionTwo = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        Assertions.assertEquals("Введено некорректное название фильма", nameExceptionTwo.getMessage(),
                "Ошибка произошла не на наименовании фильма, проверьте корректность валидации");

        film.setName(" ");
        ValidationException nameExceptionThree = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        Assertions.assertEquals("Введено некорректное название фильма", nameExceptionThree.getMessage(),
                "Ошибка произошла не на наименовании фильма, проверьте корректность валидации");
    }

    @Test
    void validationFilmDescriprionTest() {
        LocalDate testTime = LocalDate.of(1967, 3, 25);
        film.setName("nisi eiusmod");
        film.setDescription(null);
        film.setReleaseDate(testTime);
        film.setDuration(100);

        ValidationException descriptionExceptionOne = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        Assertions.assertEquals("Введено некорректное описание фильма", descriptionExceptionOne.getMessage(),
                "Ошибка произошла не на наименовании фильма, проверьте корректность валидации");

        film.setDescription("");
        ValidationException descriptionExceptionTwo = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        Assertions.assertEquals("Введено некорректное описание фильма", descriptionExceptionTwo.getMessage(),
                "Ошибка произошла не на наименовании фильма, проверьте корректность валидации");

        film.setDescription(" ");
        ValidationException descriptionExceptionThree = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        Assertions.assertEquals("Введено некорректное описание фильма", descriptionExceptionThree.getMessage(),
                "Ошибка произошла не на наименовании фильма, проверьте корректность валидации");

        film.setDescription("qweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxc" +
                "qweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxc" +
                "qweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasd");
        ValidationException descriptionExceptionFour = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        Assertions.assertEquals("Введено некорректное описание фильма", descriptionExceptionFour.getMessage(),
                "Ошибка произошла не на наименовании фильма, проверьте корректность валидации");
    }

    @Test
    void validationFilmReleaseDataTest() {
        LocalDate testTime = LocalDate.of(1880, 3, 25);
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(testTime);
        film.setDuration(100);

        ValidationException releaseDateExceptionOne = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        Assertions.assertEquals("Дата выхода фильма раньше чем - 1895-12-28",
                releaseDateExceptionOne.getMessage(),
                "Ошибка произошла не на наименовании фильма, проверьте корректность валидации");

        film.setReleaseDate(null);
        ValidationException releaseDateExceptionTwo = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        Assertions.assertEquals("Дата выхода фильма раньше чем - 1895-12-28",
                releaseDateExceptionTwo.getMessage(),
                "Ошибка произошла не на наименовании фильма, проверьте корректность валидации");
    }

}
