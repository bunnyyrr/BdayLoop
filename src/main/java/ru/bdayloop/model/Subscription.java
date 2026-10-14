package ru.bdayloop.model;

import jakarta.persistence.*;
import ru.bdayloop.model.id.SubscriptionId;

@Entity
@Table(name = "subscriptions")
public class Subscription {
    @EmbeddedId
    private SubscriptionId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("subscriberId")
    @JoinColumn(name = "subscriber_id")
    private User subscriber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("targetId")
    @JoinColumn(name = "target_id")
    private User target;

    protected Subscription() {
    }

    public Subscription(User subscriber, User target) {
        this.id = new SubscriptionId(subscriber.getId(), target.getId());
        this.subscriber = subscriber;
        this.target = target;
    }

    public SubscriptionId getId() {
        return id;
    }

    public User getSubscriber() {
        return subscriber;
    }

    public User getTarget() {
        return target;
    }
}
