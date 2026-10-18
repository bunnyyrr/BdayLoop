package ru.bdayloop.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.bdayloop.exception.BadRequestException;
import ru.bdayloop.exception.ForbiddenException;
import ru.bdayloop.model.User;
import ru.bdayloop.service.command.UserRegistration;
import ru.bdayloop.service.i.UserService;
import ru.bdayloop.web.AuthUser;
import ru.bdayloop.web.SessionUtil;
import ru.bdayloop.web.dto.request.ImportUserRequest;
import ru.bdayloop.web.dto.request.LoginRequest;
import ru.bdayloop.web.dto.request.RegisterRequest;
import ru.bdayloop.web.dto.request.UpdateUserRequest;
import ru.bdayloop.web.dto.response.SubscriptionStatusResponse;
import ru.bdayloop.web.dto.response.UserResponse;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody RegisterRequest body) {
        User newUser = new User(0, body.name(), body.birthday(), body.username(), null, User.Role.USER);
        return UserResponse.from(userService.register(newUser, body.password()));
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginRequest body, HttpServletRequest req) {
        User user = userService.login(body.username(), body.password());
        SessionUtil.login(req, user.getId(), user.getRole());
        return UserResponse.from(user);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest req) {
        SessionUtil.logout(req);
    }

    @PostMapping("/import")
    @ResponseStatus(HttpStatus.CREATED)
    public List<UserResponse> importUsers(AuthUser user, @RequestBody List<ImportUserRequest> body) {
        if (user.role() != User.Role.ADMIN) {
            throw new ForbiddenException("Требуются права администратора");
        }
        List<UserRegistration> registrations = body.stream().map(r -> new UserRegistration(new User(0, r.name(), r.birthday(), r.username(), null, parseRole(r.role())), r.password())).toList();
        return userService.importUsers(registrations).stream().map(u -> UserResponse.from(u)).toList();
    }

    @GetMapping
    public List<UserResponse> search(@RequestParam(defaultValue = "") String name, AuthUser user) {
        return userService.findByName(name).stream().map(u -> UserResponse.from(u)).toList();
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable int id, AuthUser user) {
        return UserResponse.from(userService.findById(id));
    }

    @GetMapping("/{id}/subscribe")
    public SubscriptionStatusResponse subscriptionStatus(@PathVariable int id, AuthUser user) {
        return new SubscriptionStatusResponse(userService.isSubscribedDirectly(user.id(), id));
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable int id, @RequestBody UpdateUserRequest body, AuthUser user) {
        User existing = userService.findById(id);
        User updated = new User(existing.getId(), body.name(), body.birthday(), body.username(), existing.getPasswordHash(), existing.getRole());
        return UserResponse.from(userService.update(updated, user.id()));
    }

    @PostMapping("/{id}/subscribe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void subscribe(@PathVariable int id, AuthUser user) {
        userService.subscribe(user.id(), id);
    }

    @DeleteMapping("/{id}/subscribe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@PathVariable int id, AuthUser user) {
        userService.unsubscribe(user.id(), id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id, AuthUser user, HttpServletRequest req) {
        userService.delete(id, user.id());
        SessionUtil.logout(req);
    }

    private User.Role parseRole(String role) {
        if (role == null) {
            return User.Role.USER;
        }
        try {
            return User.Role.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Неизвестная роль: " + role);
        }
    }
}