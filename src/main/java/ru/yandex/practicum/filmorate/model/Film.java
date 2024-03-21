package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
/**
 * Film.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Film {

   private Integer id;
    @NotBlank(message = "Ошибка. Название фильма не указано.")
    private String name;
    @NotNull
    @Size(max = 200, message = "Ошибка. Описание фильма должно быть меньше 200 символов")
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Positive(message = "Ошибка. Продолжительность фильма должна быть положительной (более 1 секунды).")
    private Integer duration;
    private Set<Integer> usersLikes = new HashSet<>();


}
