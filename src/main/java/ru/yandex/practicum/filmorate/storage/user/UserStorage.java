package ru.yandex.practicum.filmorate.storage.user;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;

public interface UserStorage {

    User createUser(User user);

    void updateUser(User user);

    List<User> getAllUsers();

    User getUserById(Long id);

    User addFriend(Long id, Long friendId);

    void removeFriend(Long id, Long friendId);

    List<User> getFriendsList(Long id);

    List<User> getCommonFriends(Long id, Long otherId) throws ValidationException;
}