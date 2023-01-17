package nl.tudelft.sem.template.services;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.database.UserRepository;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Certificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserSetterService {

    private final transient UserRepository userRepo;
    private final transient UserService service;

    @Autowired
    public UserSetterService(UserRepository userRepo, UserService service) {
        this.userRepo = userRepo;
        this.service = service;
    }

    /**
     * Set user's name.
     */
    public Optional<User> setName(Long id, String name) {
        Optional<User> user = service.getById(id);

        if (user.isPresent()) {
            user.get().setName(name);
            userRepo.save(user.get());
        }
        return user;
    }

    /**
     * Set user's organization.
     */
    public Optional<User> setOrganization(Long id, String organization) {
        Optional<User> user = service.getById(id);

        if (user.isPresent()) {
            user.get().setOrganization(organization);
            userRepo.save(user.get());
        }
        return user;
    }

    /**
     * Set user's gender.
     */
    public Optional<User> setGender(Long id, String gender) {
        Optional<User> user = service.getById(id);

        if (user.isPresent()) {
            user.get().setGender(gender);
            userRepo.save(user.get());
        }
        return user;
    }

    /**
     * Set user's certificate.
     */
    public Optional<User> setCertificate(Long id, Certificate certificate) {
        Optional<User> user = service.getById(id);

        if (user.isPresent()) {
            user.get().setCertificate(certificate);
            userRepo.save(user.get());
        }
        return user;
    }

    /**
     * Set user's positions.
     */
    public Optional<User> setPositions(Long id, List<Position> positions) {
        Optional<User> user = service.getById(id);

        if (user.isPresent()) {
            user.get().setPositions(positions);
            userRepo.save(user.get());
        }
        return user;
    }
}