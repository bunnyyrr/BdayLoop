package ru.bdayloop.service;

import ru.bdayloop.dao.MessageDao;
import ru.bdayloop.dao.UserDao;
import ru.bdayloop.model.Message;

import java.sql.SQLException;
import java.util.List;

public class MessageServiceImpl implements MessageService{
    private final MessageDao messageDao;
    private final UserDao userDao;
    public MessageServiceImpl(MessageDao messageDao, UserDao userDao){
        this.messageDao =messageDao;
        this.userDao=userDao;
    }

    @Override
    public Message sendMessage(Message message) throws SQLException{
        if(message.getSenderId()==message.getSubjectUserId()) throw new SQLException("Нельзя обсуждать свой же подарок");
        if(!userDao.isSubscribed(message.getSenderId(), message.getSubjectUserId())) throw new SQLException("Нет доступа к чату - сначала подпишитесь на именинника");

        return messageDao.create(message);
    }

    @Override
    public List<Message> getMessages(int subjectUserId, int senderId) throws SQLException{
        if(subjectUserId==senderId) throw new SQLException("Нельзя видеть чат про свой же подарок");
        if(!userDao.isSubscribed(senderId, subjectUserId)) throw new SQLException("Нет доступа к чату - сначала подпишитесь на именинника");

        return messageDao.findBySubjectUserId(subjectUserId);
    }
}
