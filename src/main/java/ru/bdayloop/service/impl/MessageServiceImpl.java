package ru.bdayloop.service.impl;

import org.springframework.stereotype.Service;
import ru.bdayloop.dao.i.MessageDao;
import ru.bdayloop.dao.i.UserDao;
import ru.bdayloop.exception.ForbiddenException;
import ru.bdayloop.model.Message;
import ru.bdayloop.service.i.MessageService;
import ru.bdayloop.service.validation.Validation;

import java.sql.SQLException;
import java.util.List;

@Service
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

        Validation.requireText(message.getText(), "Сообщение не может быть пустым");
        return messageDao.create(message);
    }

    @Override
    public List<Message> getMessages(int subjectUserId, int senderId) throws SQLException{
        if(subjectUserId==senderId) throw new ForbiddenException("Нельзя видеть чат про свой же подарок");
        if(!userDao.isSubscribedDirectlyOrViaGroup(senderId, subjectUserId)) throw new ForbiddenException("Нет доступа к чату - сначала подпишитесь на именинника или на группу, в которой он состоит");

        return messageDao.findBySubjectUserId(subjectUserId);
    }
}
