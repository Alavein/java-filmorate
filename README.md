# java-filmorate
---
Фильмов много — и с каждым годом становится всё больше. Чем их больше, тем больше разных оценок. Чем больше оценок, тем сложнее сделать выбор. Однако не время сдаваться! Filmorate - это сервис, который будет работать с фильмами и оценками пользователей, а также возвращать топ-5 фильмов, рекомендованных к просмотру. Теперь ни вам, ни вашим друзьям не придётся долго размышлять, что посмотреть вечером.
# Модель базы данных (ER-диаграмма)
---
![BD](https://github.com/Alavein/java-filmorate/blob/main/ER%20diagram.JPG)
[Редактировать модель](https://miro.com/app/board/uXjVKbcQ9Fo=/?share_link_id=563786810414 "Переходи в Miro!") 

## Примеры запросов

Получение Топ-10 самых популярных фильмов:

```
SELECT f.name AS film,
       count(fl.film_id) AS likes
FROM film_likes AS fl
LEFT JOIN films f ON fl.film_id=f.film_id
GROUP BY fl.film_id
ORDER BY count(fl.film_id) DESC
LIMIT 10
```

Получение фильма по его уникальному номеру (id): 

```
SELECT * 
FROM films
WHERE film_id = 1
```

Получение списка всех пользователей:

```
SELECT * 
FROM users
```
