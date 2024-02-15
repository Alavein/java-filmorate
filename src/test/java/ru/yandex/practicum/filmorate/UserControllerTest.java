package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

public class UserControllerTest {
    private final UserController controller = new UserController();
    private final User user = new User(1, "mail@mail.ru", "login", "name",
            LocalDate.of(1995, 10, 7));

    @DisplayName("Добавление нового пользователя")
    @Test
    void createUser() {
        controller.createUser(user);
        assertEquals(1, controller.getAllUsers().size());
    }

    @DisplayName("Обновление данных пользователя")
    @Test
    void updateUser() {
        User updateUser = new User(1, "newMail@mail.ru", "newLogin", "newName",
                LocalDate.of(1966, 11, 5));

        controller.createUser(user);
        controller.updateUser(updateUser);

        assertEquals("newMail@mail.ru", updateUser.getEmail());
        assertEquals("newLogin", updateUser.getLogin());
        assertEquals("newName", updateUser.getName());
        assertEquals(LocalDate.of(1966, 11, 5), updateUser.getBirthday());

    }

    @DisplayName("Не указана почта")
    @Test
    void emailEmpty() {
        user.setEmail("");
        assertEquals("Ошибка. Почта указана некорректно.",
                (assertThrows(ValidationException.class, () -> controller.createUser(user))).getMessage());
        assertEquals(0, controller.getAllUsers().size());
    }

    @DisplayName("Некорректная почта")
    @Test
    void emailIncorrect() {
        user.setEmail("мэйл.мэйл.ру");
        assertEquals("Ошибка. Почта указана некорректно.",
                (assertThrows(ValidationException.class, () -> controller.createUser(user))).getMessage());
        assertEquals(0, controller.getAllUsers().size());
    }


    @DisplayName("День рождения не указан")
    @Test
    void userBirthdayInFuture() {
        user.setBirthday(LocalDate.of(2222, 12, 12));
        assertEquals("Ошибка. Некорректно указана дата рождения.",
                (assertThrows(ValidationException.class, () -> controller.createUser(user))).getMessage());
        assertEquals(0, controller.getAllUsers().size());
    }

    @DisplayName("Логин не указан")
    @Test
    void userLoginEmpty() {
        user.setLogin("");
        assertEquals("Ошибка. Логин не может быть пустым.",
                (assertThrows(ValidationException.class, () -> controller.createUser(user))).getMessage());
        assertEquals(0, controller.getAllUsers().size());
    }

    @DisplayName("Логин с пробелами")
    @Test
    void userLoginSpace() {
        user.setLogin("Пара Па");
        assertEquals("Ошибка. Логин не должен содержать пробелы.",
                (assertThrows(ValidationException.class, () -> controller.createUser(user))).getMessage());
        assertEquals(0, controller.getAllUsers().size());
    }
}
