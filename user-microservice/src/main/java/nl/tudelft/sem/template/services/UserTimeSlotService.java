package nl.tudelft.sem.template.services;

import java.util.Optional;
import nl.tudelft.sem.template.database.UserRepository;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserTimeSlotService {

    private final transient UserRepository userRepo;
    private final transient UserService service;

    @Autowired
    public UserTimeSlotService(UserRepository userRepo, UserService service) {
        this.userRepo = userRepo;
        this.service = service;
    }

    /**
     * Update the availability of a user if timeslot is valid.
     * Week number has to be -1.
     *
     * @param id the id of the user
     * @param timeSlot to be added
     * @return the updated user
     */
    public Optional<User> addRecurringTimeSlot(Long id, TimeSlot timeSlot) {
        Optional<User> user = service.getById(id);

        if (user.isPresent() && timeSlot.getTime().getFirst() < timeSlot.getTime().getSecond() && timeSlot.getWeek() == -1) {
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
        Optional<User> user = service.getById(id);

        if (user.isPresent()) {
            user.get().removeRecurringSlot(timeSlot);
            userRepo.save(user.get());
        }
        return user;
    }

    /**
     * Add a one time slot to a user if timeslot is valid.
     * Week numbers can't be negative & time hours have to be cursive.
     *
     * @param id the id of the user
     * @param timeSlot to be added
     * @return the updated user
     */
    public Optional<User> addTimeSlot(Long id, TimeSlot timeSlot) {
        Optional<User> user = service.getById(id);

        if (user.isPresent() && timeSlot.getTime().getFirst() < timeSlot.getTime().getSecond() && timeSlot.getWeek() > 0) {
            user.get().addSlot(timeSlot);
            userRepo.save(user.get());
        }
        return user;
    }

    /**
     * Remove one time slot from a user.
     *
     * @param id the id of the user
     * @param timeSlot to be removed
     */
    public Optional<User> removeTimeSlot(Long id, TimeSlot timeSlot) {
        Optional<User> user = service.getById(id);

        if (user.isPresent()) {
            user.get().removeSlot(timeSlot);
            userRepo.save(user.get());
        }
        return user;
    }
}