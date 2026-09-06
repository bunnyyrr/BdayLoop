package ru.bdayloop;

import ru.bdayloop.dao.UserDao;
import ru.bdayloop.db.ConnectionManager;
import ru.bdayloop.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class Main {
    public static void main (String[] args) throws SQLException{
        try (Connection connection = ConnectionManager.getConnection()){
            System.out.println("Подключились:" + !connection.isClosed());
        } catch(SQLException e){
            e.printStackTrace();
        }

        UserDao userDao = new UserDao();
        User newUser = new User(
                0,
                "тестовая Катя",
                LocalDate.of(2007, 8, 4),
                "test_kate",
                "test-hash",
                User.Role.USER);
        User saved = userDao.create(newUser);
        System.out.println("Сохранён с id = " + saved.getId());

    }
}
