package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


@Data
@Builder
public class User {

    private Long id;
    @Email(message = "Ошибка. Почта указана некорректно.")
    @NotBlank(message = "Ошибка. Не указана почта.")
    private String email;
    @NotBlank(message = "Ошибка. Логин не может быть пустым.")
    @Pattern(regexp = "^\\S*$", message = "Логин не должен содержать пробелы")
    private String login;
    private String name;
    @NotNull
    @PastOrPresent(message = "Ошибка. Некорректно указана дата рождения.")
    private LocalDate birthday;
    private final Set<Long> friends = new TreeSet<>();

    public void setFriendUser(Long friendId) {
        friends.add(friendId);
    }

    public Map<String, Object> userToMap(User user) {
        return Map.of(
                "email", user.getEmail(),
                "login", user.getLogin(),
                "name", user.getName(),
                "birthday", user.getBirthday()
        );
    }
}
