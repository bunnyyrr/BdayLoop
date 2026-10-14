package ru.bdayloop.repository.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.bdayloop.model.User;
import ru.bdayloop.repository.i.UserRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaUserRepository implements UserRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public User save(User user){
        if(user.getId()== null){
            em.persist(user);
            return user;
        }
        return em.merge(user);
    }

    @Override
    public Optional<User> findById(Integer id){
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public Optional<User> findByUsername(String username){
        return em.createQuery("select u from User u where u.username=:username", User.class)
                .setParameter("username", username)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public List<User> search(String text){
        return em.createQuery("""
                        select u from User u
                        where lower(u.name) like :pattern
                           or lower(u.username) like :pattern
                           or cast(u.id as string) like :pattern
                        """, User.class)
                .setParameter("pattern", "%" + text.toLowerCase() + "%")
                .getResultList();
    }

    @Override
    public void delete(User user){
        em.remove(em.contains(user) ? user : em.merge(user));
    }
}
