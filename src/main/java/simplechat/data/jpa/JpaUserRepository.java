package simplechat.data.jpa;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import simplechat.data.UserRepository;
import simplechat.domain.ChatUser;
import simplechat.domain.Contact;
import simplechat.domain.Status;
import simplechat.error.ContactAlreadyExistException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.List;

@Repository
@Transactional
public class JpaUserRepository implements UserRepository{

    @PersistenceContext
    private EntityManager entityManager;

    public ChatUser save(ChatUser user) {
        try {
            entityManager.persist(user);
        }
        catch (PersistenceException e) {
            return null;
        }
        return user;
    }

    public ChatUser findById(Long id) {
        return entityManager.find(ChatUser.class, id);
    }

    public ChatUser findByPhone(String phone) {
        String query = "FROM ChatUser u WHERE u.phone = :phone";

        List <ChatUser> resultList = entityManager.createQuery(query)
                .setParameter("phone", phone)
                .getResultList();
        if(!resultList.isEmpty())
            return resultList.get(0);
        else
            return null;
    }

    public Contact addUserContact(Long userId, String contactPhone) {
        ChatUser user = findById(userId);
        ChatUser contactUser = findByPhone(contactPhone);
        if (user == null || contactUser == null) return null;
        Contact contact = new  Contact(contactUser.getUsername(), contactUser.getPhone());
        user.addContact(contact);
        entityManager.merge(user);
        return contact;
    }

    public Contact addUserContact(ChatUser user, String contactPhone) {
        ChatUser contactUser = findByPhone(contactPhone);
        if (contactUser == null) return null;
        Contact contact = new  Contact(contactUser.getUsername(), contactUser.getPhone());
        if (user.addContact(contact) == null) return null;
        entityManager.merge(user);
        return contact;
    }

    public Contact addUserContact(ChatUser user, ChatUser contact) {
        Contact userContact = new Contact(contact.getUsername(), contact.getPhone());
        if (user.addContact(userContact) == null) return null;
        entityManager.merge(user);
        return userContact;
    }

    public List<Contact> deleteUserContact(ChatUser user, String contactPhone) {
        ChatUser contactUser = findByPhone(contactPhone);
        if (contactUser == null) return null;
        user.removeContact(new Contact(contactUser.getUsername(), contactPhone));
        return user.getContacts();
    }


    public List<Contact> findUserContacts(Long userId) {
        ChatUser user = findById(userId);

        if (user == null) return null;

        return user.getContacts();
    }

    public ChatUser updateStatusById(Long id, String newStatus) {
        Status status = Status.valueOf(newStatus.toLowerCase());
        ChatUser user = findById(id);
        if (user == null) return null;
        user.setStatus(status);
        return entityManager.merge(user);
    }
}
