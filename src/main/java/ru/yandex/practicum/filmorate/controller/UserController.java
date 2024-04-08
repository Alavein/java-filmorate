package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Обновляем данные пользователя: {}", user);
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Список пользователей.");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendsId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendsId) {
        userService.removeFriend(id, friendsId);
    }

    @GetMapping("{id}/friends")
    public List<User> getFriendsList(@PathVariable Long id) {
        return userService.getFriendsList(id);
    }

    @GetMapping("/{firstId}/friends/common/{secondId}")
    public List<User> getCommonFriends(@PathVariable Long firstId, @PathVariable Long secondId) {
        return userService.getCommonFriends(firstId, secondId);
    }
}
