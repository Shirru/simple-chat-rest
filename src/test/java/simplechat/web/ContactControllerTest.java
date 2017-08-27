package simplechat.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import simplechat.data.UserRepository;
import simplechat.domain.ChatUser;
import simplechat.domain.Contact;
import simplechat.domain.Status;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class ContactControllerTest {

    private ObjectMapper objectMapper;
    private List<ChatUser> users;
    private MockMvc mockMvc;
    private Principal principal;
    private UserRepository mockRepository;

    @Before
    public void setUp() {

        principal = () -> "shirru";

        objectMapper = new ObjectMapper();
        mockRepository = mock(UserRepository.class);

        users = new ArrayList<ChatUser>();
        users.add(new ChatUser(1L, "shirru", "qwerty","Anna", "V",
                "89110362157", Status.online, new ArrayList<Contact>()));
        users.add(new ChatUser(2L, "user", "qwerty","Jon", "Snow",
                "11111111111", Status.online, new ArrayList<Contact>()));
        users.add(new ChatUser(3L, "misterUser", "qwerty","Jack", "Smith",
                "2222222222", Status.online, new ArrayList<Contact>()));

        when(mockRepository.findById(1L)).thenReturn(users.get(0));
        when(mockRepository.findById(2L)).thenReturn(users.get(1));
        when(mockRepository.findById(3L)).thenReturn(users.get(2));
        when(mockRepository.findByPhone(users.get(1).getPhone())).thenReturn(users.get(1));

        when(mockRepository.addUserContact(users.get(0), users.get(1).getPhone()))
                .thenReturn(new Contact(users.get(1).getUsername(), users.get(1).getPhone()));

        when(mockRepository.addUserContact(users.get(0), users.get(1)))
                .thenReturn(new Contact(users.get(1).getUsername(), users.get(1).getPhone()));

        ContactController controller = new ContactController(mockRepository);
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void shouldAddNewContact() throws Exception {
        Map<String, String> phone = new HashMap<String, String>();
        phone.put("phone", users.get(1).getPhone());

        String jsonUnsaved = objectMapper.writeValueAsString(phone);

        mockMvc.perform(post("/api/user/1/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUnsaved)
                .principal(principal)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.username", is(users.get(1).getUsername())))
                .andExpect(jsonPath("$.phone", is(users.get(1).getPhone())));

    }

    @Test
    public void shouldFindUserContacts() throws Exception {
        users.get(0).addContact(new Contact(users.get(1).getUsername(), users.get(1).getPhone()));
        users.get(0).addContact(new Contact(users.get(2).getUsername(), users.get(2).getPhone()));

        mockMvc.perform(get("/api/user/1/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(principal)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.[0].username", is(users.get(1).getUsername())))
                .andExpect(jsonPath("$.[0].phone", is(users.get(1).getPhone())))
                .andExpect(jsonPath("$.[1].username", is(users.get(2).getUsername())))
                .andExpect(jsonPath("$.[1].phone", is(users.get(2).getPhone())));
    }

    @Test
    public void shouldDeleteUserContact() throws Exception {
        when(mockRepository.deleteUserContact(users.get(0), "123456"))
                .thenReturn(users.get(0));

        mockMvc.perform(delete("/api/user/1/contacts/" + "123456")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(principal)
        )
                .andExpect(status().isNoContent());
    }
}
