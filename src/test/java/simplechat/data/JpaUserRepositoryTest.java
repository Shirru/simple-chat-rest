package simplechat.data;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import simplechat.config.DataConfig;
import simplechat.config.RootConfig;
import simplechat.config.SimpleChatAppConfig;
import simplechat.config.WebConfig;
import simplechat.domain.ChatUser;
import simplechat.domain.Contact;
import simplechat.domain.Status;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { SimpleChatAppConfig.class, WebConfig.class, DataConfig.class, RootConfig.class})
@Transactional
public class JpaUserRepositoryTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    UserRepository userRepository;

    @Test
    public void shouldSaveUserInDatabase() throws Exception {
        ChatUser unsaved = new ChatUser("shirru", "qwerty", "Anna", "V",
                "89110362157");
        ChatUser saved = new ChatUser(1L, "shirru", "qwerty", "Anna", "V",
                "89110362157", Status.online, null);

        Assert.assertEquals(saved, userRepository.save(unsaved));
    }

    @Test
    public void shouldFindUserByIdInDatabase() throws Exception {
        ChatUser userAnna = new ChatUser("shirru", "qwerty", "Anna",
                "V", "89110362157");
        ChatUser userVlad = new ChatUser("lynx", "qwerty","Vlad",
                "A", "12345678911");

        ChatUser savedAnna = userRepository.save(userAnna);
        ChatUser savedVlad = userRepository.save(userVlad);

        Assert.assertEquals(userAnna, userRepository.findById(savedAnna.getId()));
        Assert.assertEquals(userVlad, userRepository.findById(savedVlad.getId()));

        Assert.assertNotEquals(userAnna, userRepository.findById(savedVlad.getId()));
    }

    @Test
    public void shouldSetContactToUser() throws Exception {
        ChatUser userAnna = new ChatUser("shirru", "qwerty","Anna",
                "V", "89110362157");
        ChatUser userVlad = new ChatUser("lynx", "qwerty","Vlad",
                "A", "12345678911");

        ChatUser savedAnna = userRepository.save(userAnna);
        ChatUser savedVlad = userRepository.save(userVlad);

        userRepository.addUserContact(savedAnna.getId(), savedVlad.getPhone());
        List<Contact> contacts = new ArrayList<Contact>();
        contacts.add(new Contact(savedVlad.getUsername(), savedVlad.getPhone()));
        userAnna.setContacts(contacts);

        Assert.assertEquals(userRepository.findById(savedAnna.getId()).getContacts(), userAnna.getContacts());
    }

    @Test
    public void shouldFindUserByPhoneInDatabase() {
        ChatUser userAnna = new ChatUser("shirru", "qwerty","Anna",
                "V", "89110362157");
        ChatUser userVlad = new ChatUser("lynx", "qwerty","Vlad",
                "A", "12345678911");

        ChatUser savedAnna = userRepository.save(userAnna);
        ChatUser savedVlad = userRepository.save(userVlad);

        Assert.assertEquals(userAnna, userRepository.findByPhone(savedAnna.getPhone()));
        Assert.assertEquals(userVlad, userRepository.findByPhone(savedVlad.getPhone()));

        Assert.assertNotEquals(userAnna, userRepository.findByPhone(savedVlad.getPhone()));
    }

    @Test
    public void shouldUpdateUserStatus() throws Exception {
        ChatUser unsaved = new ChatUser("shirru", "qwerty","Anna", "V",
                "89110362157");

        ChatUser saved = userRepository.save(unsaved);

        Assert.assertEquals(userRepository.updateStatusById(saved.getId(), "offline").getStatus(),
                Status.offline);
    }

    @Test
    public void shouldReturnNullWhenUserNotFoundInDatabase() throws Exception {
        Assert.assertNull(userRepository.findById(0L));
        Assert.assertNull(userRepository.findByPhone("0"));
        Assert.assertNull(userRepository.updateStatusById(0L, "offline"));
        Assert.assertNull(userRepository.addUserContact(0L, "0"));
    }
}
