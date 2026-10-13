package ru.bdayloop.dao.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.bdayloop.exception.BadRequestException;
import ru.bdayloop.exception.ForbiddenException;
import ru.bdayloop.model.Message;
import ru.bdayloop.model.User;
import ru.bdayloop.repository.i.MessageRepository;
import ru.bdayloop.service.i.MessageService;
import ru.bdayloop.service.i.UserService;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageServiceImplTest extends DaoTestBase {

    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    @Autowired
    MessageRepository messageRepository;

    @Test
    void sendMessage_withoutSubscription_isForbidden() {
        User birthdayPerson = createUser("birthday");
        User stranger = createUser("stranger");

        assertThrows(ForbiddenException.class,
                () -> messageService.sendMessage(birthdayPerson.getId(), stranger.getId(), "привет"));
    }

    @Test
    void sendMessage_aboutYourself_isForbidden() {
        User birthdayPerson = createUser("birthday");

        assertThrows(ForbiddenException.class,
                () -> messageService.sendMessage(birthdayPerson.getId(), birthdayPerson.getId(), "привет"));
    }

    @Test
    void sendMessage_withBlankText_isRejected() throws SQLException {
        User birthdayPerson = createUser("birthday");
        User friend = createUser("friend");
        userService.subscribe(friend.getId(), birthdayPerson.getId());

        assertThrows(BadRequestException.class,
                () -> messageService.sendMessage(birthdayPerson.getId(), friend.getId(), "   "));
    }

    @Test
    void sendAndGetMessages_whenSubscribed_works() throws SQLException {
        User birthdayPerson = createUser("birthday");
        User friend = createUser("friend");
        userService.subscribe(friend.getId(), birthdayPerson.getId());

        Message sent = messageService.sendMessage(birthdayPerson.getId(), friend.getId(), "Скинемся?");
        List<Message> messages = messageService.getMessages(birthdayPerson.getId(), friend.getId());

        assertNotNull(sent.getCreatedAt());
        assertEquals(1, messages.size());
        assertEquals("Скинемся?", messages.get(0).getText());
        assertEquals(friend.getId(), messages.get(0).getSender().getId());
        assertEquals(birthdayPerson.getId(), messages.get(0).getSubject().getId());
    }

    @Test
    void getMessages_forBirthdayPerson_isForbidden() {
        User birthdayPerson = createUser("birthday");

        assertThrows(ForbiddenException.class,
                () -> messageService.getMessages(birthdayPerson.getId(), birthdayPerson.getId()));
    }

    @Test
    void deletingSender_keepsHisMessagesWithoutAuthor() throws SQLException {
        User birthdayPerson = createUser("birthday");
        User author = createUser("author");
        User reader = createUser("reader");
        userService.subscribe(author.getId(), birthdayPerson.getId());
        userService.subscribe(reader.getId(), birthdayPerson.getId());
        messageService.sendMessage(birthdayPerson.getId(), author.getId(), "Я купил книгу");

        userService.delete(author.getId(), author.getId());

        List<Message> messages = messageService.getMessages(birthdayPerson.getId(), reader.getId());
        assertEquals(1, messages.size());
        assertEquals("Я купил книгу", messages.get(0).getText());
        assertNull(messages.get(0).getSender());
    }

    @Test
    void deletingBirthdayPerson_removesTheirChat() throws SQLException {
        User birthdayPerson = createUser("birthday");
        User friend = createUser("friend");
        userService.subscribe(friend.getId(), birthdayPerson.getId());
        messageService.sendMessage(birthdayPerson.getId(), friend.getId(), "Скинемся?");

        userService.delete(birthdayPerson.getId(), birthdayPerson.getId());

        assertTrue(messageRepository.findBySubjectId(birthdayPerson.getId()).isEmpty());
    }
}