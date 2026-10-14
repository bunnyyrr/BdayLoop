package ru.bdayloop.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.bdayloop.model.User;
import ru.bdayloop.repository.i.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
class JpaUserRepositoryTest extends DaoTestBase {

    @Autowired
    UserRepository repository;
    @PersistenceContext
    EntityManager em;

    @Test
    void saveAndFindById_returnsSameUser() {
        User saved = repository.save(new User(null, "Катя", LocalDate.of(2000, 1, 1), "kate", "hash123", User.Role.USER));
        assertNotNull(saved.getId());
        em.flush();
        em.clear();

        Optional<User> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Катя", found.get().getName());
        assertEquals("kate", found.get().getUsername());
    }

    @Test
    void findById_whenUserDoesNotExist_returnsEmpty() {
        assertTrue(repository.findById(999).isEmpty());
    }

    @Test
    void findByUsername_findsExistingUser() {
        repository.save(new User(null, "Аня", LocalDate.of(1999, 5, 20), "anya", "hash456", User.Role.USER));
        em.flush();
        em.clear();

        Optional<User> found = repository.findByUsername("anya");

        assertTrue(found.isPresent());
        assertEquals("Аня", found.get().getName());
    }

    @Test
    void search_findsByNameUsernameOrId_ignoringCase() {
        User anya = repository.save(new User(null, "Аня Петрова", LocalDate.of(1999, 5, 20), "anya", "h", User.Role.USER));
        repository.save(new User(null, "Борис", LocalDate.of(1998, 1, 2), "boris", "h", User.Role.USER));
        em.flush();
        em.clear();

        assertEquals(1, repository.search("петр").size());
        assertEquals(1, repository.search("ANYA").size());
        assertEquals(anya.getId(), repository.search(String.valueOf(anya.getId())).get(0).getId());
        assertEquals(2, repository.search("").size());
    }

    @Test
    void delete_removesUser() {
        User saved = repository.save(new User(null, "Катя", LocalDate.of(2000, 1, 1), "kate", "h", User.Role.USER));
        em.flush();

        repository.delete(saved);
        em.flush();
        em.clear();

        assertTrue(repository.findById(saved.getId()).isEmpty());
    }

    @Test
    void changingManagedUser_isSavedWithoutSaveCall() {
        User saved = repository.save(new User(null, "Катя", LocalDate.of(2000, 1, 1), "kate", "h", User.Role.USER));
        em.flush();
        em.clear();

        User managed = repository.findById(saved.getId()).orElseThrow();
        managed.updateProfile("Екатерина", LocalDate.of(2000, 1, 2), "kate");
        em.flush();
        em.clear();

        User reloaded = repository.findById(saved.getId()).orElseThrow();
        assertEquals("Екатерина", reloaded.getName());
        assertEquals(LocalDate.of(2000, 1, 2), reloaded.getBirthday());
    }
}