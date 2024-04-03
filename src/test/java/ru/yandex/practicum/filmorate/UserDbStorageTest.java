package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private UserStorage userStorage;
    private User user;
    private User user1;

    @BeforeEach
    public void beforeEach() {
        userStorage = new UserDbStorage(jdbcTemplate);
        user = User.builder()
                .email("user@mail.ru")
                .login("login")
                .birthday(LocalDate.of(1993, 6,20))
                .name("name")
                .build();
        user1 = User.builder()
                .email("user1@mail.ru")
                .login("login1")
                .birthday(LocalDate.of(1994, 6,20))
                .name("name1")
                .build();

    }

    @Test
    public void testFindUserById() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.createUser(user);

        User savedUser = userStorage.getUserById(1L);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    public void getAllUsersTest() {

        List<User> newUsers = new ArrayList<>();
        newUsers.add(user);
        newUsers.add(user1);

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.createUser(user);
        userStorage.createUser(user1);

        List<User> savedUsers = userStorage.getAllUsers();

        assertThat(savedUsers)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUsers);
    }

    @Test
    public void shouldDeleteFriend() {
        userStorage.createUser(user);
        userStorage.createUser(user1);

        userStorage.addFriend(1L,2L);
        userStorage.removeFriend(1L, 2L);

        List<User> savedFriends = userStorage.getFriendsList(1L);

        assertThat(savedFriends)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(savedFriends);
    }
}
