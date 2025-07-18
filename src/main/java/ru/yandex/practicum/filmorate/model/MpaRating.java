package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Класс {@code MpaRating} представляет рейтинги, присваиваемые фильмам по шкале
 * Ассоциации кинокомпаний США (Motion Picture Association of America, MPA).
 *
 * <p>Используется в модели {@link Film} для указания возрастного ограничения фильма.</p>
 *
 * <p>Поддерживаемые значения:</p>
 * <ul>
 *     <li>G — General Audiences — подходит для всех возрастных групп</li>
 *     <li>PG — Parental Guidance Suggested — рекомендуется присутствие родителей</li>
 *     <li>PG_13 — Parents Strongly Cautioned — родителям не рекомендуется показывать детям младше 13 лет</li>
 *     <li>R — Restricted — лицам младше 17 лет требуется сопровождение взрослых</li>
 *     <li>NC_17 — No One 17 and Under Admitted — запрещено для лиц младше 18 лет</li>
 * </ul>
 *
 * <p>Пример использования:</p>
 * <pre>{@code
 * Film film = new Film();
 * film.setMpa(MpaRating.PG_13);
 * }</pre>
 */
@Data
public class MpaRating {
    private Long id;
    @NotBlank(message = "Рейтинг не может быть пустым")
    private String name;
}
