package com.tournament;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Точка входа приложения «Сервис организации спортивных турниров».
 *
 * <p>Лабораторная работа №2: доступ к данным реализован через Spring Data JPA
 * (PostgreSQL), REST API построен по модели MVC.</p>
 */
@SpringBootApplication
public class TournamentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TournamentServiceApplication.class, args);
    }
}
