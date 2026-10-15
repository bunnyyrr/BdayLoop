package ru.bdayloop.repository.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.bdayloop.model.Message;
import ru.bdayloop.repository.i.MessageRepository;

import java.util.List;

@Repository
public class JpaMessageRepository implements MessageRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Message save(Message message){
        if(message.getId()==null){
            em.persist(message);
            return message;
        }
        return em.merge(message);
    }

    @Override
    public List<Message> findBySubjectId(Integer subjectId){
        return em.createQuery("select m from Message m left join fetch m.sender where m.subject.id=:subjectId order by m.createdAt, m.id", Message.class)
                .setParameter("subjectId", subjectId)
                .getResultList();
    }
}
