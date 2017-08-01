package simplechat.web;

import org.springframework.security.authentication.BadCredentialsException;
import simplechat.data.UserRepository;
import simplechat.domain.ChatUser;
import simplechat.error.ChatUserNotFoundException;

import java.security.Principal;

public class UserCredentialsCheck {

    public ChatUser checkUserCredentials(Long id, Principal principal, UserRepository userRepository) {
        ChatUser chatUser = userRepository.findById(id);
        if (chatUser == null) {throw new ChatUserNotFoundException(id);}
        if (chatUser.getUsername() != principal.getName()) { throw new BadCredentialsException("Access denied");}
        return chatUser;
    }
}
