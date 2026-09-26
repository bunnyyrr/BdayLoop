package ru.bdayloop.service.impl;

import org.mindrot.jbcrypt.BCrypt;
import ru.bdayloop.dao.UserDao;
import ru.bdayloop.exception.ForbiddenException;
import ru.bdayloop.exception.NotFoundException;
import ru.bdayloop.exception.UnauthorizedException;
import ru.bdayloop.model.User;
import ru.bdayloop.service.UserService;
import ru.bdayloop.web.dto.ImportUserRequest;

import java.sql.SQLException;
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
            User.Role role = r.role() != null ? User.Role.valueOf(r.role()) : User.Role.USER;
            User newUser= new User(0, r.name(), r.birthday(), r.username(), null, role);
            created.add(register(newUser, r.password()));
        }
        return created;
    }

    @Override
    public boolean isSubscribedDirectly(int subscriberId, int targetId) throws SQLException{
        return userDao.isSubscribedDirectly(subscriberId, targetId);
    }
}
