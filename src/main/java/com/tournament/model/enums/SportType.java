package com.tournament.model.enums;

/**
 * Виды спорта, поддерживаемые сервисом турниров.
 * <p>
 * Соответствует сущности «Вид спорта» из ER-модели: у каждого вида спорта
 * есть название, описание и признак того, разрешена ли ничья по правилам.
 */
public enum SportType {
    FOOTBALL("Футбол", "Командная игра с мячом на поле", true),
    BASKETBALL("Баскетбол", "Командная игра с мячом на кольцевой площадке", false),
    TENNIS("Теннис", "Индивидуальный или парный вид спорта с ракеткой и мячом", false),
    CHESS("Шахматы", "Интеллектуальная игра на доске с фигурами", true),
    E_SPORTS("Киберспорт", "Соревнования по компьютерным играм", true);

    private final String displayName;
    private final String description;
    private final boolean allowsDraw;

    SportType(String displayName, String description, boolean allowsDraw) {
        this.displayName = displayName;
        this.description = description;
        this.allowsDraw = allowsDraw;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAllowsDraw() {
        return allowsDraw;
    }
}
