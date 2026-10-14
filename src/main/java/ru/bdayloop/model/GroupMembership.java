package ru.bdayloop.model;

import jakarta.persistence.*;

import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;
import ru.bdayloop.model.id.GroupMembershipId;

import java.time.LocalDateTime;

@Entity
@Table(name="group_members")
public class GroupMembership {
    @EmbeddedId
    private GroupMembershipId id;

    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @MapsId("groupId")
    @JoinColumn(name="group_id")
    private Group group;

    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name="user_id")
    private User user;

    @Generated(event = EventType.INSERT)
    @Column(name = "joined_at", insertable = false, updatable = false)
    private LocalDateTime joinedAt;

    protected GroupMembership (){}

    public GroupMembership(Group group, User user) {
        this.id = new GroupMembershipId(group.getId(), user.getId());
        this.group = group;
        this.user = user;
    }

    public GroupMembershipId getId() {
        return id;
    }

    public Group getGroup() {
        return group;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
}
