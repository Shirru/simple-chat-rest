package simplechat.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import simplechat.data.UserRepository;
import simplechat.domain.ChatUser;
import simplechat.domain.Contact;
import simplechat.error.ContactAlreadyExistException;
import simplechat.error.ContactNotFoundException;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/{id}/contacts")
public class ContactController extends UserCredentialsCheck{

    private UserRepository userRepository;

    @Autowired
    public ContactController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public Contact addNewContact(@PathVariable Long id, @RequestBody Map<String, String> phone,
                                 Principal principal){
        ChatUser chatUser = checkUserCredentials(id, principal, userRepository);
        ChatUser contactUser = userRepository.findByPhone(phone.get("phone"));
        if (contactUser == null) { throw new ContactNotFoundException(phone.get("phone")); }

        Contact contact = userRepository.addUserContact(chatUser, contactUser);
        if (contact == null) { throw new ContactAlreadyExistException(phone.get("phone"));}

        return contact;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Contact> getUserContacts(@PathVariable Long id, Principal principal) {
        ChatUser chatUser = checkUserCredentials(id, principal, userRepository);
        return chatUser.getContacts();
    }

    @RequestMapping(value = "/{contactPhone}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUserContact(@PathVariable("id") Long id,
                                            @PathVariable("contactPhone") String contactPhone,
                                            Principal principal) {
        ChatUser chatUser = checkUserCredentials(id, principal, userRepository);
        if (userRepository.deleteUserContact(chatUser, contactPhone) == null)
        { throw new ContactNotFoundException(contactPhone);}
        return ResponseEntity.noContent().build();
    }

}
