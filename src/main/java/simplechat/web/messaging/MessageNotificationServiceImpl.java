package simplechat.web.messaging;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import simplechat.data.UserRepository;
import simplechat.domain.Message;

@Service
public class MessageNotificationServiceImpl implements MessageNotificationService{

    private SimpMessagingTemplate messagingTemplate;
    private UserRepository userRepository;

    @Autowired
    public MessageNotificationServiceImpl(SimpMessagingTemplate messagingTemplate,
                                          UserRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
    }

    @Override
    public void notifyReceiver(Message message) {
        String receiverUsername = userRepository.findById(message.getReceiver()).getUsername();
        messagingTemplate.convertAndSendToUser(receiverUsername, "/queue/message/notifications",
                message);
    }
}
