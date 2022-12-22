package nl.tudelft.sem.template.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.services.GatewayService;
import nl.tudelft.sem.template.shared.authentication.JwtAuthenticationEntryPoint;
import nl.tudelft.sem.template.shared.authentication.JwtRequestFilter;
import nl.tudelft.sem.template.shared.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GatewayUserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserGatewayTest {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GatewayService gatewayService;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    private JwtRequestFilter jwtRequestFilter;

    @Test
    void getAllUsersCorrectlyTest() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(new User("testNetId", "name", "email"));
        users.add(new User("testNetId2", "name2", "email2"));
        when(gatewayService.getAllUsers()).thenReturn(users);
        mockMvc.perform(get("/api/user/all").contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("testNetId")))
                .andExpect(content().string(containsString("name")))
                .andExpect(content().string(containsString("email")))
                .andExpect(content().string(containsString("testNetId2")))
                .andExpect(content().string(containsString("name2")))
                .andExpect(content().string(containsString("email2")));
    }

    @Test
    void getAllUsersEmptyTest() throws Exception {
        List<User> users = new ArrayList<>();
        when(gatewayService.getAllUsers()).thenReturn(users);
        mockMvc.perform(get("/api/user/all").contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[]")));
    }

    @Test
    void getAllNotificationsCorrectlyTest() throws Exception {
        // write a test for retrieving all notifications using /api/getNotifications/{userId}
        List<String> notifications = new ArrayList<>();
        notifications.add("testNotification");
        notifications.add("testNotification2");
        when(gatewayService.getAllNotifications(2137L)).thenReturn(notifications);
        mockMvc.perform(get("/api/user/getNotifications/2137").contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("testNotification")))
                .andExpect(content().string(containsString("testNotification2")));
    }

    @Test
    void getAllNotificationsEmptyTest() throws Exception {
        // write a test for retrieving all notifications using /api/getNotifications/{userId}
        List<String> notifications = new ArrayList<>();
        when(gatewayService.getAllNotifications(2137L)).thenReturn(notifications);
        mockMvc.perform(get("/api/user/getNotifications/2137").contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[]")));
    }

    @Test
    void getAllNotificationsInvalidIdTest() throws Exception {
        // write a test for retrieving all notifications using /api/getNotifications/{userId}
        List<String> notifications = new ArrayList<>();
        when(gatewayService.getAllNotifications(2137L)).thenReturn(notifications);
        mockMvc.perform(get("/api/user/getNotifications/invalidId").contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUserCorrectlyTest() throws Exception {
        when(gatewayService.deleteUser(2137L)).thenReturn(true);
        mockMvc.perform(delete("/api/user/deleteUser/2137").contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(containsString("true")))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUserInvalidIdTest() throws Exception {
        when(gatewayService.deleteUser(2137L)).thenReturn(false);
        mockMvc.perform(delete("/api/user/deleteUser/invalidId"))
                .andExpect(status().isBadRequest());
    }
}
