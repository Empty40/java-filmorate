package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDateTime;

@SpringBootTest
public class UserControllerTest {

    static User user;
    static UserController userController;

    @BeforeEach
    public void beforeEach() {
        user = new User();
        userController = new UserController();
    }

    @Test
    void validationUserNameTest() throws Exception {
        LocalDateTime testBirthday = LocalDateTime.of(1990,03, 25, 0, 0, 0);
        user.setName("");
        user.setLogin("dolore");
        user.setBirthday(testBirthday);
        user.setEmail("mail@mail.ru");
        userController.createUser(user);
        Assertions.assertEquals(1, userController.allUsers(),
                "Пользователь должен был добавиться, " +
                        "проверьте корректность проверки условий имени пользователя");

        user.setName(null);
        userController.createUser(user);
        Assertions.assertEquals(2, userController.allUsers(),
                "Пользователь должен был добавиться, " +
                        "проверьте корректность проверки условий имени пользователя");
    }

    @Test
    void validationUserEmailTest() {
        LocalDateTime testBirthday = LocalDateTime.of(1990,03, 25, 0, 0, 0);
        user.setName("Nick Name");
        user.setLogin("dolore");
        user.setBirthday(testBirthday);
        user.setEmail("");
        try {
            userController.createUser(user);
        } catch (ValidationException e) {
            Assertions.assertEquals(0, userController.allUsers(),
                    "Пользователь не должен был добавиться, проверьте корректность проверки условий почты");
        }

        user.setEmail(" ");
        try {
            userController.createUser(user);
        } catch (ValidationException e) {
            Assertions.assertEquals(0, userController.allUsers(),
                    "Пользователь не должен был добавиться, проверьте корректность проверки условий почты");
        }

        user.setEmail(null);
        try {
            userController.createUser(user);
        } catch (ValidationException e) {
            Assertions.assertEquals(0, userController.allUsers(),
                    "Пользователь не должен был добавиться, проверьте корректность проверки условий почты");
        }
    }

    @Test
    void validationUserLoginTest() {
        LocalDateTime testBirthday = LocalDateTime.of(1990,03, 25, 0, 0, 0);
        user.setName("Nick Name");
        user.setLogin("");
        user.setBirthday(testBirthday);
        user.setEmail("mail@mail.ru");
        try {
            userController.createUser(user);
        } catch (ValidationException e) {
            Assertions.assertEquals(0, userController.allUsers(),
                    "Пользователь не должен был добавиться, проверьте корректность проверки условий логина");
        }

        user.setLogin(" ");
        try {
            userController.createUser(user);
        } catch (ValidationException e) {
            Assertions.assertEquals(0, userController.allUsers(),
                    "Пользователь не должен был добавиться, проверьте корректность проверки условий логина");
        }

        user.setLogin(null);
        try {
            userController.createUser(user);
        } catch (ValidationException e) {
            Assertions.assertEquals(0, userController.allUsers(),
                    "Пользователь не должен был добавиться, проверьте корректность проверки условий логина");
        }
    }

    @Test
    void validationUserBirthDayTest() {
        LocalDateTime testBirthday = LocalDateTime.now().plusMonths(1);
        user.setName("Nick Name");
        user.setLogin("dolore");
        user.setBirthday(testBirthday);
        user.setEmail("mail@mail.ru");
        try {
            userController.createUser(user);
        } catch (ValidationException e) {
            Assertions.assertEquals(0, userController.allUsers(),
                    "Пользователь не должен был добавиться, проверьте корректность проверки условий дня рождения");
        }

        user.setBirthday(null);
        try {
            userController.createUser(user);
        } catch (ValidationException e) {
            Assertions.assertEquals(0, userController.allUsers(),
                    "Пользователь не должен был добавиться, проверьте корректность проверки условий дня рождения");
        }
    }
}
