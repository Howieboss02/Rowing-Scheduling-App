package nl.tudelft.sem.template.integration;

import nl.tudelft.sem.template.components.RestTemplateResponseErrorHandler;
import nl.tudelft.sem.template.shared.entities.User;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ContextConfiguration(classes = { User.class })
@RestClientTest
public class RestTemplateResponseErrorHandlerTest {

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private RestTemplateBuilder builder;

    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        System.out.println("setUp");
        this.restTemplate = this.builder
                .errorHandler(new RestTemplateResponseErrorHandler())
                .build();
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void  givenRemoteApiCall_when404Error_thenThrowNotFound() {
        this.server.expect(ExpectedCount.once(), requestTo("/api/user/nonexistent"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> {
            User response = restTemplate.getForObject("/api/user/nonexistent", User.class);
        }).isInstanceOf(ResponseStatusException.class).hasMessage("404 NOT_FOUND \"Not Found\"");

    }


}
