package simplechat.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import simplechat.config.DataConfig;
import simplechat.config.RootConfig;
import simplechat.config.SimpleChatAppConfig;
import simplechat.config.WebConfig;
import simplechat.data.UserRepository;
import simplechat.domain.ChatUser;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { SimpleChatAppConfig.class, WebConfig.class, DataConfig.class, RootConfig.class})
@Transactional
public class ExceptionHandlerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    UserRepository userRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        this.objectMapper = new ObjectMapper();
        this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    @WithMockUser
    public void shouldSendStatusNotFoundWhenGetNullUserInfo() throws Exception {

        mockMvc.perform(get("/api/user/0")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.code", is(4)))
                .andExpect(jsonPath("$.message", is("ChatUser [0] not found")));
    }

    @Test
    @WithMockUser(username = "shirru")
    public void shouldSendStatusConflictWhenUserAlreadyExist() throws Exception {
        ChatUser chatUser = new ChatUser("shirru", "Anna", "V",
                "89110362157");
        ChatUser sameUser = new ChatUser("shirru", "Anna", "V",
                "89110362157");
        userRepository.save(chatUser);

        String jsonUnsaved = objectMapper.writeValueAsString(sameUser);

        mockMvc.perform(post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUnsaved)
        )
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.code", is(3)))
                .andExpect(jsonPath("$.message", is("ChatUser already exist")));
    }

    @Test
    @WithMockUser
    public void shouldSendStatusNotFoundWhenUpdateNullUserInfo() throws Exception {
        Map<String, String> status = new HashMap<String, String>();
        status.put("status", "offline");
        String jsonContent = objectMapper.writeValueAsString(status);

        mockMvc.perform(put("/api/user/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
        )
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.code", is(4)))
                .andExpect(jsonPath("$.message", is("ChatUser [0] not found")));

    }

    @Test
    @WithMockUser("lynx")
    public void shouldSendStatusUnauthorizedWhenUpdateUserInfo() throws Exception {
        ChatUser chatUser = new ChatUser("shirru", "Anna", "V",
                "89110362157");
        ChatUser saved = userRepository.save(chatUser);

        Map<String, String> status = new HashMap<String, String>();
        status.put("status", "offline");
        String jsonContent = objectMapper.writeValueAsString(status);

        Principal principal = new Principal() {
            public String getName() {
                return "lynx";
            }
        };

        mockMvc.perform(put("/api/user/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
                .principal(principal)
        )
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.code", is(2)))
                .andExpect(jsonPath("$.message", is("Access denied for user [" +
                        principal.getName() + "]")));

    }

    @Test
    public void shouldSendStatusConflictWhenContactAlreadyExist() throws Exception{
        ChatUser chatUser = new ChatUser("shirru", "Anna", "V",
                "89110362157");
        ChatUser contact = new ChatUser("wqe", "A", "V",
                "77777777777");
        ChatUser saved = userRepository.save(chatUser);
        userRepository.save(contact);

        userRepository.addUserContact(saved, contact.getPhone());

        Principal principal = new Principal() {
            public String getName() {
                return "shirru";
            }
        };

        Map<String, String> phone = new HashMap<String, String>();
        phone.put("phone", contact.getPhone());
        String jsonContent = objectMapper.writeValueAsString(phone);

        mockMvc.perform(post("/api/user/" + saved.getId() + "/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
                .principal(principal)
        )
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.message",
                        is("Contact [" + contact.getPhone() + "] already exist")));
    }

}
