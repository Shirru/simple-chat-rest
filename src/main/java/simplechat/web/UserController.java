package simplechat.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import simplechat.data.UserRepository;
import simplechat.domain.ChatUser;
import simplechat.error.ChatUserAlreadyExistException;

import java.net.URI;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController extends UserCredentialsCheck{

    private UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<Map<String, Long>> registerUser(@RequestBody ChatUser user,
                                                          UriComponentsBuilder ucb) {
        Map<String, Long> userId = new HashMap<String, Long>();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        ChatUser savedUser = userRepository.save(user);

        if (savedUser == null) { throw new ChatUserAlreadyExistException();}

        userId.put("id", savedUser.getId());

        HttpHeaders headers = new HttpHeaders();
        URI locationUri = ucb.path("/api/user/")
                .path(String.valueOf(savedUser.getId()))
                .build()
                .toUri();
        headers.setLocation(locationUri);

        return new ResponseEntity<Map<String, Long>>(userId, headers, HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @PostAuthorize("returnObject.username == principal.username")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ChatUser getUserInfo(@PathVariable Long id, Principal principal) {
        return checkUserCredentials(id, principal, userRepository);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = "application/json")
    public ChatUser updateUserStatus(@PathVariable Long id, @RequestBody Map<String, String> status,
                                     Principal principal) {
        checkUserCredentials(id, principal, userRepository);
        return userRepository.updateStatusById(id, status.get("status"));
    }

}
