package ru.bdayloop.dao.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.bdayloop.exception.ConflictException;
import ru.bdayloop.model.User;
import ru.bdayloop.service.command.UserRegistration;
import ru.bdayloop.service.i.UserService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceImplTest extends DaoTestBase {

    @Autowired
    UserService userService;

    @Test
    void importUsers_whenLastLoginIsTaken_savesNobody() {
        createUser("taken");
        List<UserRegistration> batch = List.of(registration("first"), registration("second"), registration("taken"));

        assertThrows(ConflictException.class, () -> userService.importUsers(batch));

        assertTrue(userRepository.findByUsername("first").isEmpty());
        assertTrue(userRepository.findByUsername("second").isEmpty());
    }

    @Test
    void importUsers_whenAllValid_savesEveryone() throws SQLException {
        List<UserRegistration> batch = List.of(registration("first"), registration("second"));

        List<User> created = userService.importUsers(batch);

        assertEquals(2, created.size());
        assertTrue(userRepository.findByUsername("first").isPresent());
        assertTrue(userRepository.findByUsername("second").isPresent());
    }

    private UserRegistration registration(String username) {
        return new UserRegistration(new User(null, "Имя", LocalDate.of(2000, 1, 1), username, null, User.Role.USER), "secret");
    }
}