package com.tournament.support;

import com.tournament.model.enums.SportType;
import com.tournament.model.team.Team;
import com.tournament.model.tournament.Tournament;
import com.tournament.model.user.Admin;
import com.tournament.model.user.Captain;
import com.tournament.model.user.Coach;
import com.tournament.model.user.Manager;
import com.tournament.model.user.Player;
import com.tournament.model.user.Referee;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Фабрика тестовых сущностей, чтобы тесты не дублировали длинные конструкторы.
 */
public final class TestFixtures {

    public static final String PASSWORD_HASH = "$2a$10$testPasswordHash";
    public static final String PHONE_NUMBER = "+79990000000";

    private TestFixtures() {
    }

    public static Admin admin(String email) {
        return new Admin(null, email, PASSWORD_HASH, "Иван", "Админов", PHONE_NUMBER);
    }

    public static Manager manager(String email) {
        return new Manager(null, email, PASSWORD_HASH, "Пётр", "Менеджеров", PHONE_NUMBER);
    }

    public static Coach coach(String email) {
        return new Coach(null, email, PASSWORD_HASH, "Олег", "Тренеров", PHONE_NUMBER);
    }

    public static Referee referee(String email) {
        return new Referee(null, email, PASSWORD_HASH, "Сергей", "Судейкин", PHONE_NUMBER);
    }

    public static Player player(String email) {
        return new Player(null, email, PASSWORD_HASH, "Алексей", "Игроков", PHONE_NUMBER);
    }

    public static Captain captain(String email) {
        return new Captain(null, email, PASSWORD_HASH, "Артём", "Капитанов", PHONE_NUMBER);
    }

    public static Team team(String name) {
        return new Team(null, name, "Москва");
    }

    public static Team team(String name, String city) {
        return new Team(null, name, city);
    }

    public static Tournament tournament(String title, LocalDate startDate) {
        return new Tournament(null, title, SportType.FOOTBALL,
                startDate, startDate.plusDays(10), new BigDecimal("10000"));
    }
}
