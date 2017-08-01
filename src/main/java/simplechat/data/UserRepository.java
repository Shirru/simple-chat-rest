package simplechat.data;

import simplechat.domain.ChatUser;
import simplechat.domain.Contact;

import java.util.List;

public interface UserRepository {

    ChatUser save(ChatUser user);

    ChatUser findById(Long id);

    ChatUser findByPhone(String phone);

    Contact addUserContact(Long userId, String contactPhone);

    Contact addUserContact(ChatUser user, String contactPhone);

    Contact addUserContact(ChatUser user, ChatUser contact);

    List<Contact> findUserContacts(Long userId);

    List<Contact> deleteUserContact(ChatUser user, String contactPhone);

    ChatUser updateStatusById(Long id, String newStatus);
}
