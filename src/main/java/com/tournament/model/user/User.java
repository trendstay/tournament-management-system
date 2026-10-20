package com.tournament.model.user;

import com.tournament.model.Identifiable;
import com.tournament.model.enums.UserRole;

import java.util.Locale;
import java.util.Objects;

/**
 * Абстрактный базовый класс пользователя системы.
 * <p>
 * Любой пользователь имеет учётные данные (email, хэш пароля, имя, фамилия,
 * телефон) и роль. Конкретные роли представлены наследниками:
 * {@link Admin}, {@link Manager}, {@link Player}, {@link Captain},
 * {@link Coach}, {@link Referee}.
 * <p>
 * Наследование позволяет описывать полномочия роли в одном месте и не
 * смешивать данные разных типов участников.
 */
public abstract class User implements Identifiable {

    private Long id;
    private final String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private final UserRole role;

    protected User(Long id, String email, String passwordHash, String firstName,
                   String lastName, String phoneNumber, UserRole role) {
        this.id = id;
        this.email = requireText(email, "Email пользователя не может быть пустым");
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.role = Objects.requireNonNull(role, "Роль пользователя не может быть null");
    }

    /**
     * Может ли пользователь создавать турниры.
     */
    public abstract boolean canCreateTournament();

    /**
     * Может ли пользователь управлять учётными записями других пользователей.
     */
    public abstract boolean canManageUsers();

    /**
     * Может ли пользователь управлять составом команды.
     */
    public abstract boolean canManageTeam();

    /**
     * Может ли пользователь судить матчи.
     */
    public abstract boolean canRefereeMatches();

    /**
     * Возвращает полное имя пользователя для отображения в протоколах и таблицах.
     */
    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        if (firstName != null && !firstName.isBlank()) {
            fullName.append(firstName);
        }
        if (lastName != null && !lastName.isBlank()) {
            if (fullName.length() > 0) {
                fullName.append(' ');
            }
            fullName.append(lastName);
        }
        return fullName.length() == 0 ? email : fullName.toString();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserRole getRole() {
        return role;
    }

    /**
     * Пользователи равны, если у них совпадает email (уникальный ключ из ER-модели).
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof User)) {
            return false;
        }
        User user = (User) other;
        return email.equalsIgnoreCase(user.email);
    }

    @Override
    public int hashCode() {
        return email.toLowerCase(Locale.ROOT).hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{email='" + email + "', role=" + role + "}";
    }

    protected static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
