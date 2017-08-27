package simplechat.data.jpa;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import simplechat.data.UserMessagesCustom;
import simplechat.domain.ChatUser;
import simplechat.domain.Message;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional
public class MessageRepositoryImpl implements UserMessagesCustom {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Message> findUserMessagesWithContact(Long userId, Long contactId) {
        String query = "FROM Message m WHERE (m.sender = :userId OR m.receiver = :userId)" +
                " AND (m.sender = :contactId  OR m.receiver = :contactId )";

        List<Message> messages = entityManager.createQuery(query)
                .setParameter("userId", userId)
                .setParameter("contactId", contactId)
                .getResultList();

        return messages;
    }

    public void deleteAllUserMessagesWithContact(Long userId, Long contactId) {
        List<Message> messages = findUserMessagesWithContact(userId, contactId);
        if (messages != null) {
            String query = "DELETE FROM Message m WHERE (m.sender = :userId OR m.receiver = :userId)" +
                    " AND (m.sender = :contactId  OR m.receiver = :contactId )";

            entityManager.createQuery(query)
                    .setParameter("userId", userId)
                    .setParameter("contactId", contactId)
                    .executeUpdate();
        }
    }
}
