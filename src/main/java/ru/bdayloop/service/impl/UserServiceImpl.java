package ru.bdayloop.service.impl;

import org.mindrot.jbcrypt.BCrypt;
import ru.bdayloop.dao.UserDao;
import ru.bdayloop.model.User;
import ru.bdayloop.service.UserService;

import java.sql.SQLException;
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
            throw new SQLException("Неверный логин или пароль");
        }
        return found.get();
    }

    @Override
    public User findById(int id) throws SQLException{
        return userDao.findById(id).orElseThrow(() -> new SQLException("Пользователь с id "+ id+" не найден"));
    }

    @Override
    public List<User> findByName(String name) throws SQLException{
        return userDao.findByName(name);
    }

    @Override
    public User findByUsername(String username) throws SQLException{
        return userDao.findByUsername(username).orElseThrow(() -> new SQLException("Пользователь с username "+ username+" не найден")) ;
    }

    @Override
    public User update(User user) throws SQLException{
        userDao.update(user);
        return user;
    }

    @Override
    public void delete(int id) throws SQLException{
        userDao.delete(id);
    }

    @Override
    public void subscribe(int subscriberId, int targetId) throws SQLException{
        userDao.subscribe(subscriberId, targetId);
    }

    @Override
    public void unsubscribe(int subscriberId, int targetId) throws SQLException{
        userDao.subscribe(subscriberId, targetId);
    }
}
