package ru.bdayloop.model;

import jakarta.persistence.*;
import ru.bdayloop.model.id.GroupSubscriptionId;

@Entity
@Table(name= "group_subscriptions")
public class GroupSubscription{
    @EmbeddedId
    private GroupSubscriptionId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("subscriberId")
    @JoinColumn(name="subscriber_id")
    private User subscriber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("groupId")
    @JoinColumn(name= "group_id")
    private Group group;

    protected GroupSubscription() {
    }

    public GroupSubscription(User subscriber, Group group){
        this.id= new GroupSubscriptionId(subscriber.getId(), group.getId());
        this.subscriber = subscriber;
        this.group=group;
    }

    public GroupSubscriptionId getId() {
        return id;
    }

    public User getSubscriber() {
        return subscriber;
    }

    public Group getGroup() {
        return group;
    }
}