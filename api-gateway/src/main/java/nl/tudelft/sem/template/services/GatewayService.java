package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.shared.models.RegistrationRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GatewayService {

    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity registerUser(RegistrationRequestModel request) {
        return restTemplate.postForObject("http://localhost:8081/register", request, ResponseEntity.class);
    }
}
