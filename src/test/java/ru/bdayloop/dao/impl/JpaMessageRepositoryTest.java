package ru.bdayloop.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.bdayloop.model.Message;
import ru.bdayloop.model.User;
import ru.bdayloop.repository.i.MessageRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
class JpaMessageRepositoryTest extends DaoTestBase {

    @Autowired
    MessageRepository messageRepository;
    @PersistenceContext
    EntityManager em;

    private User newUser(String username) {
        return userRepository.save(new User(null, username, LocalDate.of(2000, 1, 1), username, "h", User.Role.USER));
    }

    @Test
    void saveAndFindBySubjectId_returnsMessagesInOrder() {
        User subject = newUser("subject");
        User sender = newUser("sender");
        messageRepository.save(new Message(subject, sender, "первое"));
        messageRepository.save(new Message(subject, sender, "второе"));
        em.flush();
        em.clear();

        List<Message> messages = messageRepository.findBySubjectId(subject.getId());

        assertEquals(2, messages.size());
        assertEquals("первое", messages.get(0).getText());
        assertEquals("второе", messages.get(1).getText());
        assertEquals(sender.getId(), messages.get(0).getSender().getId());
    }

    @Test
    void save_fillsCreatedAtGeneratedByDatabase() {
        User subject = newUser("subject");
        User sender = newUser("sender");

        Message saved = messageRepository.save(new Message(subject, sender, "привет"));
        em.flush();

        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void findBySubjectId_ignoresOtherChats() {
        User kate = newUser("kate");
        User anya = newUser("anya");
        User sender = newUser("sender");
        messageRepository.save(new Message(kate, sender, "для Кати"));
        messageRepository.save(new Message(anya, sender, "для Ани"));
        em.flush();
        em.clear();

        assertEquals(1, messageRepository.findBySubjectId(kate.getId()).size());
    }

    @Test
    void findBySubjectId_loadsSendersWithOneQuery() {
        User subject = newUser("subject");
        for (int i = 0; i < 3; i++) {
            messageRepository.save(new Message(subject, newUser("sender" + i), "привет"));
        }
        em.flush();
        em.clear();
        Statistics stats = em.getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();

        List<Message> messages = messageRepository.findBySubjectId(subject.getId());
        messages.forEach(m -> m.getSender().getName());

        assertEquals(1, stats.getPrepareStatementCount());
    }
}