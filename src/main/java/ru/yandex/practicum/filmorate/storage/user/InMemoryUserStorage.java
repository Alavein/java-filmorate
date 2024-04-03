package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int generatorUserId = 0;

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

    @Override
    public User createUser(User user) {
        userValidation(user);
        log.info("Добавляем пользователя: {}", user);
        user.setId(generateUserId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        userValidation(user);
        log.info("Обновляем данные пользователя: {}", user);
        Integer id = user.getId();
        if (!users.containsKey(id)) {
            log.info("Ошибка. Пользователь не найден: {}", user);
            throw new DataNotFoundException("Ошибка. Пользователь не найден");
        }
        users.put(id, user);
        log.info("Данные о пользователе успешно обновлены: {}", user);
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        log.info("Список пользователей.");
        return users.values();
    }

    @Override
    public User getUserById(int id) {
        User user = users.get(id);
        if (user == null) {
            throw new DataNotFoundException("Ошибка. Пользователь не найден.");
        }
        log.info("Данные о пользователе с id: '{}' успешно найдены", id);
        return user;
    }

    private int generateUserId() {
        return ++generatorUserId;
    }
}
