package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.shared.enums.MicroservicePorts;
import nl.tudelft.sem.template.shared.models.AuthenticationRequestModel;
import nl.tudelft.sem.template.shared.models.AuthenticationResponseModel;
import nl.tudelft.sem.template.shared.models.RegistrationRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GatewayService {

    private final String API_PREFIX = "http://localhost:";

    @Autowired
    private RestTemplate restTemplate;

    //TODO: add other services and try catch statements along with proper ResponseEntity return types

    public ResponseEntity registerUser(RegistrationRequestModel request) {
        return restTemplate.postForObject(this.API_PREFIX + MicroservicePorts.AUTHENTICATION.port + "/register", request, ResponseEntity.class);
    }

    public AuthenticationResponseModel login(AuthenticationRequestModel request) {
        return restTemplate.postForObject(this.API_PREFIX + MicroservicePorts.AUTHENTICATION.port + "/authenticate", request, AuthenticationResponseModel.class);
    }
}
