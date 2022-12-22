package nl.tudelft.sem.template.authentication.domain.user;

import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.MicroservicePorts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * A DDD nl.tudelft.sem.template.service for registering a new user.
 */
@Service
public class RegistrationService {
    private final transient UserRepository userRepository;
    private final transient PasswordHashingService passwordHashingService;

    private static final String apiPrefix = "http://localhost:";
    private static final String userPath = "/api/user";

    @Autowired
    private transient RestTemplate restTemplate;

    /**
     * Instantiates a new UserService.
     *
     * @param userRepository         the user repository
     * @param passwordHashingService the password encoder
     */
    public RegistrationService(UserRepository userRepository, PasswordHashingService passwordHashingService) {
        this.userRepository = userRepository;
        this.passwordHashingService = passwordHashingService;
    }

    /**
     * Register a new user.
     *
     * @param netId    The NetID of the user
     * @param password The password of the user
     * @throws Exception if the user already exists
     */
    public AppUser registerUser(NetId netId, Password password, Email email) throws Exception {
        try {
            if (userRepository.existsByNetId(netId)) {
                throw new NetIdAlreadyInUseException(netId);
            }
            if (userRepository.existsByEmail(email)) {
                throw new EmailAlreadyInUseException(email);
            }
            // Hash password
            HashedPassword hashedPassword = passwordHashingService.hash(password);

            // Create new account
            AppUser user = new AppUser(netId, hashedPassword, email);

            userRepository.save(user);
            return user;
        } catch (Exception e) {
            throw new Exception("Could not register user", e);
        }
    }

    public User registerUserDetails(User userToRegister) {
        return restTemplate.postForObject(this.apiPrefix + MicroservicePorts.USER.port + userPath
                + "/register", userToRegister, User.class);
    }

    public boolean checkNetIdIsUnique(NetId netId) {
        return !userRepository.existsByNetId(netId);
    }

    public boolean checkEmailIsUnique(Email email) {
        return !userRepository.existsByEmail(email);
    }
}
