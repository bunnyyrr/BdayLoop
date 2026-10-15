package ru.bdayloop.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.bdayloop.model.Group;
import ru.bdayloop.model.GroupMembership;
import ru.bdayloop.model.GroupSubscription;
import ru.bdayloop.model.Subscription;
import ru.bdayloop.model.User;
import ru.bdayloop.repository.i.MembershipRepository;
import ru.bdayloop.repository.i.SubscriptionRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
class JpaSubscriptionRepositoryTest extends DaoTestBase {

    @Autowired
    SubscriptionRepository subscriptionRepository;
    @Autowired
    MembershipRepository membershipRepository;
    @PersistenceContext
    EntityManager em;

    private User newUser(String username) {
        return userRepository.save(new User(null, username, LocalDate.of(2000, 1, 1), username, "h", User.Role.USER));
    }

    @Test
    void isSubscribedDirectlyOrViaGroup_directSubscription_returnsTrue() {
        User anya = newUser("anya");
        User kate = newUser("kate");
        subscriptionRepository.save(new Subscription(anya, kate));
        em.flush();
        em.clear();

        assertTrue(subscriptionRepository.isSubscribedDirectlyOrViaGroup(anya.getId(), kate.getId()));
    }

    @Test
    void isSubscribedDirectlyOrViaGroup_viaGroupSubscription_returnsTrue() {
        User anya = newUser("anya");
        User kate = newUser("kate");
        Group group = groupRepository.save(new Group("972501 ТГУ", null));
        membershipRepository.save(new GroupMembership(group, kate));
        subscriptionRepository.save(new GroupSubscription(anya, group));
        em.flush();
        em.clear();

        assertTrue(subscriptionRepository.isSubscribedDirectlyOrViaGroup(anya.getId(), kate.getId()));
    }

    @Test
    void isSubscribedDirectlyOrViaGroup_onlyMembershipWithoutSubscription_returnsFalse() {
        User anya = newUser("anya");
        User kate = newUser("kate");
        Group group = groupRepository.save(new Group("972501 ТГУ", null));
        membershipRepository.save(new GroupMembership(group, kate));
        membershipRepository.save(new GroupMembership(group, anya));
        em.flush();
        em.clear();

        assertFalse(subscriptionRepository.isSubscribedDirectlyOrViaGroup(anya.getId(), kate.getId()));
    }

    @Test
    void isSubscribedDirectlyOrViaGroup_noSubscription_returnsFalse() {
        User anya = newUser("anya");
        User kate = newUser("kate");

        assertFalse(subscriptionRepository.isSubscribedDirectlyOrViaGroup(anya.getId(), kate.getId()));
    }

    @Test
    void isSubscribedDirectly_ignoresGroupSubscription() {
        User anya = newUser("anya");
        User kate = newUser("kate");
        Group group = groupRepository.save(new Group("Клуб", null));
        membershipRepository.save(new GroupMembership(group, kate));
        subscriptionRepository.save(new GroupSubscription(anya, group));
        em.flush();
        em.clear();

        assertFalse(subscriptionRepository.isSubscribedDirectly(anya.getId(), kate.getId()));

        subscriptionRepository.save(new Subscription(em.getReference(User.class, anya.getId()), em.getReference(User.class, kate.getId())));
        em.flush();
        em.clear();

        assertTrue(subscriptionRepository.isSubscribedDirectly(anya.getId(), kate.getId()));
    }

    @Test
    void deleteSubscription_removesIt_andIsSilentWhenAbsent() {
        User anya = newUser("anya");
        User kate = newUser("kate");
        subscriptionRepository.save(new Subscription(anya, kate));
        em.flush();
        em.clear();

        subscriptionRepository.deleteSubscription(anya.getId(), kate.getId());
        subscriptionRepository.deleteSubscription(anya.getId(), kate.getId());
        em.flush();
        em.clear();

        assertFalse(subscriptionRepository.isSubscribedDirectly(anya.getId(), kate.getId()));
    }

    @Test
    void deleteGroupSubscription_removesIt_andIsSilentWhenAbsent() {
        User anya = newUser("anya");
        Group group = groupRepository.save(new Group("Клуб", null));
        subscriptionRepository.save(new GroupSubscription(anya, group));
        em.flush();
        em.clear();

        subscriptionRepository.deleteGroupSubscription(anya.getId(), group.getId());
        subscriptionRepository.deleteGroupSubscription(anya.getId(), group.getId());
        em.flush();
        em.clear();

        assertFalse(subscriptionRepository.isSubscribedToGroup(anya.getId(), group.getId()));
    }
}
