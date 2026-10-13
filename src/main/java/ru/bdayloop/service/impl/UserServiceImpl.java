package ru.bdayloop.service.impl;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bdayloop.dao.i.SubscriptionDao;
import ru.bdayloop.exception.*;
import ru.bdayloop.model.User;
import ru.bdayloop.repository.i.UserRepository;
import ru.bdayloop.service.command.UserRegistration;
import ru.bdayloop.service.i.UserService;
import ru.bdayloop.service.validation.Validation;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final SubscriptionDao subscriptionDao;
    public UserServiceImpl(UserRepository userRepository, SubscriptionDao subscriptionDao){
        this.userRepository = userRepository;
        this.subscriptionDao = subscriptionDao;
    }

    @Override
    public User register(User user, String plainPassword) throws SQLException{
        validateProfile(user.getName(), user.getBirthday(), user.getUsername());
        Validation.requireText(plainPassword, "Пароль не может быть пустым");
        if(userRepository.findByUsername(user.getUsername()).isPresent()){
            throw new ConflictException("Логин «" + user.getUsername() + "» уже занят");
        }

        String hash = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        User toCreateUser = new User(
                null,
                user.getName(),
                user.getBirthday(),
                user.getUsername(),
                hash,
                user.getRole()
        );
        return userRepository.save(toCreateUser);
    }

    @Override
    @Transactional(readOnly = true)
    public User login(String username, String plainPassword) throws SQLException{
        Validation.requireText(username, "Введите логин");
        Validation.requireText(plainPassword, "Введите пароль");

        Optional<User> found = userRepository.findByUsername(username);
        if(found.isEmpty() || !BCrypt.checkpw(plainPassword, found.get().getPasswordHash())){
            throw new UnauthorizedException("Неверный логин или пароль");
        }
        return found.get();
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(int id) throws SQLException{
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь с id "+ id+" не найден"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByName(String name) throws SQLException{
        return userRepository.search(name);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) throws SQLException{
        return userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("Пользователь с username "+ username+" не найден")) ;
    }

    @Override
    public User update(User user, int requesterId) throws SQLException{
        if(user.getId() != requesterId){
            throw new ForbiddenException("Можно редактировать только свой профиль");
        }
        validateProfile(user.getName(), user.getBirthday(), user.getUsername());
        Optional<User> sameUsername = userRepository.findByUsername(user.getUsername());
        if(sameUsername.isPresent() && !sameUsername.get().getId().equals(user.getId())){
            throw new ConflictException("Логин «" + user.getUsername() + "» уже занят");
        }

        User managed = userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + user.getId() + " не найден"));
        managed.updateProfile(user.getName(), user.getBirthday(), user.getUsername());
        return managed;
    }

    @Override
    public void delete(int id, int requesterId) throws SQLException{
        if(id != requesterId){
            throw new ForbiddenException("Можно удалить только свой аккаунт");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        userRepository.delete(user);
    }

    @Override
    public void subscribe(int subscriberId, int targetId) throws SQLException{
        if(subscriberId == targetId){
            throw new BadRequestException("Нельзя подписаться на себя");
        }
        subscriptionDao.subscribe(subscriberId, targetId);
    }

    @Override
    public void unsubscribe(int subscriberId, int targetId) throws SQLException{
        subscriptionDao.unsubscribe(subscriberId, targetId);
    }

    @Override
    public List<User> importUsers(List<UserRegistration> registrations) throws SQLException{
        List<User> created = new ArrayList<>();
        for(UserRegistration r : registrations) {
            created.add(register(r.user(), r.password()));
        }
        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSubscribedDirectly(int subscriberId, int targetId) throws SQLException{
        return subscriptionDao.isSubscribedDirectly(subscriberId, targetId);
    }

    private void validateProfile(String name, LocalDate birthday, String username){
        Validation.requireText(name, "Имя не может быть пустым");
        Validation.requireNotNull(birthday, "Дата рождения не может быть пустой");
        Validation.requireText(username, "Логин не может быть пустым");
        if(birthday.isAfter(LocalDate.now())){
            throw new BadRequestException("Дата рождения не может быть в будущем");
        }
    }
}