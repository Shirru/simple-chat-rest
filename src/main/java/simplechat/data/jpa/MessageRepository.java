package simplechat.data.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import simplechat.data.UserMessagesCustom;
import simplechat.domain.Message;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long>, UserMessagesCustom {

    List<Message> findMessagesByReceiverOrderByMdate(Long receiverId);
    List<Message> findMessagesBySenderOrderByMdate(Long senderId);
    List<Message> findMessagesByReceiverOrSenderOrderByMdate(Long receiverId, Long senderId);
}
