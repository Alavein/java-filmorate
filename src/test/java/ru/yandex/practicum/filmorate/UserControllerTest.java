package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

@SpringBootTest
public class UserControllerTest {
    @Autowired
    private UserController controller;
    User user = new User(1, "mail@mail.ru", "login",
            "Name", LocalDate.of(1995, 10, 7), new HashSet<>());
    User user1 = new User(2, "mail2@mail.ru", "login2",
            "Name2", LocalDate.of(1994, 2, 18), new HashSet<>());
    User user2 = new User(2, "mail3@mail.ru", "login3",
            "Общий друг", LocalDate.of(1994, 5, 10), new HashSet<>());

    @DisplayName("Обновление данных пользователя")
    @Test
    void updateUser() {

        User updateUser = new User(1, "newEmail@mail.ru", "newLogin",
                "newName", LocalDate.of(1995, 10, 11), new HashSet<>());
        controller.createUser(user);
        controller.updateUser(updateUser);

        assertEquals("newEmail@mail.ru", updateUser.getEmail());
        assertEquals("newLogin", updateUser.getLogin());
        assertEquals(LocalDate.of(1995, 10, 11), updateUser.getBirthday());
        assertEquals("newName", updateUser.getName());
    }

    @DisplayName("Некорректное заполнение почты (пустая почта).")
    @Test
    void emailEmpty() {
        user.setEmail("");
        assertEquals("Ошибка. Почта указана некорректно.",
                (assertThrows(ValidationException.class, () -> controller.createUser(user))).getMessage());
    }

    @DisplayName("Некорректное заполнение почты (неправильный формат).")
    @Test
    void emailIncorrect() {
        user.setEmail("emailmail.ru");
        assertEquals("Ошибка. Почта указана некорректно.",
                (assertThrows(ValidationException.class, () -> controller.createUser(user))).getMessage());
    }

    @DisplayName("Некорректное заполнение логина (пустой логин).")
    @Test
    void userLoginEmpty() {
        user.setLogin("");
        assertEquals("Ошибка. Логин не может быть пустым.",
                (assertThrows(ValidationException.class, () -> controller.createUser(user))).getMessage());
    }

    @DisplayName("Некорректное заполнение логина (логин с пробелами).")
    @Test
    void userLoginSpace() {
        user.setLogin("Log in");
        assertEquals("Ошибка. Логин не должен содержать пробелы.",
                (assertThrows(ValidationException.class, () -> controller.createUser(user))).getMessage());
    }

    @DisplayName("Некорректное заполнение даты рождения (день рождения не указан).")
    @Test
    void userBirthdayInFuture() {
        user.setBirthday(LocalDate.of(2024, 10, 10));
        assertEquals("Ошибка. Некорректно указана дата рождения.",
                (assertThrows(ValidationException.class, () -> controller.createUser(user))).getMessage());
    }

    @DisplayName("Добавление друга.")
    @Test
    void addFriend() {
        controller.createUser(user);
        controller.createUser(user1);
        controller.addFriend(user.getId(), user1.getId());

        assertEquals(1, user.getFriends().size());
        assertEquals(1, user1.getFriends().size());
    }

    @DisplayName("Удаление из друзей.")
    @Test
    void deleteFriend() {
        controller.createUser(user);
        controller.createUser(user1);
        controller.addFriend(user.getId(), user1.getId());
        controller.removeFriend(user.getId(), user1.getId());

        assertEquals(0, user.getFriends().size());
        assertEquals(0, user1.getFriends().size());
    }

    @DisplayName("Список общих друзей.")
    @Test
    void getCommonFriends() {
        controller.createUser(user);
        controller.createUser(user1);
        controller.createUser(user2);
        controller.addFriend(user.getId(), user2.getId());
        controller.addFriend(user1.getId(), user2.getId());
        List<User> commonFriendList = controller.getCommonFriends(user.getId(), user1.getId());
        assertEquals(1, commonFriendList.size());
        commonFriendList = controller.getCommonFriends(user.getId(), user2.getId());
        assertEquals(0, commonFriendList.size());
    }

    @DisplayName("Список друзей.")
    @Test
    void getFriends() {
        controller.createUser(user);
        controller.createUser(user1);
        controller.createUser(user2);
        controller.addFriend(user.getId(), user1.getId());
        controller.addFriend(user.getId(), user2.getId());
        List<User> listUserFriends = controller.getFriendsList(user.getId());

        assertEquals(2, listUserFriends.size());
    }
}
