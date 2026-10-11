package com.tournament.model.user;

import com.tournament.model.enums.UserRole;

public abstract class User {
    protected Long id;
    protected String email;
    protected String passwordHash;
    protected String firstName;
    protected String lastName;
    protected UserRole role;

    public User() {}

    public User(Long id, String email, String passwordHash, String firstName, String lastName, UserRole role) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

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

    // Проверка прав доступа
    public abstract boolean canCreateTournament();
    public abstract boolean canManageUsers();
}