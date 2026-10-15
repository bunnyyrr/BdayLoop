package ru.bdayloop.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.bdayloop.model.Group;
import ru.bdayloop.model.GroupMembership;
import ru.bdayloop.model.User;
import ru.bdayloop.model.id.GroupMembershipId;
import ru.bdayloop.repository.i.MembershipRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
class JpaMembershipRepositoryTest extends DaoTestBase {

    @Autowired
    MembershipRepository membershipRepository;
    @PersistenceContext
    EntityManager em;

    private User newUser(String username) {
        return userRepository.save(new User(null, username, LocalDate.of(2000, 1, 1), username, "h", User.Role.USER));
    }

    @Test
    void saveAndExists_workWithCompositeKey() {
        User kate = newUser("kate");
        Group club = groupRepository.save(new Group("Клуб", null));
        membershipRepository.save(new GroupMembership(club, kate));
        em.flush();
        em.clear();

        assertTrue(membershipRepository.exists(club.getId(), kate.getId()));
        assertFalse(membershipRepository.exists(club.getId(), 999));
    }

    @Test
    void save_fillsJoinedAtGeneratedByDatabase() {
        User kate = newUser("kate");
        Group club = groupRepository.save(new Group("Клуб", null));

        GroupMembership saved = membershipRepository.save(new GroupMembership(club, kate));
        em.flush();
        em.clear();

        GroupMembership reloaded = em.find(GroupMembership.class, new GroupMembershipId(club.getId(), kate.getId()));
        assertNotNull(reloaded.getJoinedAt());
        assertEquals(saved.getId(), reloaded.getId());
    }

    @Test
    void findMembers_returnsUsersOfThatGroupOnly_withOneQuery() {
        User kate = newUser("kate");
        User anya = newUser("anya");
        Group club = groupRepository.save(new Group("Клуб", null));
        Group team = groupRepository.save(new Group("Сборная", null));
        membershipRepository.save(new GroupMembership(club, kate));
        membershipRepository.save(new GroupMembership(club, anya));
        membershipRepository.save(new GroupMembership(team, kate));
        em.flush();
        em.clear();
        Statistics stats = em.getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();

        List<User> members = membershipRepository.findMembers(club.getId());
        List<String> names = members.stream().map(u-> u.getName()).toList();

        assertEquals(2, members.size());
        assertTrue(names.contains("kate"));
        assertTrue(names.contains("anya"));
        assertEquals(1, stats.getPrepareStatementCount());
    }

    @Test
    void delete_removesMembership_andIsSilentWhenAbsent() {
        User kate = newUser("kate");
        Group club = groupRepository.save(new Group("Клуб", null));
        membershipRepository.save(new GroupMembership(club, kate));
        em.flush();
        em.clear();

        membershipRepository.delete(club.getId(), kate.getId());
        membershipRepository.delete(club.getId(), kate.getId());
        em.flush();
        em.clear();

        assertFalse(membershipRepository.exists(club.getId(), kate.getId()));
    }
}
