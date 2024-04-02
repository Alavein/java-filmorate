package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("inMemoryUserStorage")
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    private final List<User> users = new ArrayList<>();
    private  Long generatedId = 0L;

    @Override
    public User createUser(User user) {
        user.setId(generateIdUser());
        users.add(user);
        return user;
    }


    @Override
    public void updateUser(User user) {
        try {
            for (User u : users) {
                if (Objects.equals(u.getId(), user.getId())) {
                    break;
                } else {
                    throw new ValidationException("Ошибка. Пользователь не найден.");
                }
            }
            users.removeIf(u -> Objects.equals(u.getId(), user.getId()));
            users.add(user);
        } catch (ValidationException exception) {
            log.info(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    @Override
    public List<User> getAllUsers() {
        return users;
    }

    @Override
    public User getUserById(Long id) {
        Optional<User> optionalUser = users.stream()
                .filter(user -> Objects.equals(user.getId(), id))
                .findFirst();
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new DataNotFoundException("Ошибка. Пользователь не найден.");
        }
    }

    @Override
    public User addFriend(Long id, Long friendId) {
        Optional<User> optionalUser = users.stream()
                .filter(user -> Objects.equals(user.getId(), id))
                .findFirst();
        Optional<User> optionalFriend = users.stream()
                .filter(user -> Objects.equals(user.getId(), id))
                .findFirst();
        if (optionalUser.isEmpty() || optionalFriend.isEmpty() || id <= 0 || friendId <= 0) {
            throw new DataNotFoundException("Ошибка. Пользователь не найден.");
        } else {
            getUserById(id).setFriendUser(friendId);
            getUserById(friendId).setFriendUser(id);
        }
        return getUserById(id);
    }

    @Override
    public void removeFriend(Long id, Long friendId) {
        Optional<User> optionalUser = users.stream()
                .filter(user -> Objects.equals(user.getId(), id))
                .findFirst();
        Optional<User> optionalFriend = users.stream()
                .filter(user -> Objects.equals(user.getId(), id))
                .findFirst();
        if (optionalUser.isEmpty() || optionalFriend.isEmpty()) {
            throw new DataNotFoundException("Ошибка. Пользователь не найден.");
        } else {
            getUserById(id).getFriends().remove(friendId);
        }
    }

    @Override
    public List<User> getFriendsList(Long id) {
        Optional<User> optionalUser = users.stream()
                .filter(user -> Objects.equals(user.getId(), id))
                .findFirst();
        if (optionalUser.isEmpty() || id <= 0) {
            throw new DataNotFoundException("Ошибка. Пользователь не найден.");
        } else {
            User us = optionalUser.get();
            return users.stream()
                    .filter(user -> us.getFriends().contains(user.getId()))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) throws ValidationException {
        if (id <= 0 || otherId <= 0) {
            throw new ValidationException("id не могут быть отрицательными");
        } else {
            List<User> friends = getFriendsList(id);
            List<User> otherFriends = getFriendsList(otherId);
            friends.retainAll(otherFriends);
            return friends;
        }
    }

    private Long generateIdUser() {
        return ++generatedId;
    }
}