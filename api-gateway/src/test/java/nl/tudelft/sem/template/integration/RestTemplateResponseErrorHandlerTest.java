package nl.tudelft.sem.template.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.tudelft.sem.template.components.RestTemplateResponseErrorHandler;
import nl.tudelft.sem.template.shared.entities.User;

import static org.assertj.core.api.Assertions.*;

import nl.tudelft.sem.template.shared.utils.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

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

    @Test
    public void  givenRemoteApiCall_when500Error_thenThrowInternalServerError() {
        this.server.expect(ExpectedCount.once(), requestTo("/api/user/internalservererror"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> {
            User response = restTemplate.getForObject("/api/user/internalservererror", User.class);
        }).isInstanceOf(ResponseStatusException.class).hasMessage("500 INTERNAL_SERVER_ERROR \"Internal Server Error\"");
    }

    @Test
    public void  givenRemoteApiCall_when400Error_thenThrowBadRequest() {
        this.server.expect(ExpectedCount.once(), requestTo("/api/user/badrequest"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> {
            User response = restTemplate.getForObject("/api/user/badrequest", User.class);
        }).isInstanceOf(ResponseStatusException.class).hasMessage("400 BAD_REQUEST \"Bad Request\"");
    }
    //test hasError function form RestTemplateResponseErrorHandler
    @Test
    public void  givenRemoteApiCall_when200Error_thenNoError() throws JsonProcessingException {
        User expected = new User("testNetID", "testName", "testEmail");
        this.server.expect(ExpectedCount.once(), requestTo("/api/user/ok"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(JsonUtil.serialize(expected), MediaType.APPLICATION_JSON));

        User response = restTemplate.getForObject("/api/user/ok", User.class);

        assertThat(response).isEqualTo(expected);
    }


}
