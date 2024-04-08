package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    public UserServiceImpl(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
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

    @Override
    public User createUser(User user) {
        userValidation(user);
        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        userValidation(user);
        userStorage.updateUser(user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    @Override
    public User addFriend(Long id, Long friendId) {
        return userStorage.addFriend(id, friendId);
    }

    @Override
    public void removeFriend(Long id, Long friendId) {
        userStorage.removeFriend(id, friendId);
    }

    @Override
    public List<User> getFriendsList(Long id) {
        return userStorage.getFriendsList(id);
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) throws ValidationException {
        return userStorage.getCommonFriends(id, otherId);
    }
}
