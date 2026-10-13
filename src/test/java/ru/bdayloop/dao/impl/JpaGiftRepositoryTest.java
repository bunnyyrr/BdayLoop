package ru.bdayloop.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.bdayloop.model.Gift;
import ru.bdayloop.model.User;
import ru.bdayloop.repository.i.GiftRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
class JpaGiftRepositoryTest extends DaoTestBase {

    @Autowired
    GiftRepository giftRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    void saveAndFindByOwnerId_returnsOnlyOwnersGifts() {
        User kate = userRepository.save(new User(null, "Катя", LocalDate.of(2000, 1, 1), "kate", "h", User.Role.USER));
        User anya = userRepository.save(new User(null, "Аня", LocalDate.of(2000, 1, 1), "anya", "h", User.Role.USER));
        giftRepository.save(new Gift(kate, "Книга"));
        giftRepository.save(new Gift(kate, "Кружка"));
        giftRepository.save(new Gift(anya, "Плед"));
        em.flush();
        em.clear();

        List<Gift> gifts = giftRepository.findByOwnerId(kate.getId());

        assertEquals(2, gifts.size());
        assertEquals("Книга", gifts.get(0).getTitle());
        assertEquals("Кружка", gifts.get(1).getTitle());
        assertEquals(kate.getId(), gifts.get(0).getOwner().getId());
    }

    @Test
    void delete_removesGift() {
        User kate = userRepository.save(new User(null, "Катя", LocalDate.of(2000, 1, 1), "kate", "h", User.Role.USER));
        Gift gift = giftRepository.save(new Gift(kate, "Книга"));
        em.flush();

        giftRepository.delete(gift);
        em.flush();
        em.clear();

        assertTrue(giftRepository.findById(gift.getId()).isEmpty());
    }
}