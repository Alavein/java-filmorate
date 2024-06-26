package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Film.
 */

@Data
@Builder
public class Film {

    private Long id;
    @NotBlank(message = "Ошибка. Название фильма не указано.")
    private String name;
    @NotNull
    @Size(max = 200, message = "Ошибка. Описание фильма должно быть меньше 200 символов")
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Positive(message = "Ошибка. Продолжительность фильма должна быть положительной (более 1 секунды).")
    private Integer duration;
    private final Set<Long> usersLikes = new TreeSet<>();
    private Mpa mpa;
    private Set<Genre> genres;

    public Set<Long> getUsersLikes() {
        return usersLikes;
    }

    public Map<String, Object> filmToMap(Film film) {
        return Map.of(
                "name", film.getName(),
                "description", film.getDescription(),
                "release_date", film.getReleaseDate(),
                "duration", film.getDuration(),
                "id_mpa", mpa.getId()
        );
    }
}
