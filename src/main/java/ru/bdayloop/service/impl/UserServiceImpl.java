package ru.bdayloop.service.impl;

import org.mindrot.jbcrypt.BCrypt;
import ru.bdayloop.dao.i.UserDao;
import ru.bdayloop.exception.*;
import ru.bdayloop.model.User;
import ru.bdayloop.service.i.UserService;
import ru.bdayloop.service.validation.Validation;
import ru.bdayloop.web.dto.ImportUserRequest;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    public UserServiceImpl(UserDao userDao){
        this.userDao =userDao;
    }

    @Override
    public User register(User user, String plainPassword) throws SQLException{
        validateProfile(user.getName(), user.getBirthday(), user.getUsername());
        Validation.requireText(plainPassword, "Пароль не может быть пустым");
        if(userDao.findByUsername(user.getUsername()).isPresent()){
            throw new ConflictException("Логин «" + user.getUsername() + "» уже занят");
        }

        String hash = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        User toCreateUser = new User(
                0,
                user.getName(),
                user.getBirthday(),
                user.getUsername(),
                hash,
                user.getRole()
        );
        return userDao.create(toCreateUser);
    }

    @Override
    public User login(String username, String plainPassword) throws SQLException{
        Validation.requireText(username, "Введите логин");
        Validation.requireText(plainPassword, "Введите пароль");

        Optional<User> found = userDao.findByUsername(username);
        if(found.isEmpty() || !BCrypt.checkpw(plainPassword, found.get().getPasswordHash())){
            throw new UnauthorizedException("Неверный логин или пароль");
        }
        return found.get();
    }

    @Override
    public User findById(int id) throws SQLException{
        return userDao.findById(id).orElseThrow(() -> new NotFoundException("Пользователь с id "+ id+" не найден"));
    }

    @Override
    public List<User> findByName(String name) throws SQLException{
        return userDao.findByName(name);
    }

    @Override
    public User findByUsername(String username) throws SQLException{
        return userDao.findByUsername(username).orElseThrow(() -> new NotFoundException("Пользователь с username "+ username+" не найден")) ;
    }

    @Override
    public User update(User user, int requesterId) throws SQLException{
        if(user.getId() != requesterId){
            throw new ForbiddenException("Можно редактировать только свой профиль");
        }
        validateProfile(user.getName(), user.getBirthday(), user.getUsername());
        Optional<User> sameUsername = userDao.findByUsername(user.getUsername());
        if(sameUsername.isPresent() && sameUsername.get().getId() != user.getId()){
            throw new ConflictException("Логин «" + user.getUsername() + "» уже занят");
        }

        userDao.update(user);
        return user;
    }

    @Override
    public void delete(int id, int requesterId) throws SQLException{
        if(id != requesterId){
            throw new ForbiddenException("Можно удалить только свой аккаунт");
        }
        userDao.delete(id);
    }

    @Override
    public void subscribe(int subscriberId, int targetId) throws SQLException{
        if(subscriberId == targetId){
            throw new BadRequestException("Нельзя подписаться на себя");
        }
        userDao.subscribe(subscriberId, targetId);
    }

    @Override
    public void unsubscribe(int subscriberId, int targetId) throws SQLException{
        userDao.unsubscribe(subscriberId, targetId);
    }

    @Override
    public List<User> importUsers(List<ImportUserRequest> requests) throws SQLException{
        List<User> created = new ArrayList<>();
        for(ImportUserRequest r : requests) {
            User.Role role = parseRole(r.role());
            User newUser= new User(0, r.name(), r.birthday(), r.username(), null, role);
            created.add(register(newUser, r.password()));
        }
        return created;
    }

    @Override
    public boolean isSubscribedDirectly(int subscriberId, int targetId) throws SQLException{
        return userDao.isSubscribedDirectly(subscriberId, targetId);
    }

    private void validateProfile(String name, LocalDate birthday, String username){
        Validation.requireText(name, "Имя не может быть пустым");
        Validation.requireNotNull(birthday, "Дата рождения не может быть пустой");
        Validation.requireText(username, "Логин не может быть пустым");
        if(birthday.isAfter(LocalDate.now())){
            throw new BadRequestException("Дата рождения не может быть в будущем");
        }
    }

    private User.Role parseRole(String role){
        if(role == null){
            return User.Role.USER;
        }
        try {
            return User.Role.valueOf(role);
        } catch (IllegalArgumentException e){
            throw new BadRequestException("Неизвестная роль: " + role);
        }
    }
}
