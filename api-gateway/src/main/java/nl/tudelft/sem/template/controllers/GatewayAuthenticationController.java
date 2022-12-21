package nl.tudelft.sem.template.controllers;

import nl.tudelft.sem.template.services.GatewayService;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.models.AuthenticationRequestModel;
import nl.tudelft.sem.template.shared.models.AuthenticationResponseModel;
import nl.tudelft.sem.template.shared.models.RegistrationRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = "/api/auth")
public class GatewayAuthenticationController {
    @Autowired
    private transient GatewayService gatewayService;

    // AUTHENTICATION MS

    /**
     * register a new user.
     */
    @PostMapping(path = "/register")
    public ResponseEntity<User> register(@RequestBody RegistrationRequestModel request) {
        try {
            User response = gatewayService.registerUser(request);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * login a user.
     */
    @PostMapping(path = "/login")
    public ResponseEntity<AuthenticationResponseModel> login(@RequestBody AuthenticationRequestModel request) {
        try {
            AuthenticationResponseModel response = gatewayService.login(request);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
