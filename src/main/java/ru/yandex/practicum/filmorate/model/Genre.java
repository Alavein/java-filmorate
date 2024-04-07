package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Genre implements Comparable<Genre> {
    private Long id;
    private String name;

    @Override
    public int compareTo(Genre genre) {
        return this.id.compareTo(genre.getId());
    }
}