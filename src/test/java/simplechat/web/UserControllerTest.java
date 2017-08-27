package simplechat.web;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import simplechat.data.UserRepository;
import simplechat.domain.ChatUser;
import simplechat.domain.Status;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class UserControllerTest {


    private ObjectMapper objectMapper;
    private UserRepository mockRepository;
    private ChatUser unsaved;
    private ChatUser saved;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        mockRepository = mock(UserRepository.class);
        unsaved = new ChatUser("shirru", "qwerty","Anna",
                "V", "89110362157");
        saved = new ChatUser(1L, "shirru", "qwerty","Anna",
                "V","89110362157", Status.online, null);

        ChatUser merged = new ChatUser(1L, "shirru", "qwerty","Anna", "V",
                "89110362157", Status.offline, null);

        when(mockRepository.save(unsaved)).thenReturn(saved);
        when(mockRepository.updateStatusById(1L, "offline")).thenReturn(merged);
        when(mockRepository.findById(1L)).thenReturn(saved);

        UserController controller = new UserController(mockRepository);

        UserController spyController = spy(controller);
        //when(spyController.checkUserCredentials(1L, null, mockRepository)).thenReturn(saved);
        doReturn(saved).when(spyController).checkUserCredentials(1L, null, mockRepository);
        mockMvc = standaloneSetup(spyController).build();
    }

    @Test
    public void shouldProcessRegistration() throws Exception {

        String jsonUnsaved = objectMapper.writeValueAsString(unsaved);

        mockMvc.perform(post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUnsaved)
        )
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(header().string("Location", "http://localhost/api/user/1"))
                .andExpect(jsonPath("$.id", is(1)));

        verify(mockRepository, atLeastOnce()).save(unsaved);
    }


    @Test
    public void shouldUpdateUserStatus() throws Exception {

        Map<String, String> status = new HashMap<String, String>();
        status.put("status", "offline");
        String jsonContent = objectMapper.writeValueAsString(status);

        Principal principal = () -> "shirru";

        mockMvc.perform(put("/api/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
                .principal(principal)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.status", is(Status.offline.toString())));

        verify(mockRepository, atLeastOnce()).updateStatusById(1L, "offline");
    }

    @Test
    public void shouldGetUserInfo() throws Exception {
        mockMvc.perform(get("/api/user/1")
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
        .andExpect(jsonPath("$.username", is(saved.getUsername())))
                .andExpect(jsonPath("$.username", is(saved.getUsername())))
                .andExpect(jsonPath("$.firstName", is(saved.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(saved.getLastName())))
                .andExpect(jsonPath("$.phone", is(saved.getPhone())))
                .andExpect(jsonPath("$.status", is(saved.getStatus().toString())))
                .andExpect(jsonPath("$.contacts", is(saved.getContacts())));
    }

}
