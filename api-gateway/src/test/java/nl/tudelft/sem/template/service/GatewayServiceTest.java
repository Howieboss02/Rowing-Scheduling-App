package nl.tudelft.sem.template.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.tudelft.sem.template.components.RestTemplateResponseErrorHandler;
import nl.tudelft.sem.template.services.GatewayService;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.MicroservicePorts;
import nl.tudelft.sem.template.shared.models.AuthenticationRequestModel;
import nl.tudelft.sem.template.shared.models.AuthenticationResponseModel;
import nl.tudelft.sem.template.shared.models.RegistrationRequestModel;
import nl.tudelft.sem.template.shared.utils.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@RestClientTest
public class GatewayServiceTest {

    private RestTemplate restTemplate;

    private GatewayService service;

    @Autowired
    private RestTemplateBuilder builder;

    @Autowired
    private MockRestServiceServer server;

    private static final String apiPrefix = "http://localhost:";
    private static final String registerPath = "/register";
    private static final String authenticatePath = "/authenticate";

    private static final String token = "token";

    private RegistrationRequestModel registerRequest;
    private AuthenticationRequestModel loginRequest;
    private User user;
    private AuthenticationResponseModel loginResponse;

    /**
     * Setup the test.
     */
    @BeforeEach
    public void setUp() {
        registerRequest = new RegistrationRequestModel();
        loginRequest = new AuthenticationRequestModel();
        user = new User();
        loginResponse = new AuthenticationResponseModel(token);

        this.restTemplate = this.builder
                .errorHandler(new RestTemplateResponseErrorHandler())
                .build();
        server = MockRestServiceServer.createServer(restTemplate);
        service = new GatewayService(this.restTemplate);
    }

    @Test
    public void testRegisterUser() throws JsonProcessingException {
        // Set up mock response
        server.expect(requestTo(apiPrefix + MicroservicePorts.AUTHENTICATION.port + registerPath))
                .andRespond(withSuccess(JsonUtil.serialize(user), MediaType.APPLICATION_JSON));

        // Test method
        User result = service.registerUser(registerRequest);

        // Verify result
        assertEquals(user, result);
    }

    @Test
    public void testLogin() throws JsonProcessingException {
        // Set up mock response
        server.expect(requestTo(apiPrefix + MicroservicePorts.AUTHENTICATION.port + authenticatePath))
                .andRespond(withSuccess(JsonUtil.serialize(loginResponse), MediaType.APPLICATION_JSON));

        // Test method
        AuthenticationResponseModel result = service.login(loginRequest);

        // Verify result
        assertEquals(loginResponse, result);
    }
}
