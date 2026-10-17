package com.tournament.model.user;

import com.tournament.model.enums.UserRole;

/**
 * Абстрактный базовый класс пользователя системы.
 * Демонстрирует использование принципов ООП (Абстракция, Наследование, Инкапсуляция).
 * Каждая сущность содержит первичный ключ `Long id`.
 */
public abstract class User {
    protected Long id;                  // Уникальный идентификатор
    protected String email;              // Почтовый адрес (логин)
    protected String passwordHash;       // Хэш пароля
    protected String firstName;          // Имя
    protected String lastName;           // Фамилия
    protected UserRole role;             // Роль пользователя

    public User() {}

    public User(Long id, String email, String passwordHash, String firstName, String lastName, UserRole role) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    // Геттеры и сеттеры с явным указанием типов (без var)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    // Абстрактные методы проверки ролевых полномочий
    public abstract boolean canCreateTournament();
    public abstract boolean canManageUsers();
}