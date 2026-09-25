package ru.bdayloop.service.impl;

import ru.bdayloop.dao.MessageDao;
import ru.bdayloop.dao.UserDao;
import ru.bdayloop.exception.ForbiddenException;
import ru.bdayloop.model.Message;
import ru.bdayloop.service.MessageService;

import java.sql.SQLException;
import java.util.List;

public class MessageServiceImpl implements MessageService {
    private final MessageDao messageDao;
    private final UserDao userDao;
    public MessageServiceImpl(MessageDao messageDao, UserDao userDao){
        this.messageDao =messageDao;
        this.userDao=userDao;
    }

    @Override
    public Message sendMessage(Message message) throws SQLException{
        if(message.getSenderId()==message.getSubjectUserId()) throw new ForbiddenException("Нельзя обсуждать свой же подарок");
        if(!userDao.isSubscribedDirectlyOrViaGroup(message.getSenderId(), message.getSubjectUserId())) throw new ForbiddenException("Нет доступа к чату - сначала подпишитесь на именинника или на группу, в которой он состоит");

        return messageDao.create(message);
    }

    @Override
    public List<Message> getMessages(int subjectUserId, int senderId) throws SQLException{
        if(subjectUserId==senderId) throw new ForbiddenException("Нельзя видеть чат про свой же подарок");
        if(!userDao.isSubscribedDirectlyOrViaGroup(senderId, subjectUserId)) throw new ForbiddenException("Нет доступа к чату - сначала подпишитесь на именинника или на группу, в которой он состоит");

        return messageDao.findBySubjectUserId(subjectUserId);
    }
}
