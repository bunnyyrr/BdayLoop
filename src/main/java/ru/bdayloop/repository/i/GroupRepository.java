package ru.bdayloop.repository.i;

import ru.bdayloop.model.Group;

import java.util.List;
import java.util.Optional;

public interface GroupRepository {
    Group save(Group group);
    Optional<Group> findById(Integer id);
    boolean existsByName(String name);
    List<Group> findAll();
    List<Group> findByNameContaining(String text);
    List<Group> findByMember(Integer userId);
    List<Group> findBySubscriber(Integer userId);
    void delete(Group group);
}
