package ru.bdayloop;

import ru.bdayloop.dao.GiftDao;
import ru.bdayloop.dao.GroupDao;
import ru.bdayloop.dao.UserDao;
import ru.bdayloop.db.ConnectionManager;
import ru.bdayloop.model.Gift;
import ru.bdayloop.model.Group;
import ru.bdayloop.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main (String[] args) throws SQLException{
        try (Connection connection = ConnectionManager.getConnection()){
            System.out.println("Подключились:" + !connection.isClosed());
        } catch(SQLException e){
            e.printStackTrace();
        }

        UserDao userDao= new UserDao();
        GroupDao groupDao = new GroupDao();
        GiftDao giftDao =new GiftDao();

        User newUser = new User(
                0,
                "тестовая Катя",
                LocalDate.of(2007, 8, 4),
                "test_kate_" + System.currentTimeMillis(),
                "test-hash",
                User.Role.USER);
        User user = userDao.create(newUser);
        System.out.println("Пользователь сохранён с id = " + user.getId());

        Group newGroup = new Group(0,
                "девчонки",
                user.getId());
        Group group = groupDao.create(newGroup);
        System.out.println("Создана группа с id = " + group.getId());

        groupDao.joinGroup(user.getId(), group.getId());
        List<Integer> members = groupDao.groupMembers(group.getId());
        System.out.println("Участники группы "+ group.getName()+" : "+ members);

        Gift newGift = new Gift(0,
                user.getId(),
                "конфетки");
        Gift gift = giftDao.create(newGift);
        System.out.println("Создан подарок "+ gift.getTitle() + " с id "+ gift.getId());

        Gift newGift1 = new Gift(0,
                user.getId(),
                "мармеладки");
        Gift gift1 = giftDao.create(newGift1);
        System.out.println("Создан подарок "+ gift.getTitle() + " с id "+ gift1.getId());

        List<Gift> gifts = giftDao.findByUserId(user.getId());
        System.out.println("Подарки пользователя: "+ gifts.size());

        try{
            groupDao.delete(group.getId(), 999);
            System.out.println("ОШИБКА: кто угодно может удалить группу!");
        } catch (SQLException e){
            System.out.println("Ожидаемо отказано: "+ e.getMessage());
        }

        try{
            groupDao.delete(group.getId(), user.getId());
            System.out.println("Создатель удалил группу");
        } catch (SQLException e){
            System.out.println("ОШИБКА: отказано: "+ e.getMessage());
        }
    }
}
