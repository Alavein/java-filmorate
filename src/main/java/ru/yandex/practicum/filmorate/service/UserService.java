package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    User getUserById(Long id);

    User addFriend(Long id, Long friendId);

    void removeFriend(Long id, Long friendId);

    List<User> getFriendsList(Long id);

    List<User> getCommonFriends(Long id, Long otherId) throws ValidationException;
}