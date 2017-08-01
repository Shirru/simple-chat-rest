package simplechat.data;

import simplechat.domain.Message;

import java.util.List;

public interface UserMessagesCustom {

    List<Message> findUserMessagesWithContact(Long userId, Long contactId);
    void deleteAllUserMessagesWithContact(Long userId, Long contactId);
}
