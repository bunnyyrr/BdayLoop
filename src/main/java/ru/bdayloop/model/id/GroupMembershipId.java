package ru.bdayloop.model.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public record GroupMembershipId(@Column(name= "group_id") Integer groupId, @Column(name= "user_id") Integer userId) implements Serializable { }
