package ru.bdayloop.dao.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.bdayloop.exception.BadRequestException;
import ru.bdayloop.exception.ConflictException;
import ru.bdayloop.exception.NotFoundException;
import ru.bdayloop.model.User;
import ru.bdayloop.service.command.UserRegistration;
import ru.bdayloop.service.i.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    void importUsers_whenAllValid_savesEveryone()  {
        List<UserRegistration> batch = List.of(registration("first"), registration("second"));

        List<User> created = userService.importUsers(batch);

        assertEquals(2, created.size());
        assertTrue(userRepository.findByUsername("first").isPresent());
        assertTrue(userRepository.findByUsername("second").isPresent());
    }

    @Test
    void subscribe_toYourself_isRejected() {
        User kate = createUser("kate");

        assertThrows(BadRequestException.class, () -> userService.subscribe(kate.getId(), kate.getId()));
    }

    @Test
    void subscribe_toUnknownUser_isNotFound() {
        User kate = createUser("kate");

        assertThrows(NotFoundException.class, () -> userService.subscribe(kate.getId(), 999));
    }

    @Test
    void subscribe_twice_isConflict() {
        User kate = createUser("kate");
        User anya = createUser("anya");
        userService.subscribe(kate.getId(), anya.getId());

        assertThrows(ConflictException.class, () -> userService.subscribe(kate.getId(), anya.getId()));
    }

    @Test
    void subscribeAndUnsubscribe_changeStatus() {
        User kate = createUser("kate");
        User anya = createUser("anya");

        userService.subscribe(kate.getId(), anya.getId());
        assertTrue(userService.isSubscribedDirectly(kate.getId(), anya.getId()));

        userService.unsubscribe(kate.getId(), anya.getId());
        userService.unsubscribe(kate.getId(), anya.getId());
        assertFalse(userService.isSubscribedDirectly(kate.getId(), anya.getId()));
    }

    @Test
    void deletingUser_removesSubscriptionsToHim() {
        User kate = createUser("kate");
        User anya = createUser("anya");
        userService.subscribe(kate.getId(), anya.getId());

        userService.delete(anya.getId(), anya.getId());

        assertFalse(userService.isSubscribedDirectly(kate.getId(), anya.getId()));
    }

    private UserRegistration registration(String username) {
        return new UserRegistration(new User(null, "Имя", LocalDate.of(2000, 1, 1), username, null, User.Role.USER), "secret");
    }
}