package nl.tudelft.sem.template.services;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.database.UserRepository;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.entities.UserModel;
import nl.tudelft.sem.template.shared.enums.Certificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final transient UserRepository userRepo;

    @Autowired
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Get all users.
     */
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    /**
     * Identify user by id.
     *
     * @param id the id of the user
     * @return information about the user
     */
    public Optional<User> getById(Long id) {
        if (id < 0 || !userRepo.existsById(id)) {
            return Optional.empty();
        }
        return userRepo.findById(id);
    }

    /**
     * Identify user by unique netId.
     *
     * @param name the netId of the user
     * @return information about the user
     */
    public Optional<User> getByNetId(String name) {
        if (name.isEmpty()) {
            return Optional.empty();
        }
        for (User user : userRepo.findAll()) {
            if (user.getUserInfo().getNetId().equals(name)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    /**
     * Add user to the database.
     * If the user already exists, system will give an error
     *
     * @param user full information about a user
     * @return the information of the added user
     */
    public User insert(User user) {
        if (user == null || user.getUserInfo().getNetId().isEmpty() || userRepo.existsById(user.getId())) {
            return null;
        }
        return userRepo.save(user);
    }

    /**
     * Delete a specific user found by id.
     *
     * @param id the id of the user
     * @return whether the user was deleted or not
     */
    public boolean deleteById(Long id) {
        if (id <= 0 || !userRepo.existsById(id)) {
            return false;
        }
        userRepo.deleteById(id);
        return true;
    }

    /**
     * Update information about a user inside the database.
     *
     * @param id the id of the user we want to update
     * @param userModel the new information about the user
     * @return the new profile
     */
    public Optional<User> updateById(Long id, UserModel userModel) {
        Optional<User> toUpdate = getById(id);
        if (toUpdate.isPresent()) {
            User user = toUpdate.get();
            String name = userModel.getName();
            if (name != null) {
                toUpdate.get().getUserInfo().setName(name);
            }
            String organization = userModel.getOrganization();
            if (organization != null) {
                toUpdate.get().getUserInfo().setOrganization(organization);
            }
            String gender = userModel.getGender();
            if (gender != null) {
                toUpdate.get().getUserInfo().setGender(gender);
            }
            Certificate certificate = userModel.getCertificate();
            if (certificate != null) {
                toUpdate.get().getUserInfo().setCertificate(certificate);
            }
            List<Position> positions = userModel.getPositions();
            if (positions != null) {
                user.setPositions(positions);
            }
            userRepo.save(user);
        }
        return toUpdate;
    }

    /**
     * Get all notifications for a user.
     */
    public Optional<List<String>> getNotifications(Long id) {
        Optional<User> user = getById(id);
        return user.map(User::getNotifications);
    }

    /**
     * Add a notification to a user.
     */
    public User addNotification(Long id, String notification) {
        Optional<User> user = getById(id);
        if (user.isPresent()) {
            User u = user.get();
            u.addNotification(notification);
            userRepo.save(u);
            return u;
        }
        return null;
    }
}
