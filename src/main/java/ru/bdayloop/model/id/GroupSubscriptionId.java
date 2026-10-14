package ru.bdayloop.model.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public record GroupSubscriptionId(@Column(name="subscriber_id") Integer subscriberId, @Column(name = "group_id") Integer groupId) implements Serializable {
}
