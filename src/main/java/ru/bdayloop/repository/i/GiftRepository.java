package ru.bdayloop.repository.i;

import ru.bdayloop.model.Gift;

import java.util.List;
import java.util.Optional;

public interface GiftRepository {
    Gift save(Gift gift);
    Optional<Gift> findById(Integer id);
    List<Gift> findByOwnerId(Integer ownerId);
    void delete(Gift gift);
}
