package ru.bdayloop.dao.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;
import ru.bdayloop.exception.*;
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

    @Test
    void update_changesOwnProfile_andKeepsRoleAndPassword() {
        User kate = createUser("kate");

        userService.update(kate.getId(), "Катя", LocalDate.of(1999, 5, 20), "kate2", kate.getId());

        User reloaded = userService.findById(kate.getId());
        assertEquals("Катя", reloaded.getName());
        assertEquals(LocalDate.of(1999, 5, 20), reloaded.getBirthday());
        assertEquals("kate2", reloaded.getUsername());
        assertEquals(User.Role.USER, reloaded.getRole());
        assertEquals("hash", reloaded.getPasswordHash());
    }

    @Test
    void update_ofAnotherUser_isForbidden() {
        User kate = createUser("kate");
        User anya = createUser("anya");

        assertThrows(ForbiddenException.class, () -> userService.update(kate.getId(), "Взлом", LocalDate.of(2000, 1, 1), "kate", anya.getId()));
    }

    @Test
    void update_withTakenUsername_isConflict() {
        User kate = createUser("kate");
        createUser("anya");

        assertThrows(ConflictException.class, () -> userService.update(kate.getId(), "Катя", LocalDate.of(2000, 1, 1), "anya", kate.getId()));
    }

    @Test
    void update_keepingOwnUsername_isAllowed() {
        User kate = createUser("kate");

        userService.update(kate.getId(), "Катя", LocalDate.of(2000, 1, 1), "kate", kate.getId());

        assertEquals("Катя", userService.findById(kate.getId()).getName());
    }

    @Test
    void update_withBlankName_isRejected() {
        User kate = createUser("kate");

        assertThrows(BadRequestException.class, () -> userService.update(kate.getId(), " ", LocalDate.of(2000, 1, 1), "kate", kate.getId()));
    }

    @Test
    void changePassword_withCorrectOldPassword_switchesLogin() {
        User kate = registerUser("kate", "old-pass");

        userService.changePassword(kate.getId(), "old-pass", "new-pass", kate.getId());

        assertEquals(kate.getId(), userService.login("kate", "new-pass").getId());
        assertThrows(UnauthorizedException.class, () -> userService.login("kate", "old-pass"));
    }

    @Test
    void changePassword_withWrongOldPassword_isRejected() {
        User kate = registerUser("kate", "old-pass");

        assertThrows(BadRequestException.class,
                () -> userService.changePassword(kate.getId(), "wrong", "new-pass", kate.getId()));

        assertEquals(kate.getId(), userService.login("kate", "old-pass").getId());
    }

    @Test
    void changePassword_ofAnotherUser_isForbidden() {
        User kate = registerUser("kate", "old-pass");
        User anya = registerUser("anya", "anya-pass");

        assertThrows(ForbiddenException.class,
                () -> userService.changePassword(kate.getId(), "old-pass", "new-pass", anya.getId()));
    }

    @Test
    void changePassword_withBlankNewPassword_isRejected() {
        User kate = registerUser("kate", "old-pass");

        assertThrows(BadRequestException.class,
                () -> userService.changePassword(kate.getId(), "old-pass", " ", kate.getId()));
    }

    @Test
    void login_withHashCreatedByOldJBCrypt_stillWorks() {
        String legacyHash = "$2a$10$/OF05P8vcSyNncQi2ZczcewEWXN7cFca9Kj5b/1wCKhHMUHO7fHja";
        User legacy = new TransactionTemplate(transactionManager).execute(status -> userRepository.save(
                new User(null, "legacy", LocalDate.of(2000, 1, 1), "legacy", legacyHash, User.Role.USER)));

        assertEquals(legacy.getId(), userService.login("legacy", "secret").getId());
    }

    private User registerUser(String username, String password) {
        return userService.register(
                new User(null, username, LocalDate.of(2000, 1, 1), username, null, User.Role.USER), password);
    }

    private UserRegistration registration(String username) {
        return new UserRegistration(new User(null, "Имя", LocalDate.of(2000, 1, 1), username, null, User.Role.USER), "secret");
    }
}