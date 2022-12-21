package nl.tudelft.sem.template.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.nio.charset.StandardCharsets;
import nl.tudelft.sem.template.services.GatewayService;
import nl.tudelft.sem.template.shared.authentication.JwtAuthenticationEntryPoint;
import nl.tudelft.sem.template.shared.authentication.JwtRequestFilter;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.models.AuthenticationRequestModel;
import nl.tudelft.sem.template.shared.models.AuthenticationResponseModel;
import nl.tudelft.sem.template.shared.models.RegistrationRequestModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(GatewayAuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthenticationGatewayTest {

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
    public void testingCorrectRegisterRouting() throws Exception {
        RegistrationRequestModel request = new RegistrationRequestModel();
        request.setName("test");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(request);
        when(gatewayService.registerUser(request)).thenReturn(new User("testNetId", "name", "email"));
        mockMvc.perform(post("/api/auth/register").contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isOk()).andExpect(content().string(containsString("testNetId")));
    }

    @Test
    public void testingIncorrectRegisterRouting() throws Exception {
        RegistrationRequestModel request = new RegistrationRequestModel();
        request.setName("test");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(request);
        when(gatewayService.registerUser(request)).thenThrow(new IllegalArgumentException());
        mockMvc.perform(post("/api/auth/register").contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testingIncorrectRegisterRoutingWithRestTemplateError() throws Exception {
        RegistrationRequestModel request = new RegistrationRequestModel();
        request.setName("test");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(request);
        when(gatewayService.registerUser(request)).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));
        mockMvc.perform(post("/api/auth/register").contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testingCorrectLoginRouting() throws Exception {
        AuthenticationRequestModel request = new AuthenticationRequestModel();
        request.setNetId("testNetID");
        request.setPassword("testPassword");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(request);
        when(gatewayService.login(request)).thenReturn(new AuthenticationResponseModel("testToken"));
        mockMvc.perform(post("/api/auth/login").contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isOk()).andExpect(content().string(containsString("testToken")));
    }

    @Test
    public void testingIncorrectLoginRouting() throws Exception {
        AuthenticationRequestModel request = new AuthenticationRequestModel();
        request.setNetId("testNetID");
        request.setPassword("testPassword");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(request);
        when(gatewayService.login(request)).thenThrow(new IllegalArgumentException());
        mockMvc.perform(post("/api/auth/login").contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testingIncorrectLoginRoutingWithRestTemplateError() throws Exception {
        AuthenticationRequestModel request = new AuthenticationRequestModel();
        request.setNetId("testNetID");
        request.setPassword("testPassword");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(request);
        when(gatewayService.login(request)).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));
        mockMvc.perform(post("/api/auth/login").contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

}
