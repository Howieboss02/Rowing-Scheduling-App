package nl.tudelft.sem.template.services;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.database.UserRepository;
import nl.tudelft.sem.template.shared.domain.Node;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.Day;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
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
        } else {
            return userRepo.findById(id);
        }
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
        List<User> all = userRepo.findAll();
        for (User user : all) {
            if (user.getNetId().equals(name)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    /**
     * Add user to the database.
     *
     * @param user full information about a user
     * @return the information of the added user
     */
    public User insert(User user) {
        if (user == null || user.getNetId().isEmpty()) {
            return null;
        }
        System.out.println("Inserting user: " + user.getId());
        //We are currently not checking if the user already exists
        return userRepo.save(user);
    }

    /**
     * Delete a specific user found by id.
     *
     * @param id the id of the user
     * @return whether the user was deleted or not
     */
    public boolean deleteById(Long id) {
        if (id < 0 || !userRepo.existsById(id)) {
            return false;
        } else {
            userRepo.deleteById(id);
        }
        return true;
    }

    /**
     * Update information about a user inside the database.
     *
     * @param id           the id of the user we want to update
     * @param name         the netId of the user
     * @param organization the organization the user is part of
     * @param gender       the gender of the rower
     * @param certificate  the biggest certificate a user holds
     * @param positions    the list of position they can fill
     * @return the new profile
     */
    public Optional<User> updateById(Long id, String name, String organization, String gender,
                                     Certificate certificate, List<Position> positions) {
        Optional<User> toUpdate = getById(id);
        System.out.println("chuj wam w dupy");
        if (toUpdate.isPresent()) {
            System.out.println("walzłem do ifa");
            if (name != null) {
                System.out.println("Updating name to: " + name);
                toUpdate.get().setName(name);
            }
            if (organization != null) {
                System.out.println("Updating organization to: " + organization);
                toUpdate.get().setOrganization(organization);
            }
            if (gender != null) {
                System.out.println("updating gender:" + gender);
                toUpdate.get().setGender(gender);
            }
            if (certificate != null) {
                System.out.println("Updating certificate to: " + certificate);
                toUpdate.get().setCertificate(certificate);
            }
            if (positions != null) {
                System.out.println("Updating positions to: " + positions);
                toUpdate.get().setPositions(positions);
            }
            System.out.println(toUpdate);
            userRepo.save(toUpdate.get());
        }
        return toUpdate;
    }

    /**
     * Update the availability of a user.
     *
     * @param id the id of the user
     * @param timeSlot to be added
     * @return the updated user
     */
    public Optional<User> addRecurringTimeSlot(Long id, TimeSlot timeSlot) {
        Optional<User> user = getById(id);

        if (user.isPresent()) {
            user.get().addRecurringSlot(timeSlot);
            userRepo.save(user.get());
        }
        return user;
    }

    /**
     * Remove a recurring time slot from a user.
     *
     * @param id the id of the user
     * @param timeSlot to be removed
     */
    public Optional<User> removeRecurringTimeSlot(Long id, TimeSlot timeSlot) {
        Optional<User> user = getById(id);

        if (user.isPresent()) {
            user.get().removeRecurringSlot(timeSlot);
            userRepo.save(user.get());
        }
        return user;
    }

    /**
     * Add a one time time slot to a user.
     */
    public Optional<User> addTimeSlot(Long id, TimeSlot timeSlot) {
        Optional<User> user = getById(id);

        if (user.isPresent()) {
            user.get().addSlot(timeSlot);
            userRepo.save(user.get());
        }
        return user;
    }

    /**
     * Remove a one time time slot from a user.
     */
    public Optional<User> removeTimeSlot(Long id, TimeSlot timeSlot) {
        Optional<User> user = getById(id);

        if (user.isPresent()) {
            user.get().removeSlot(timeSlot);
            userRepo.save(user.get());
        }
        return user;
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
