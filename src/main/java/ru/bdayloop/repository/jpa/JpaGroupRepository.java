package ru.bdayloop.repository.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.bdayloop.model.Group;
import ru.bdayloop.repository.i.GroupRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaGroupRepository implements GroupRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Group save(Group group){
        if(group.getId() == null){
            em.persist(group);
            return group;
        }
        return em.merge(group);
    }

    @Override
    public Optional<Group> findById(Integer id){
        return Optional.ofNullable(em.find(Group.class, id));
    }

    @Override
    public boolean existsByName(String name){
        Long count = em.createQuery("select count(g) from Group g where g.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count>0;
    }

    @Override
    public List<Group> findAll(){
        return em.createQuery("select g from Group g order by g.id", Group.class)
                .getResultList();
    }

    @Override
    public List<Group> findByNameContaining(String text){
        return em.createQuery("select g from Group g where lower(g.name) like :pattern order by g.id", Group.class)
                .setParameter("pattern", "%" + text.toLowerCase() + "%")
                .getResultList();
    }

    @Override
    public List<Group> findByMember(Integer userId){
        return em.createNativeQuery("""
                        select g.* from groups g
                        join group_members gm on gm.group_id = g.id
                        where gm.user_id = :userId
                        order by g.id
                        """, Group.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public List<Group> findBySubscriber(Integer userId) {
        return em.createNativeQuery("""
                        select g.* from groups g
                        join group_subscriptions gs on gs.group_id = g.id
                        where gs.subscriber_id = :userId
                        order by g.id
                        """, Group.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public void delete(Group group){
        em.remove(em.contains(group)? group : em.merge(group));
    }
}
