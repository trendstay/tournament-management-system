package com.tournament.model.enums;

/**
 * Перечисление возможных жизненных циклов (статусов) спортивного матча.
 */
public enum MatchStatus {
    SCHEDULED,   // Запланирован
    IN_PROGRESS, // В процессе проведения
    FINISHED,    // Успешно завершен
    CANCELLED    // Отменен
}