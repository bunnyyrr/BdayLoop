package ru.bdayloop.dao.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.bdayloop.exception.BadRequestException;
import ru.bdayloop.exception.ForbiddenException;
import ru.bdayloop.exception.NotFoundException;
import ru.bdayloop.model.Gift;
import ru.bdayloop.model.User;
import ru.bdayloop.service.i.GiftService;
import ru.bdayloop.service.i.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GiftServiceImplTest extends DaoTestBase {

    @Autowired
    GiftService giftService;
    @Autowired
    UserService userService;

    @Test
    void create_forExistingUser_returnsGiftWithOwnerId() {
        User kate = createUser("kate");

        Gift gift = giftService.create(kate.getId(), "Книга");

        assertEquals("Книга", gift.getTitle());
        assertEquals(kate.getId(), gift.getOwner().getId());
    }

    @Test
    void create_withBlankTitle_isRejected() {
        User kate = createUser("kate");

        assertThrows(BadRequestException.class, () -> giftService.create(kate.getId(), "   "));
    }

    @Test
    void create_forUnknownUser_isNotFound() {
        assertThrows(NotFoundException.class, () -> giftService.create(999, "Книга"));
    }

    @Test
    void delete_byAnotherUser_isForbidden() {
        User kate = createUser("kate");
        User anya = createUser("anya");
        Gift gift = giftService.create(kate.getId(), "Книга");

        assertThrows(ForbiddenException.class, () -> giftService.delete(gift.getId(), anya.getId()));
        assertEquals(1, giftService.findByUserId(kate.getId()).size());
    }

    @Test
    void delete_byOwner_removesGift() {
        User kate = createUser("kate");
        Gift gift = giftService.create(kate.getId(), "Книга");

        giftService.delete(gift.getId(), kate.getId());

        assertTrue(giftService.findByUserId(kate.getId()).isEmpty());
    }

    @Test
    void deletingUser_removesHisGifts()  {
        User kate = createUser("kate");
        giftService.create(kate.getId(), "Книга");
        giftService.create(kate.getId(), "Кружка");

        userService.delete(kate.getId(), kate.getId());

        List<Gift> left = giftService.findByUserId(kate.getId());
        assertTrue(left.isEmpty());
    }

    @Test
    void findByUserId_ownerIdIsReadableAfterTransactionEnds() {
        User kate = createUser("kate");
        giftService.create(kate.getId(), "Книга");

        List<Gift> gifts = giftService.findByUserId(kate.getId());

        assertEquals(kate.getId(), gifts.get(0).getOwner().getId());
        assertEquals(1, gifts.size());
    }
}