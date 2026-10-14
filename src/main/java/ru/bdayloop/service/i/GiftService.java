package ru.bdayloop.service.i;

import ru.bdayloop.model.Gift;

import java.util.List;

public interface GiftService {
    Gift create(int ownerId, String title);
    void delete(int id, int requesterId);
    List<Gift> findByUserId(int userId);
}
