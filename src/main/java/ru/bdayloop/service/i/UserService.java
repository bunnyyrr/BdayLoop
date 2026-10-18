package ru.bdayloop.service.i;

import ru.bdayloop.model.User;
import ru.bdayloop.service.command.UserRegistration;

import java.time.LocalDate;
import java.util.List;

public interface UserService {
    User register(User user, String plainPassword);
    User login(String username, String plainPassword) ;
    User findById(int id) ;
    List<User> findByName(String name) ;
    User findByUsername(String username) ;
    User update(int id, String name, LocalDate birthday, String username, int requesterId) ;
    void delete(int id, int requesterId);
    void subscribe(int subscriberId, int targetId);
    void unsubscribe(int subscriberId, int targetId);
    List<User> importUsers(List<UserRegistration> registrations);
    boolean isSubscribedDirectly(int subscriberId, int targetId) ;
    void changePassword(int id, String oldPassword, String newPassword, int requesterId);
}
