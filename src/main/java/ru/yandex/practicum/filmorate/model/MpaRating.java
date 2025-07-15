package ru.yandex.practicum.filmorate.model;

/**
 * Перечисление {@code MpaRating} представляет рейтинги, присваиваемые фильмам по шкале
 * Ассоциации кинокомпаний США (Motion Picture Association of America, MPA).
 *
 * <p>Используется в модели {@link Film} для указания возрастного ограничения фильма.</p>
 *
 * <p>Поддерживаемые значения:</p>
 * <ul>
 *     <li>{@link #G} — General Audiences — подходит для всех возрастных групп</li>
 *     <li>{@link #PG} — Parental Guidance Suggested — рекомендуется присутствие родителей</li>
 *     <li>{@link #PG_13} — Parents Strongly Cautioned — родителям не рекомендуется показывать детям младше 13 лет</li>
 *     <li>{@link #R} — Restricted — лицам младше 17 лет требуется сопровождение взрослых</li>
 *     <li>{@link #NC_17} — No One 17 and Under Admitted — запрещено для лиц младше 18 лет</li>
 * </ul>
 *
 * <p>Пример использования:</p>
 * <pre>{@code
 * Film film = new Film();
 * film.setMpa(MpaRating.PG_13);
 * }</pre>
 */
public enum MpaRating {
    /**
     * G — General Audiences.
     * Подходит для всех возрастных групп.
     */
    G("G"),
    /**
     * PG — Parental Guidance Suggested.
     * Рекомендуется присутствие родителей.
     */
    PG("PG"),
    /**
     * PG-13 — Parents Strongly Cautioned.
     * Родителям не рекомендуется показывать детям младше 13 лет.
     */
    PG_13("PG-13"),
    /**
     * R — Restricted.
     * Лицам младше 17 лет требуется сопровождение взрослых.
     */
    R("R"),
    /**
     * NC-17 — No One 17 and Under Admitted.
     * Запрещено для лиц младше 18 лет.
     */
    NC_17("NC-17");

    private final String value;

    MpaRating(String value) {
        this.value = value;
    }

    /**
     * Возвращает строковое значение рейтинга, соответствующее стандарту MPA.
     *
     * @return строковое представление рейтинга (например, "PG", "R")
     */
    public String getValue() {
        return value;
    }
}
