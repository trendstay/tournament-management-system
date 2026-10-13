package com.tournament.service;

import com.tournament.model.user.AdminUser;
import com.tournament.model.user.CustomerUser;
import com.tournament.model.user.ManagerUser;
import com.tournament.model.user.User;
import com.tournament.repository.UserRepository;

import java.util.List;

/**
 * Сервис управления пользователями и бизнес-валидации данных.
 */
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AdminUser registerAdmin(String email, String password, String firstName, String lastName) {
        validateEmail(email);
        AdminUser admin = new AdminUser(null, email, password, firstName, lastName);
        return (AdminUser) userRepository.save(admin);
    }

    public ManagerUser registerManager(String email, String password, String firstName, String lastName) {
        validateEmail(email);
        ManagerUser manager = new ManagerUser(null, email, password, firstName, lastName);
        return (ManagerUser) userRepository.save(manager);
    }

    public CustomerUser registerCustomer(String email, String password, String firstName, String lastName, String phone) {
        validateEmail(email);
        CustomerUser customer = new CustomerUser(null, email, password, firstName, lastName, phone);
        return (CustomerUser) userRepository.save(customer);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID " + id + " не найден"));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public boolean deleteUser(Long id) {
        return userRepository.delete(id);
    }

    private void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Некорректный формат email");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }
    }
}