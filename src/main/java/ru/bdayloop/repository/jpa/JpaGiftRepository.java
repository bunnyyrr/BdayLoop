package ru.bdayloop.repository.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.bdayloop.model.Gift;
import ru.bdayloop.repository.i.GiftRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaGiftRepository implements GiftRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Gift save(Gift gift){
        if(gift.getId()== null){
            em.persist(gift);
            return gift;
        }
        return em.merge(gift);
    }

    @Override
    public Optional<Gift> findById(Integer id){
        return Optional.ofNullable(em.find(Gift.class, id));
    }

    @Override
    public List<Gift> findByOwnerId(Integer ownerId){
        return em.createQuery("select g from Gift g where g.owner.id = :ownerId order by g.id", Gift.class)
                .setParameter("ownerId", ownerId)
                .getResultList();
    }

    @Override
    public void delete(Gift gift){
        em.remove(em.contains(gift) ? gift : em.merge(gift));
    }
}
