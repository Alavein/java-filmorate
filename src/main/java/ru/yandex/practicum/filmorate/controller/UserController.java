package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private int generatorUserId = 0;

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Добавляем пользователя: {}", user);
        userValidation(user);
        user.setId(generateUserId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен: {}", user);
        return user;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Список пользователей");
        return users.values();
    }


    @PutMapping
    public User updateUser(@RequestBody User user) {
        userValidation(user);
        log.info("Обновляем данные пользователя: {}", user);
        Integer id = user.getId();
        if (!users.containsKey(id)) {
            log.info("Ошибка. Пользователь не найден: {}", user);
            throw new ValidationException("Ошибка. Пользователь не найден");
        }
        users.put(id, user);
        log.info("Данные о пользователе успешно обновлены: {}", user);
        return user;
    }

    private void userValidation(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Ошибка. Почта указана некорректно.");
            throw new ValidationException("Ошибка. Почта указана некорректно.");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка. Некорректно указана дата рождения.");
            throw new ValidationException("Ошибка. Некорректно указана дата рождения.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Ошибка. Логин не может быть пустым.");
            throw new ValidationException("Ошибка. Логин не может быть пустым.");
        }
        if (user.getLogin().contains(" ")) {
            log.warn("Ошибка. Логин не должен содержать пробелы.");
            throw new ValidationException("Ошибка. Логин не должен содержать пробелы.");
        }
    }

    private int generateUserId() {
        return ++generatorUserId;
    }
}
