package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

public class FilmControllerTest {
    private final FilmController controller = new FilmController();
    private final Film film = new Film(1, "Фильм", "Описание",
            LocalDate.of(2024, 1, 1), 100);

    @DisplayName("Добавление фильма")
    @Test
    void create_Film() {
        controller.createFilm(film);
        assertEquals(1, controller.getAllFilms().size());
    }

    @DisplayName("Обновление фильма")
    @Test
    void update_Film() {
        Film updateFilm = new Film(1, "Фильм2", "Описание2",
                LocalDate.of(2024, 1, 2), 110);

        controller.createFilm(film);
        controller.updateFilm(updateFilm);

        assertEquals("Описание2", updateFilm.getDescription());
        assertEquals("Фильм2", updateFilm.getName());
        assertEquals(LocalDate.of(2024, 1, 2), updateFilm.getReleaseDate());
        assertEquals(110, updateFilm.getDuration());
        assertEquals(1, controller.getAllFilms().size());
    }

    @DisplayName("Фильм без названия")
    @Test
    void create_EmptyName() {
        film.setName("");

        assertEquals("Ошибка. Название фильма не указано.", assertThrows(ValidationException.class, () ->
                controller.createFilm(film)).getMessage());
        assertEquals(0, controller.getAllFilms().size());
    }

    @DisplayName("Фильм c очень длинным названием")
    @Test
    void create_DescriptionMoreThan200() {
        film.setDescription("Огромное некорректное описание с деталями, излишками, именами актеров, " +
                "другими фильмами, где играли актеры, информацией о саундтреках и куче прочей неинтересной ерунды. " +
                "Все не то, и все не так. И пройти не должно.  ");

        assertEquals("Ошибка. Описание фильма должно быть меньше 200 символов",
                (assertThrows(RuntimeException.class, () -> controller.createFilm(film)).getMessage()));
        assertEquals(0, controller.getAllFilms().size());
    }


    @DisplayName("Длительность фильма отрицательная")
    @Test
    void create_WrongDuration() {
        film.setDuration(-10);

        assertEquals("Ошибка. Продолжительность фильма должна быть положительной (более 1 секунды).",
                ((assertThrows(RuntimeException.class, () -> controller.createFilm(film))).getMessage()));
        assertEquals(0, controller.getAllFilms().size());
    }

    @DisplayName("Некорректная дата релиза")
    @Test
    void create_WrongDateRelease() {
        film.setReleaseDate(LocalDate.of(1000, 1, 1));

        assertEquals("Ошибка. Некорректная дата релиза фильма.",
                ((assertThrows(RuntimeException.class, () -> controller.createFilm(film))).getMessage()));
        assertEquals(0, controller.getAllFilms().size());
    }
}
