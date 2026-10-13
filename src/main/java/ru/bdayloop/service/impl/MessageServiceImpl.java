package ru.bdayloop.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bdayloop.dao.i.SubscriptionDao;
import ru.bdayloop.exception.ForbiddenException;
import ru.bdayloop.exception.NotFoundException;
import ru.bdayloop.model.Message;
import ru.bdayloop.model.User;
import ru.bdayloop.repository.i.MessageRepository;
import ru.bdayloop.repository.i.UserRepository;
import ru.bdayloop.service.i.MessageService;
import ru.bdayloop.service.validation.Validation;

import java.sql.SQLException;
import java.util.List;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SubscriptionDao subscriptionDao;
    public MessageServiceImpl(MessageRepository messageRepositor, UserRepository userRepository, SubscriptionDao subscriptionDao){
        this.messageRepository =messageRepositor;
        this.userRepository= userRepository;
        this.subscriptionDao=subscriptionDao;
    }

    @Override
    public Message sendMessage(int subjectId, int senderId, String text) throws SQLException{
        if(senderId==subjectId) throw new ForbiddenException("Нельзя обсуждать свой же подарок");
        if(!subscriptionDao.isSubscribedDirectlyOrViaGroup(senderId, subjectId)) throw new ForbiddenException("Нет доступа к чату - сначала подпишитесь на именинника или на группу, в которой он состоит");

        Validation.requireText(text, "Сообщение не может быть пустым");

        User subject = userRepository.findById(subjectId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + subjectId + " не найден"));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + senderId + " не найден"));
        return messageRepository.save(new Message(subject, sender, text));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getMessages(int subjectUserId, int senderId) throws SQLException{
        if(subjectUserId==senderId) throw new ForbiddenException("Нельзя видеть чат про свой же подарок");
        if(!subscriptionDao.isSubscribedDirectlyOrViaGroup(senderId, subjectUserId)) throw new ForbiddenException("Нет доступа к чату - сначала подпишитесь на именинника или на группу, в которой он состоит");

        return messageRepository.findBySubjectId(subjectUserId);
    }
}
