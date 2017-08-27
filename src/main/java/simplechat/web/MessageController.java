package simplechat.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import simplechat.data.UserRepository;
import simplechat.data.jpa.MessageRepository;
import simplechat.domain.ChatUser;
import simplechat.domain.Message;
import simplechat.error.ContactNotFoundException;
import simplechat.web.messaging.MessageNotificationService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/user/{id}/contact/{contactPhone}/messages")
@PreAuthorize("isAuthenticated()")
public class MessageController extends UserCredentialsCheck{

    private MessageRepository messageRepository;
    private UserRepository userRepository;
    private MessageNotificationService notificationService;

    @Autowired
    public MessageController(MessageRepository messageRepository,
                             UserRepository userRepository,
                             MessageNotificationService notificationService) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Message> getUserMessagesWithContact(@PathVariable("id") Long id,
                                                    @PathVariable("contactPhone") String contactPhone,
                                                    Principal principal) {
        checkUserCredentials(id, principal, userRepository);
        ChatUser contact = userRepository.findByPhone(contactPhone);
        if (contact == null) { throw new ContactNotFoundException(contactPhone);}
        return messageRepository.findUserMessagesWithContact(id, contact.getId());
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public Message sendMessage(@PathVariable("id") Long id,
                               @PathVariable("contactPhone") String contactPhone,
                               @RequestBody Message message,
                               Principal principal) {
        checkUserCredentials(id, principal, userRepository);
        ChatUser contact = userRepository.findByPhone(contactPhone);
        if (contact == null) { throw new ContactNotFoundException(contactPhone);}

        message.setSender(id);
        message.setReceiver(contact.getId());
        Message savedMessage = messageRepository.save(message);
        notificationService.notifyReceiver(savedMessage);
        return savedMessage;
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAllUserMessagesWithContact(@PathVariable("id") Long id,
                                                                 @PathVariable("contactPhone") String contactPhone,
                                                                 Principal principal) {
        checkUserCredentials(id, principal, userRepository);
        ChatUser contact = userRepository.findByPhone(contactPhone);
        if (contact == null) { throw new ContactNotFoundException(contactPhone);}
        messageRepository.deleteAllUserMessagesWithContact(id, contact.getId());

        return ResponseEntity.noContent().build();
    }


}
