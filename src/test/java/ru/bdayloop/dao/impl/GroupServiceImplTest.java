package ru.bdayloop.dao.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.bdayloop.exception.BadRequestException;
import ru.bdayloop.exception.ConflictException;
import ru.bdayloop.exception.ForbiddenException;
import ru.bdayloop.exception.NotFoundException;
import ru.bdayloop.model.Group;
import ru.bdayloop.model.User;
import ru.bdayloop.service.i.GroupService;
import ru.bdayloop.service.i.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroupServiceImplTest extends DaoTestBase {

    @Autowired
    GroupService groupService;
    @Autowired
    UserService userService;

    @Test
    void create_setsCreator() {
        User kate = createUser("kate");

        Group group = groupService.create("Клуб", kate.getId());

        assertEquals("Клуб", group.getName());
        assertEquals(kate.getId(), group.getCreatedBy().getId());
    }

    @Test
    void create_withBlankName_isRejected() {
        User kate = createUser("kate");

        assertThrows(BadRequestException.class, () -> groupService.create("  ", kate.getId()));
    }

    @Test
    void create_withTakenName_isConflict() {
        User kate = createUser("kate");
        groupService.create("Клуб", kate.getId());

        assertThrows(ConflictException.class, () -> groupService.create("Клуб", kate.getId()));
    }

    @Test
    void create_forUnknownUser_isNotFound() {
        assertThrows(NotFoundException.class, () -> groupService.create("Клуб", 999));
    }

    @Test
    void update_byCreator_renamesGroup() {
        User kate = createUser("kate");
        Group group = groupService.create("Клуб", kate.getId());

        groupService.update(group.getId(), "Новый клуб", kate.getId(), User.Role.USER);

        assertEquals("Новый клуб", groupService.findById(group.getId()).getName());
    }

    @Test
    void update_byAnotherUser_isForbidden() {
        User kate = createUser("kate");
        User anya = createUser("anya");
        Group group = groupService.create("Клуб", kate.getId());

        assertThrows(ForbiddenException.class,
                () -> groupService.update(group.getId(), "Захват", anya.getId(), User.Role.USER));
        assertEquals("Клуб", groupService.findById(group.getId()).getName());
    }

    @Test
    void update_byAdmin_isAllowed() {
        User kate = createUser("kate");
        User admin = createUser("admin");
        Group group = groupService.create("Клуб", kate.getId());

        groupService.update(group.getId(), "Переименовано админом", admin.getId(), User.Role.ADMIN);

        assertEquals("Переименовано админом", groupService.findById(group.getId()).getName());
    }

    @Test
    void update_toTakenName_isConflict() {
        User kate = createUser("kate");
        groupService.create("Занято", kate.getId());
        Group group = groupService.create("Клуб", kate.getId());

        assertThrows(ConflictException.class,
                () -> groupService.update(group.getId(), "Занято", kate.getId(), User.Role.USER));
    }

    @Test
    void delete_byAnotherUser_isForbidden() {
        User kate = createUser("kate");
        User anya = createUser("anya");
        Group group = groupService.create("Клуб", kate.getId());

        assertThrows(ForbiddenException.class,
                () -> groupService.delete(group.getId(), anya.getId(), User.Role.USER));
        assertEquals(1, groupService.findAll().size());
    }

    @Test
    void delete_byCreator_removesGroupWithItsMembers()  {
        User kate = createUser("kate");
        Group group = groupService.create("Клуб", kate.getId());
        groupService.joinGroup(kate.getId(), group.getId());

        groupService.delete(group.getId(), kate.getId(), User.Role.USER);

        assertTrue(groupService.findAll().isEmpty());
        assertTrue(groupService.findByMember(kate.getId()).isEmpty());
    }

    @Test
    void deletingCreator_keepsGroupWithoutCreator()  {
        User kate = createUser("kate");
        User anya = createUser("anya");
        Group group = groupService.create("Клуб", kate.getId());

        userService.delete(kate.getId(), kate.getId());

        Group left = groupService.findById(group.getId());
        assertNull(left.getCreatedBy());
        assertThrows(ForbiddenException.class,
                () -> groupService.delete(group.getId(), anya.getId(), User.Role.USER));
    }

    @Test
    void joinAndSubscribe_areVisibleThroughFindBy()  {
        User kate = createUser("kate");
        Group club = groupService.create("Клуб", kate.getId());
        Group team = groupService.create("Сборная", kate.getId());

        groupService.joinGroup(kate.getId(), club.getId());
        groupService.subscribeToGroup(kate.getId(), team.getId());

        List<Group> member = groupService.findByMember(kate.getId());
        List<Group> subscribed = groupService.findBySubscriber(kate.getId());
        assertEquals(1, member.size());
        assertEquals("Клуб", member.get(0).getName());
        assertEquals(1, subscribed.size());
        assertEquals("Сборная", subscribed.get(0).getName());
        assertEquals(List.of(kate.getId()), groupService.groupMembers(club.getId()));
    }

    @Test
    void joinGroup_twice_isConflict() {
        User kate = createUser("kate");
        Group club = groupService.create("Клуб", kate.getId());
        groupService.joinGroup(kate.getId(), club.getId());

        assertThrows(ConflictException.class, () -> groupService.joinGroup(kate.getId(), club.getId()));
    }

    @Test
    void joinGroup_unknownGroupOrUser_isNotFound() {
        User kate = createUser("kate");
        Group club = groupService.create("Клуб", kate.getId());

        assertThrows(NotFoundException.class, () -> groupService.joinGroup(kate.getId(), 999));
        assertThrows(NotFoundException.class, () -> groupService.joinGroup(999, club.getId()));
    }

    @Test
    void leaveGroup_removesMembership_andIsSilentWhenNotMember() {
        User kate = createUser("kate");
        Group club = groupService.create("Клуб", kate.getId());
        groupService.joinGroup(kate.getId(), club.getId());

        groupService.leaveGroup(club.getId(), kate.getId());
        groupService.leaveGroup(club.getId(), kate.getId());

        assertTrue(groupService.groupMembers(club.getId()).isEmpty());
    }

    @Test
    void subscribeToGroup_twice_isConflict_andUnsubscribeWorks() {
        User kate = createUser("kate");
        Group club = groupService.create("Клуб", kate.getId());
        groupService.subscribeToGroup(kate.getId(), club.getId());

        assertThrows(ConflictException.class, () -> groupService.subscribeToGroup(kate.getId(), club.getId()));

        groupService.unsubscribeFromGroup(kate.getId(), club.getId());
        assertTrue(groupService.findBySubscriber(kate.getId()).isEmpty());
    }

    @Test
    void deletingGroup_removesMembershipsAndSubscriptions() {
        User kate = createUser("kate");
        User anya = createUser("anya");
        Group club = groupService.create("Клуб", kate.getId());
        groupService.joinGroup(anya.getId(), club.getId());
        groupService.subscribeToGroup(anya.getId(), club.getId());

        groupService.delete(club.getId(), kate.getId(), User.Role.USER);

        assertTrue(groupService.findByMember(anya.getId()).isEmpty());
        assertTrue(groupService.findBySubscriber(anya.getId()).isEmpty());
    }

    @Test
    void deletingUser_removesHisMembershipsAndSubscriptions() {
        User kate = createUser("kate");
        User anya = createUser("anya");
        Group club = groupService.create("Клуб", kate.getId());
        groupService.joinGroup(anya.getId(), club.getId());
        groupService.subscribeToGroup(anya.getId(), club.getId());

        userService.delete(anya.getId(), anya.getId());

        assertTrue(groupService.groupMembers(club.getId()).isEmpty());
    }
}
