package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDateTime;

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
	void validationFilmNameTest() throws Exception {
		LocalDateTime testTime = LocalDateTime.of(1967,03, 25, 0, 0, 0);
		film.setName(null);
		film.setDescription("adipisicing");
		film.setReleaseDate(testTime);
		film.setDuration(100);
		try {
			filmController.addFilm(film);
		} catch (ValidationException e) {
			Assertions.assertEquals(0, filmController.allFilms(),
					"Фильм не должен был добавиться, проверьте корректность проверки условий наименования фильма");
		}

		film.setName("");
		try {
			filmController.addFilm(film);
		} catch (ValidationException e) {
			Assertions.assertEquals(0, filmController.allFilms(),
					"Фильм не должен был добавиться, проверьте корректность проверки условий наименования фильма");
		}

		film.setName(" ");
		try {
			filmController.addFilm(film);
		} catch (ValidationException e) {
			Assertions.assertEquals(0, filmController.allFilms(),
					"Фильм не должен был добавиться, проверьте корректность проверки условий наименования фильма");
		}
	}

	@Test
	void validationFilmDescriprionTest() throws Exception {
		LocalDateTime testTime = LocalDateTime.of(1967,03, 25, 0, 0, 0);
		film.setName("nisi eiusmod");
		film.setDescription(null);
		film.setReleaseDate(testTime);
		film.setDuration(100);
		try {
			filmController.addFilm(film);
		} catch (ValidationException e) {
			Assertions.assertEquals(0, filmController.allFilms(),
					"Фильм не должен был добавиться, проверьте корректность проверки условий описания фильма");
		}

		film.setDescription("");
		try {
			filmController.addFilm(film);
		} catch (ValidationException e) {
			Assertions.assertEquals(0, filmController.allFilms(),
					"Фильм не должен был добавиться, проверьте корректность проверки условий описания фильма");
		}

		film.setDescription(" ");
		try {
			filmController.addFilm(film);
		} catch (ValidationException e) {
			Assertions.assertEquals(0, filmController.allFilms(),
					"Фильм не должен был добавиться, проверьте корректность проверки условий описания фильма");
		}

		film.setDescription("qweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxc" +
				"qweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxc" +
				"qweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasdzxcqweasd");
		try {
			filmController.addFilm(film);
		} catch (ValidationException e) {
			Assertions.assertEquals(0, filmController.allFilms(),
					"Фильм не должен был добавиться, проверьте корректность проверки условий описания фильма");
		}
	}

	@Test
	void validationFilmReleaseDataTest() throws Exception {
		LocalDateTime testTime = LocalDateTime.of(1880,03, 25, 0, 0, 0);
		film.setName("nisi eiusmod");
		film.setDescription("adipisicing");
		film.setReleaseDate(testTime);
		film.setDuration(100);
		try {
			filmController.addFilm(film);
		} catch (ValidationException e) {
			Assertions.assertEquals(0, filmController.allFilms(),
					"Фильм не должен был добавиться, проверьте корректность проверки условий даты релиза");
		}

		film.setReleaseDate(null);
		try {
			filmController.addFilm(film);
		} catch (ValidationException e) {
			Assertions.assertEquals(0, filmController.allFilms(),
					"Фильм не должен был добавиться, проверьте корректность проверки условий даты релиза");
		}
	}

}
