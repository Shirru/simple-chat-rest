package simplechat.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import simplechat.config.DataConfig;
import simplechat.config.RootConfig;
import simplechat.config.SimpleChatAppConfig;
import simplechat.config.WebConfig;
import simplechat.data.jpa.MessageRepository;
import simplechat.domain.ChatUser;
import simplechat.domain.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { SimpleChatAppConfig.class, WebConfig.class, DataConfig.class, RootConfig.class})
@Transactional
@TestPropertySource("classpath:/simplechat/test.properties")
public class JpaMessageRepositoryTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserRepository userRepository;

    private List<ChatUser> users;

    @Before
    public void setUp() {
        users = new ArrayList<ChatUser>();
        users.add(new ChatUser("user", "qwerty","Anna",
                "V", "89110362156"));
        users.add(new ChatUser("user2", "qwerty","Vlad",
                "A", "12345678912"));
        users.add(new ChatUser("jh", "qwerty","John",
                "A", "11111111111"));
        users.add(new ChatUser("misterUser", "qwerty","Jack",
                "Smith", "222222222"));

        for (ChatUser user : users) {
            userRepository.save(user);
        }
    }

    @Test
    public void shouldSaveMessageInDatabase() throws Exception {
        Date date = new Date();

        Message unsaved = new Message(users.get(0).getId(), users.get(1).getId(), "Text", date);
        Message saved = new Message(1L, users.get(0).getId(), users.get(1).getId(), "Text", date);

        Assert.assertEquals(saved, messageRepository.save(unsaved));
    }

    @Test
    public void shouldFindUserMessagesWithContactFromDb() throws Exception {
        List<Message> messages = new ArrayList<Message>();
        messages.add(new Message(users.get(0).getId(), users.get(1).getId(), "Hello Vlad", new Date()));
        messages.add(new Message(users.get(1).getId(), users.get(0).getId(), "Hello Ann", new Date()));
        messages.add(new Message(users.get(0).getId(), users.get(2).getId(), "text", new Date()));
        messages.add(new Message(users.get(1).getId(), users.get(2).getId(), "qqqq", new Date()));

        for (Message message : messages) {
            messageRepository.save(message);
        }

        Assert.assertEquals(messages.subList(0, 2),
                messageRepository.findUserMessagesWithContact(users.get(0).getId(), users.get(1).getId()));

        Assert.assertEquals(0,
                messageRepository.findUserMessagesWithContact(0L, 0L).size());
    }

    @Test
    public void shouldDeleteAllUserMessagesWithContactFromDb() throws Exception {
        List<Message> messages = new ArrayList<Message>();
        messages.add(new Message(users.get(0).getId(), users.get(1).getId(), "Hello Vlad", new Date()));
        messages.add(new Message(users.get(1).getId(), users.get(0).getId(), "Hello Ann", new Date()));
        messages.add(new Message(users.get(0).getId(), users.get(2).getId(), "text", new Date()));
        messages.add(new Message(users.get(1).getId(), users.get(2).getId(), "qqqq", new Date()));

        for (Message message : messages) {
            messageRepository.save(message);
        }

        messageRepository.deleteAllUserMessagesWithContact(users.get(0).getId(), users.get(1).getId());

        Assert.assertEquals(0,
                messageRepository.findUserMessagesWithContact(users.get(0).getId(), users.get(1).getId()).size());
    }
}
