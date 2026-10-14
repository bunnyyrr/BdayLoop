package ru.bdayloop.repository.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.bdayloop.model.GroupSubscription;
import ru.bdayloop.model.id.GroupSubscriptionId;
import ru.bdayloop.model.Subscription;
import ru.bdayloop.model.id.SubscriptionId;
import ru.bdayloop.repository.i.SubscriptionRepository;

@Repository
public class JpaSubscriptionRepository implements SubscriptionRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Subscription save(Subscription subscription) {
        em.persist(subscription);
        return subscription;
    }

    @Override
    public boolean isSubscribedDirectly(Integer subscriberId, Integer targetId) {
        return em.find(Subscription.class, new SubscriptionId(subscriberId, targetId)) != null;
    }

    @Override
    public void deleteSubscription(Integer subscriberId, Integer targetId) {
        Subscription subscription = em.find(Subscription.class, new SubscriptionId(subscriberId, targetId));
        if (subscription != null) {
            em.remove(subscription);
        }
    }

    @Override
    public GroupSubscription save(GroupSubscription subscription) {
        em.persist(subscription);
        return subscription;
    }

    @Override
    public boolean isSubscribedToGroup(Integer subscriberId, Integer groupId) {
        return em.find(GroupSubscription.class, new GroupSubscriptionId(subscriberId, groupId)) != null;
    }

    @Override
    public void deleteGroupSubscription(Integer subscriberId, Integer groupId) {
        GroupSubscription subscription = em.find(GroupSubscription.class, new GroupSubscriptionId(subscriberId, groupId));
        if (subscription != null) {
            em.remove(subscription);
        }
    }

    @Override
    public boolean isSubscribedDirectlyOrViaGroup(Integer subscriberId, Integer targetId) {
        if (isSubscribedDirectly(subscriberId, targetId)) {
            return true;
        }
        Long viaGroup = em.createQuery("""
                        select count(gs) from GroupSubscription gs, GroupMembership gm
                        where gm.id.groupId = gs.id.groupId
                          and gs.id.subscriberId = :subscriberId
                          and gm.id.userId = :targetId
                        """, Long.class)
                .setParameter("subscriberId", subscriberId)
                .setParameter("targetId", targetId)
                .getSingleResult();
        return viaGroup > 0;
    }
}