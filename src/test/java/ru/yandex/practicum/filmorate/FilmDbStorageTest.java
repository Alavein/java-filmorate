package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private UserStorage userStorage;
    private FilmStorage filmStorage;
    private Film film;
    private Film film1;
    private User user;

    @BeforeEach
    public void beforeEach() {
        filmStorage = new FilmDbStorage(jdbcTemplate);
        userStorage = new UserDbStorage(jdbcTemplate);
        film = Film.builder()
                .name("name")
                .description("desc")
                .releaseDate(LocalDate.of(1990, 1, 1))
                .duration(100)
                .mpa(new Mpa(1, "G"))
                .build();
        film1 = Film.builder()
                .name("name")
                .description("desc")
                .releaseDate(LocalDate.of(1990, 1, 1))
                .duration(100)
                .mpa(new Mpa(1, "G"))
                .build();
        user = User.builder()
                .email("email@mail.ru")
                .login("login")
                .birthday(LocalDate.of(1993, 6, 20))
                .name("name")
                .build();
    }

    @Test
    public void shouldCreateAndFindFilmByIdTest() {
        filmStorage.createFilm(film);
        Film savedFilm = filmStorage.getFilmById(1L);

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    public void shouldUpdateFilm() {
        film1.setId(1L);
        filmStorage.createFilm(film);
        filmStorage.updateFilm(film1);
        Film savedFilm = filmStorage.getFilmById(1L);

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film1);
    }

    @Test
    public void shouldAddLike() {
        filmStorage.createFilm(film);
        userStorage.createUser(user);
        filmStorage.addFilmLike(1L,1L);

        Film savedFilm = filmStorage.getFilmById(1L);

        assertNotNull(savedFilm);
        assertThat(savedFilm.getUsersLikes())
                .isNotNull()
                .isEqualTo(Set.of(1L));
    }

    @Test
    public void shouldGetFilms() {
        filmStorage.createFilm(film);
        filmStorage.createFilm(film1);

        List<Film> savedFilms = filmStorage.getAllFilms();

        assertThat(savedFilms)
                .isNotNull()
                .isEqualTo(List.of(film, film1));
    }
}