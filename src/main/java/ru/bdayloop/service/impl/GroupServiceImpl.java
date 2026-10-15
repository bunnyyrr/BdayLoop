package ru.bdayloop.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bdayloop.exception.ConflictException;
import ru.bdayloop.exception.ForbiddenException;
import ru.bdayloop.exception.NotFoundException;
import ru.bdayloop.model.Group;
import ru.bdayloop.model.GroupMembership;
import ru.bdayloop.model.GroupSubscription;
import ru.bdayloop.model.User;
import ru.bdayloop.repository.i.GroupRepository;
import ru.bdayloop.repository.i.MembershipRepository;
import ru.bdayloop.repository.i.SubscriptionRepository;
import ru.bdayloop.repository.i.UserRepository;
import ru.bdayloop.service.i.GroupService;
import ru.bdayloop.service.validation.Validation;

import java.util.List;

@Service
@Transactional
public class GroupServiceImpl implements GroupService {
    private final MembershipRepository membershipRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupServiceImpl(GroupRepository groupRepository, UserRepository userRepository, MembershipRepository membershipRepository, SubscriptionRepository subscriptionRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public void delete(int groupId, int requesterId, User.Role requesterRole) {
        Group group = permissionCheck(groupId, requesterId, requesterRole);
        groupRepository.delete(group);
    }

    @Override
    public Group create(String name, int creatorId){
        Validation.requireText(name, "Название группы не может быть пустым");
        if(groupRepository.existsByName(name)){
            throw new ConflictException("Группа с названием «" + name + "» уже существует");
        }
        User creator = findExistingUser(creatorId);
        return groupRepository.save(new Group(name, creator));
    }

    @Override
    @Transactional(readOnly = true)
    public Group findById(int id){
        return groupRepository.findById(id).orElseThrow(()-> new NotFoundException("Группа с id "+ id + " не найдена"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Group> findByName(String name){
        return groupRepository.findByNameContaining(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Group> findAll(){
        return groupRepository.findAll();
    }

    @Override
    public void joinGroup(int userId, int groupId) {
        User user = findExistingUser(userId);
        Group group = findById(groupId);
        if (membershipRepository.exists(groupId, userId)) {
            throw new ConflictException("Вы уже состоите в этой группе");
        }
        membershipRepository.save(new GroupMembership(group, user));
    }

    @Override
    public void subscribeToGroup(int subscriberId, int groupId) {
        User subscriber = findExistingUser(subscriberId);
        Group group = findById(groupId);
        if (subscriptionRepository.isSubscribedToGroup(subscriberId, groupId)) {
            throw new ConflictException("Вы уже подписаны на эту группу");
        }
        subscriptionRepository.save(new GroupSubscription(subscriber, group));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> groupMembers(int groupId) {
        return membershipRepository.findMembers(groupId);
    }

    @Override
    public void leaveGroup(int groupId, int userId) {
        membershipRepository.delete(groupId, userId);
    }

    @Override
    public void unsubscribeFromGroup(int subscriberId, int groupId) {
        subscriptionRepository.deleteGroupSubscription(subscriberId, groupId);
    }

    @Override
    public Group update(int groupId, String newName, int requesterId, User.Role requesterRole){
        Group group = permissionCheck(groupId, requesterId, requesterRole);
        Validation.requireText(newName, "Название группы не может быть пустым");
        if(!newName.equals(group.getName()) && groupRepository.existsByName(newName)){
            throw new ConflictException("Группа с названием «" + newName + "» уже существует");
        }

        group.rename(newName);
        return group;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Group> findByMember(int userId){
        return groupRepository.findByMember(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Group> findBySubscriber(int subscriberId){
        return groupRepository.findBySubscriber(subscriberId);
    }

    private User findExistingUser(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    private Group permissionCheck(int groupId, int requesterId, User.Role requesterRole){
        Group group = findById(groupId);
        boolean isCreator = group.getCreatedBy() != null && group.getCreatedBy().getId().equals(requesterId);
        if(!isCreator && requesterRole!=User.Role.ADMIN){
            throw new ForbiddenException("Управлять группой может только её создатель или администратор");
        }
        return group;
    }
}