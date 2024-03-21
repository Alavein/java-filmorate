package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private int id;
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
    private Set<Integer> friends = new HashSet<>();
}
