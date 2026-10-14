package ru.bdayloop.model.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public record SubscriptionId(@Column(name = "subscriber_id") Integer subscriberId, @Column(name = "target_id") Integer targetId) implements Serializable {
}
