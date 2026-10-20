package com.tournament.repository;

import com.tournament.model.enums.UserRole;
import com.tournament.model.user.Player;
import com.tournament.model.user.User;
import com.tournament.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit-тесты in-memory репозитория пользователей.
 */
class InMemoryUserRepositoryTest {

    private InMemoryUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
    }

    @Test
    void save_shouldAssignSequentialIdentifiers() {
        User firstUser = userRepository.save(TestFixtures.player("first@test.com"));
        User secondUser = userRepository.save(TestFixtures.player("second@test.com"));

        assertNotNull(firstUser.getId());
        assertNotNull(secondUser.getId());
        assertTrue(secondUser.getId() > firstUser.getId());
        assertEquals(2, userRepository.count());
    }

    @Test
    void findById_whenUserExists_shouldReturnUser() {
        User savedUser = userRepository.save(TestFixtures.player("player@test.com"));

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(savedUser, foundUser.get());
    }

    @Test
    void findById_whenUserDoesNotExist_shouldReturnEmptyOptional() {
        assertFalse(userRepository.findById(42L).isPresent());
    }

    @Test
    void findByEmail_shouldIgnoreCase() {
        userRepository.save(TestFixtures.player("Player@Test.com"));

        assertTrue(userRepository.findByEmail("player@test.com").isPresent());
    }

    @Test
    void findByRole_shouldFilterUsers() {
        userRepository.save(TestFixtures.player("player@test.com"));
        userRepository.save(TestFixtures.captain("captain@test.com"));

        List<User> players = userRepository.findByRole(UserRole.PLAYER);

        assertEquals(1, players.size());
        assertEquals("player@test.com", players.get(0).getEmail());
    }

    @Test
    void deleteById_shouldRemoveUser() {
        User savedUser = userRepository.save(TestFixtures.player("player@test.com"));

        assertTrue(userRepository.deleteById(savedUser.getId()));
        assertFalse(userRepository.findById(savedUser.getId()).isPresent());
        assertEquals(0, userRepository.count());
    }

    @Test
    void findAll_shouldReturnUsersSortedByIdentifier() {
        Player firstUser = TestFixtures.player("first@test.com");
        Player secondUser = TestFixtures.player("second@test.com");
        userRepository.save(secondUser);
        userRepository.save(firstUser);

        List<User> users = userRepository.findAll();

        assertEquals(2, users.size());
        assertTrue(users.get(0).getId() < users.get(1).getId());
    }
}
