package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

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
    void validationUserNameTest() {
        LocalDate testBirthday = LocalDate.of(1990,3, 25);
        user.setName("");
        user.setLogin("dolore");
        user.setBirthday(testBirthday);
        user.setEmail("mail@mail.ru");
        userController.createUser(user);
        Assertions.assertEquals(user, userController.allUsers().get(0),
                "Пользователь должен был добавиться, " +
                        "проверьте корректность проверки условий имени пользователя");

        user.setName(null);
        userController.createUser(user);
        Assertions.assertEquals(user, userController.allUsers().get(1),
                "Пользователь должен был добавиться, " +
                        "проверьте корректность проверки условий имени пользователя");
    }

    @Test
    void validationUserEmailTest() {
        LocalDate testBirthday = LocalDate.of(1990,3, 25);
        user.setName("Nick Name");
        user.setLogin("dolore");
        user.setBirthday(testBirthday);
        user.setEmail("");

        ValidationException userEmailExceptionOne = Assertions.assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
        Assertions.assertEquals("Был введен некорректный E-mail", userEmailExceptionOne.getMessage(),
                "Ошибка произошла не на E-mail, проверьте корректность валидации");

        user.setEmail(" ");
        ValidationException userEmailExceptionTwo = Assertions.assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
        Assertions.assertEquals("Был введен некорректный E-mail", userEmailExceptionTwo.getMessage(),
                "Ошибка произошла не на E-mail, проверьте корректность валидации");

        user.setEmail(null);
        ValidationException userEmailExceptionThree = Assertions.assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
        Assertions.assertEquals("Был введен некорректный E-mail", userEmailExceptionThree.getMessage(),
                "Ошибка произошла не на E-mail, проверьте корректность валидации");
    }

    @Test
    void validationUserLoginTest() {
        LocalDate testBirthday = LocalDate.of(1990,3, 25);
        user.setName("Nick Name");
        user.setLogin("");
        user.setBirthday(testBirthday);
        user.setEmail("mail@mail.ru");

        ValidationException userLoginExceptionOne = Assertions.assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
        Assertions.assertEquals("Был введен некорректный логин", userLoginExceptionOne.getMessage(),
                "Ошибка произошла не на указании логина, проверьте корректность валидации");

        user.setLogin(" ");
        ValidationException userLoginExceptionTwo = Assertions.assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
        Assertions.assertEquals("Был введен некорректный логин", userLoginExceptionTwo.getMessage(),
                "Ошибка произошла не на указании логина, проверьте корректность валидации");

        user.setLogin(null);
        ValidationException userLoginExceptionThree = Assertions.assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
        Assertions.assertEquals("Был введен некорректный логин", userLoginExceptionThree.getMessage(),
                "Ошибка произошла не на указании логина, проверьте корректность валидации");
    }

    @Test
    void validationUserBirthDayTest() {
        LocalDate testBirthday = LocalDate.now().plusMonths(1);
        user.setName("Nick Name");
        user.setLogin("dolore");
        user.setBirthday(testBirthday);
        user.setEmail("mail@mail.ru");

        ValidationException userBirthdayExceptionOne = Assertions.assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
        Assertions.assertEquals("Введенная дата позже чем текущая", userBirthdayExceptionOne.getMessage(),
                "Ошибка произошла не на указании даты рождения, проверьте корректность валидации");

        user.setBirthday(null);
        ValidationException userBirthdayExceptionTwo = Assertions.assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
        Assertions.assertEquals("Введенная дата позже чем текущая", userBirthdayExceptionTwo.getMessage(),
                "Ошибка произошла не на указании даты рождения, проверьте корректность валидации");
    }
}
