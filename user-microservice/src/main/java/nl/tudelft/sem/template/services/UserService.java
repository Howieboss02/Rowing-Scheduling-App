package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.database.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Certificate;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {


    private final UserRepository userRepo;

    @Autowired
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public Optional<User> getById(Long id) {
        if (id < 0 || !userRepo.existsById(id)) {
            return Optional.empty();
        } else {
            return userRepo.findById(id);
        }
    }

    public User insert(User user) {
        if (user == null || user.getName().isEmpty()){
            return null;
        }
        //We are currently not checking if the user already exists
        return userRepo.save(user);
    }

    public boolean deleteById(Long id) {
        if (id < 0 || !userRepo.existsById(id)) {
            return false;
        } else {
            userRepo.deleteById(id);
        }
        return true;
    }

    public Optional<User> updateById(Long id, String name, String organization, String email, String gender,
                                     Certificate certificate, List<Position> positions) {

        Optional<User> toUpdate = getById(id);

        if (toUpdate.isPresent()) {
            toUpdate.get().setName(name);
            toUpdate.get().setOrganization(organization);
            toUpdate.get().setEmail(email);
            toUpdate.get().setGender(gender);
            toUpdate.get().setCertificate(certificate);
            toUpdate.get().setPositions(positions);

            userRepo.save(toUpdate.get());
        }
        return toUpdate;
    }

    public Optional<List<String>> getNotifications(Long id) {
        Optional<User> user = getById(id);
        return user.map(User::getNotifications);
    }
}
