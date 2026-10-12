package ru.bdayloop.dao.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.bdayloop.dao.i.GroupDao;
import ru.bdayloop.model.Group;
import ru.bdayloop.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GroupDaoImplTest extends DaoTestBase {

    @Autowired
    GroupDao groupDao;

    @Test
    void createAndFindById_returnsSameGroup() throws SQLException {
        Group created = groupDao.create(new Group(0, "Тестовая группа", null));

        Optional<Group> found = groupDao.findById(created.getId());
        assertTrue(found.isPresent());
        assertEquals("Тестовая группа", found.get().getName());
    }

    @Test
    void findByMember_returnsGroupUserJoined() throws SQLException {
        User user = createUser("kate");
        Group group = groupDao.create(new Group(0, "Клуб", null));

        groupDao.joinGroup(user.getId(), group.getId());

        List<Group> groups = groupDao.findByMember(user.getId());
        assertEquals(1, groups.size());
        assertEquals("Клуб", groups.get(0).getName());
    }


    @Test
    void findBySubscriber_returnsOnlySubscribedGroups() throws SQLException {
        User anya = createUser("anya");
        Group club = groupDao.create(new Group(0, "Клуб", null));
        Group team = groupDao.create(new Group(0, "Сборная", null));
        groupDao.subscribeToGroup(anya.getId(), club.getId());
        groupDao.joinGroup(anya.getId(), team.getId());

        List<Group> groups = groupDao.findBySubscriber(anya.getId());

        assertEquals(1, groups.size());
        assertEquals("Клуб", groups.get(0).getName());
    }
}