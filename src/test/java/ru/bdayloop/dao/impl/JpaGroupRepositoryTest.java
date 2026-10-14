package ru.bdayloop.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.bdayloop.model.Group;
import ru.bdayloop.model.User;
import ru.bdayloop.repository.i.GroupRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
class JpaGroupRepositoryTest extends DaoTestBase {

    @Autowired
    GroupRepository repository;
    @PersistenceContext
    EntityManager em;

    private User newUser(String username) {
        return userRepository.save(new User(null, username, LocalDate.of(2000, 1, 1), username, "h", User.Role.USER));
    }

    private void addMember(Group group, User user) {
        em.createNativeQuery("insert into group_members (group_id, user_id) values (:g, :u)")
                .setParameter("g", group.getId())
                .setParameter("u", user.getId())
                .executeUpdate();
    }

    private void addSubscription(Group group, User user) {
        em.createNativeQuery("insert into group_subscriptions (subscriber_id, group_id) values (:u, :g)")
                .setParameter("g", group.getId())
                .setParameter("u", user.getId())
                .executeUpdate();
    }

    @Test
    void saveAndFindById_returnsGroupWithCreator() {
        User kate = newUser("kate");
        Group saved = repository.save(new Group("Тестовая группа", kate));
        em.flush();
        em.clear();

        Group found = repository.findById(saved.getId()).orElseThrow();

        assertEquals("Тестовая группа", found.getName());
        assertEquals(kate.getId(), found.getCreatedBy().getId());
    }

    @Test
    void save_withoutCreator_isAllowed() {
        Group saved = repository.save(new Group("Без создателя", null));
        em.flush();
        em.clear();

        assertNull(repository.findById(saved.getId()).orElseThrow().getCreatedBy());
    }

    @Test
    void existsByName_isExactMatch() {
        repository.save(new Group("Клуб", null));
        em.flush();

        assertTrue(repository.existsByName("Клуб"));
        assertFalse(repository.existsByName("Клу"));
    }

    @Test
    void findByNameContaining_ignoresCase() {
        repository.save(new Group("Волейбольная сборная", null));
        repository.save(new Group("Шахматный клуб", null));
        em.flush();
        em.clear();

        List<Group> found = repository.findByNameContaining("СБОРН");

        assertEquals(1, found.size());
        assertEquals("Волейбольная сборная", found.get(0).getName());
    }

    @Test
    void findAll_returnsGroupsInCreationOrder() {
        repository.save(new Group("Первая", null));
        repository.save(new Group("Вторая", null));
        em.flush();
        em.clear();

        List<Group> all = repository.findAll();

        assertEquals(2, all.size());
        assertEquals("Первая", all.get(0).getName());
    }

    @Test
    void findByMember_returnsOnlyGroupsUserJoined() {
        User kate = newUser("kate");
        Group club = repository.save(new Group("Клуб", null));
        repository.save(new Group("Чужая", null));
        em.flush();
        addMember(club, kate);
        em.clear();

        List<Group> groups = repository.findByMember(kate.getId());

        assertEquals(1, groups.size());
        assertEquals("Клуб", groups.get(0).getName());
    }

    @Test
    void findBySubscriber_returnsOnlySubscribedGroups() {
        User anya = newUser("anya");
        Group club = repository.save(new Group("Клуб", null));
        Group team = repository.save(new Group("Сборная", null));
        em.flush();
        addSubscription(club, anya);
        addMember(team, anya);
        em.clear();

        List<Group> groups = repository.findBySubscriber(anya.getId());

        assertEquals(1, groups.size());
        assertEquals("Клуб", groups.get(0).getName());
    }

    @Test
    void delete_removesGroup() {
        Group group = repository.save(new Group("Клуб", null));
        em.flush();

        repository.delete(group);
        em.flush();
        em.clear();

        assertTrue(repository.findById(group.getId()).isEmpty());
    }
}
