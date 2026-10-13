package ru.bdayloop.repository.i;

import ru.bdayloop.model.Message;

import java.util.List;

public interface MessageRepository {
    Message save(Message message);
    List<Message> findBySubjectId(Integer subjectId);
}
