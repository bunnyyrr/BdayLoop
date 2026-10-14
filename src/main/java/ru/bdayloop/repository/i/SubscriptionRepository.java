package ru.bdayloop.repository.i;

import ru.bdayloop.model.GroupSubscription;
import ru.bdayloop.model.Subscription;

public interface SubscriptionRepository {
    Subscription save(Subscription subscription);
    boolean isSubscribedDirectly(Integer subscriberId, Integer targetId);
    void deleteSubscription(Integer subscriberId, Integer targetId);

    GroupSubscription save(GroupSubscription subscription);
    boolean isSubscribedToGroup(Integer subscriberId, Integer groupId);
    void deleteGroupSubscription(Integer subscriberId, Integer groupId);

    boolean isSubscribedDirectlyOrViaGroup(Integer subscriberId, Integer targetId);
}
