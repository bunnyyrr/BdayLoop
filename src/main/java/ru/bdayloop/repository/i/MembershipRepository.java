package ru.bdayloop.repository.i;

import ru.bdayloop.model.GroupMembership;

import java.util.List;

public interface MembershipRepository {
    GroupMembership save(GroupMembership membership);
    boolean exists(Integer groupId, Integer userId);
    void delete(Integer groupId, Integer userId);
    List<Integer> findMemberIds(Integer groupId);
}
