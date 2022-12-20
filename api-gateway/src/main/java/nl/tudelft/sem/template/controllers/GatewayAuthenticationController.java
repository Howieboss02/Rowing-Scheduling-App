package nl.tudelft.sem.template.controllers;

import nl.tudelft.sem.template.services.GatewayService;
import nl.tudelft.sem.template.shared.models.AuthenticationRequestModel;
import nl.tudelft.sem.template.shared.models.AuthenticationResponseModel;
import nl.tudelft.sem.template.shared.models.RegistrationRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class GatewayAuthenticationController {
    @Autowired
    private transient GatewayService gatewayService;

    // AUTHENTICATION MS

    @PostMapping(path = "/auth/register")
    public ResponseEntity register(@RequestBody RegistrationRequestModel request) {
        return gatewayService.registerUser(request);
    }

    @PostMapping(path = "/auth/login")
    public ResponseEntity<AuthenticationResponseModel> login(@RequestBody AuthenticationRequestModel request) {
        return ResponseEntity.ok(gatewayService.login(request));
    }

    // test line who edited it
}
