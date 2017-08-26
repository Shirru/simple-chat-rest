package simplechat.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import simplechat.data.UserRepository;
import simplechat.data.jpa.MessageRepository;
import simplechat.domain.ChatUser;
import simplechat.domain.Contact;
import simplechat.domain.Message;
import simplechat.domain.Status;
import simplechat.web.messaging.MessageNotificationService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class MessageControllerTest {

    private ObjectMapper objectMapper;
    private List<ChatUser> users;
    private MockMvc mockMvc;
    private Principal principal;
    private MessageRepository mockMessageRepository;
    private UserRepository mockUserRepository;

    @Before
    public void setUp() {

        principal = new Principal() {
            public String getName() {
                return "shirru";
            }
        };

        objectMapper = new ObjectMapper();
        mockUserRepository = mock(UserRepository.class);
        mockMessageRepository = mock(MessageRepository.class);
        MessageNotificationService mockNotificationService = mock(MessageNotificationService.class);

        users = new ArrayList<ChatUser>();
        users.add(new ChatUser(1L, "shirru", "qwerty","Anna", "V",
                "89110362157", Status.online, new ArrayList<Contact>()));
        users.add(new ChatUser(2L, "user", "qwerty","Jon", "Snow",
                "11111111111", Status.online,  new ArrayList<Contact>()));
        users.add(new ChatUser(3L, "misterUser", "qwerty","Jack", "Smith",
                "2222222222", Status.online, new ArrayList<Contact>()));

        users.get(0).addContact(new Contact(users.get(1).getUsername(), users.get(1).getPhone()));
        users.get(0).addContact(new Contact(users.get(2).getUsername(), users.get(2).getPhone()));

        when(mockUserRepository.findById(1L)).thenReturn(users.get(0));
        when(mockUserRepository.findById(2L)).thenReturn(users.get(1));
        when(mockUserRepository.findById(3L)).thenReturn(users.get(2));
        when(mockUserRepository.findByPhone(users.get(1).getPhone())).thenReturn(users.get(1));

        MessageController controller = new MessageController(mockMessageRepository, mockUserRepository,
                mockNotificationService);
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void shouldFindUserMessagesWithContact() throws Exception {
        List <Message> messages = new ArrayList<Message>();
        messages.add(new Message(1L, 1L, 2L, "Hello", new Date()));
        messages.add(new Message(2L, 2L, 1L, "Hi", new Date()));

        when(mockMessageRepository.findUserMessagesWithContact(1L,2L))
                .thenReturn(messages);

        mockMvc.perform(get("/api/user/1/contact/" + users.get(1).getPhone() + "/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(principal)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].sender", is(1)))
                .andExpect(jsonPath("$.[0].receiver", is(2)))
                .andExpect(jsonPath("$.[0].mtext", is("Hello")))
                .andExpect(jsonPath("$.[1].id", is(2)))
                .andExpect(jsonPath("$.[1].sender", is(2)))
                .andExpect(jsonPath("$.[1].receiver", is(1)))
                .andExpect(jsonPath("$.[1].mtext", is("Hi")));

    }

    @Test
    public void shouldSaveMessage() throws Exception {

        Date date = new Date();
        Long fasttime = date.getTime();

        Message unsaved = new Message(1L, 2L, "hello", date);
        Message saved = new Message(1L, 1L, 2L, "hello", date);

        when(mockMessageRepository.save(unsaved)).thenReturn(saved);
        String jsonUnsaved = objectMapper.writeValueAsString(unsaved);

        mockMvc.perform(post("/api/user/1/contact/" + users.get(1).getPhone() + "/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUnsaved)
                .principal(principal)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.sender", is(1)))
                .andExpect(jsonPath("$.receiver", is(2)))
                .andExpect(jsonPath("$.mtext", is("hello")))
                .andExpect(jsonPath("$.mdate", is(fasttime)));

    }

    @Test
    public void shouldDeleteAllUserMessagesWithContact() throws Exception {

        mockMvc.perform(delete("/api/user/1/contact/" + users.get(1).getPhone() + "/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(principal)
        )
                .andExpect(status().isNoContent());
    }
}
