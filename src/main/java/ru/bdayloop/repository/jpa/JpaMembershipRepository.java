package ru.bdayloop.repository.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.bdayloop.model.GroupMembership;
import ru.bdayloop.model.id.GroupMembershipId;
import ru.bdayloop.repository.i.MembershipRepository;

import java.util.List;

@Repository
public class JpaMembershipRepository implements MembershipRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public GroupMembership save(GroupMembership membership){
        em.persist(membership);
        return membership;
    }

    @Override
    public boolean exists(Integer groupId, Integer userId){
        return em.find(GroupMembership.class, new GroupMembershipId(groupId, userId))!= null;
    }

    @Override
    public void delete(Integer groupId, Integer userId) {
        GroupMembership membership = em.find(GroupMembership.class, new GroupMembershipId(groupId, userId));
        if (membership != null) {
            em.remove(membership);
        }
    }

    @Override
    public List<Integer> findMemberIds(Integer groupId) {
        return em.createQuery(
                        "select m.id.userId from GroupMembership m where m.id.groupId = :groupId order by m.joinedAt, m.id.userId",
                        Integer.class)
                .setParameter("groupId", groupId)
                .getResultList();
    }
}
