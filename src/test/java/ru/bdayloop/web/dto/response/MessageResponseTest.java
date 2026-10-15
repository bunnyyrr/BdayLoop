package ru.bdayloop.web.dto.response;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.bdayloop.model.Message;
import ru.bdayloop.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MessageResponseTest {

    private User user(int id, String username) {
        return new User(id, username, LocalDate.of(2000, 1, 1), username, "h", User.Role.USER);
    }

    private Message message(User subject, User sender) {
        Message message = new Message(subject, sender, "привет");
        ReflectionTestUtils.setField(message, "id", 10);
        return message;
    }

    @Test
    void from_withSender_containsSenderId() {
        MessageResponse response = MessageResponse.from(message(user(1, "birthday"), user(2, "friend")));

        assertEquals(10, response.id());
        assertEquals(1, response.subjectUserId());
        assertEquals(2, response.senderId());
        assertEquals("friend", response.senderName());
        assertEquals("привет", response.text());
    }

    @Test
    void from_whenSenderDeleted_hasNullSenderId() {
        MessageResponse response = MessageResponse.from(message(user(1, "birthday"), null));

        assertNull(response.senderId());
        assertNull(response.senderName());
        assertEquals("привет", response.text());
    }
}