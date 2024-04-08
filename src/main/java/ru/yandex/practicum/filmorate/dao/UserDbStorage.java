package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import org.yaml.snakeyaml.constructor.DuplicateKeyException;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    private boolean checkUser(Long userId) {
        try {
            jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?", this::buildUser, userId);
            return false;
        } catch (EmptyResultDataAccessException exception) {
            return true;
        }
    }

    @Override
    public User createUser(User user) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        Long id = insert.executeAndReturnKey(user.userToMap(user)).longValue();
        user.setId(id);
        if (!isEmpty(user.getFriends())) {
            for (long idf : user.getFriends()) {
                jdbcTemplate.update("INSERT INTO friendship (id_users, id_friends) VALUES (?, ?)", id, idf);
            }
        }
        return user;
    }

    @Override
    public void updateUser(User user) {
        if (checkUser(user.getId())) {
            log.info("Ошибка updateUser. Пользователь с id " + user.getId() + " не найден.");
            throw new DataNotFoundException("Ошибка. Пользователь с id " + user.getId() + " не найден.");
        }

        String sqlQry = "UPDATE users SET " +
                "email = ?, " +
                "login = ?, " +
                "name = ?, " +
                "birthday = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQry,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        jdbcTemplate.update("DELETE FROM friendship WHERE id_users = ?", user.getId());

        if (!isEmpty(user.getFriends())) {
            user.getFriends().forEach(id -> {
                        jdbcTemplate.update("INSERT INTO friendship (id_users, id_friends) VALUES (?, ?)",
                                user.getId(), id);
                    }
            );
        }
    }

    @Override
    public List<User> getAllUsers() {
        return jdbcTemplate.query("SELECT * FROM users", this::buildUser);
    }

    @Override
    public User getUserById(Long id) {
        if (checkUser(id)) {
            log.info("Ошибка. Пользователь с id " + id + " не найден.");
            throw new DataNotFoundException("Ошибка. Пользователь с id " + id + " не найден.");
        }
        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?", this::buildUser, id);
    }

    private User buildUser(ResultSet rs, int rowNum) throws SQLException {
        User user = User.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
        if (!isEmpty(user.getFriends())) {
            user.getFriends().addAll(jdbcTemplate.query("SELECT id_friends FROM friendship WHERE id_users = ?",
                    (rs1, rowNum1) -> rs1.getLong("id_friends"), user.getId())
            );
        }
        return user;
    }

    @Override
    public User addFriend(Long id, Long friendId) {
        if (checkUser(id)) {
            log.info("Ошибка addFriend. Пользователь с id " + id + " не найден.");
            throw new DataNotFoundException("Ошибка. Пользователь с id " + id + " не найден.");
        }
        if (checkUser(friendId)) {
            log.info("Ошибка addFriend. Пользователь с id " + friendId + " не найден.");
            throw new DataNotFoundException("Ошибка. Пользователь с id " + friendId + " не найден.");
        }
        if (Objects.equals(id, friendId)) {
            log.info("Ошибка addFriend. Нельзя стать своим же другом. По крайней мере, в программе.");
            throw new ValidationException("Ошибка. Нельзя стать своим же другом.");
        }
        String sql = "INSERT INTO friendship (id_users, id_friends) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sql, id, friendId);
            getUserById(id).setFriendUser(friendId);
        } catch (DuplicateKeyException e) {
            log.info("Ошибка addFriend. Второй раз добавить друга нельзя.");
            throw new ValidationException("Ошибка. Второй раз добавить друга нельзя.");
        }
        return getUserById(id);
    }

    @Override
    public void removeFriend(Long id, Long friendId) {
        if (checkUser(id)) {
            log.info("Ошибка removeFriend. Пользователь с id " + id + " не найден.");
            throw new DataNotFoundException("Ошибка. Пользователь с id " + id + " не найден.");
        }
        if (checkUser(friendId)) {
            log.info("Ошибка removeFriend. Пользователь с id " + friendId + " не найден.");
            throw new DataNotFoundException("Ошибка. Пользователь с id " + friendId + " не найден.");
        }
        if ((jdbcTemplate.update("DELETE FROM friendship WHERE id_users = ? AND id_friends = ?", id, friendId) == 0)
                || (jdbcTemplate.update("DELETE FROM friendship WHERE id_users = ? AND id_friends = ?", friendId, id) == 0)) {
            log.info("Ошибка removeFriend. Пользователи не являются друзьями.");
        }
    }

    @Override
    public List<User> getFriendsList(Long id) {
        if (checkUser(id)) {
            log.info("Ошибка getFriendsList. Пользователь с id " + id + " не найден.");
            throw new DataNotFoundException("Ошибка. Пользователь с id " + id + " не найден.");
        }
        String sql = "SELECT id_friends FROM friendship WHERE id_users = ?";
        List<Long> listFriendsId = jdbcTemplate.query(sql, (rs1, nowNum1) -> rs1.getLong("id_friends"), id);
        return listFriendsId.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) throws ValidationException {
        if (id <= 0 || otherId <= 0) {
            throw new ValidationException("Ошибка. Id не может быть отрицательным числом.");
        } else {
            List<User> friends = getFriendsList(id);
            List<User> otherFriends = getFriendsList(otherId);
            friends.retainAll(otherFriends);
            return friends;
        }
    }
}
