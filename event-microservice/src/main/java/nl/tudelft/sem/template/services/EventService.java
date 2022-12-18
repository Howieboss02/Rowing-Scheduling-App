package nl.tudelft.sem.template.services;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.database.EventRepository;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    private final transient EventRepository eventRepo;
    
    @Autowired
    public EventService(EventRepository eventRepo) {
        this.eventRepo = eventRepo;
    }

    public List<Event> getAllEvents() {
        return eventRepo.findAll();
    }

    /**
     * Query to insert events to the database.
     *
     * @param event the new event
     * @return null if the insertion fails or the event itself if it's successful
     */
    public Event insert(Event event) {
        if (event == null) {
            return null;
        } else {
            return eventRepo.save(event);
        }
    }

    /**
     * Query for deleting a specific event identified by id.
     *
     * @param eventId the event's id
     * @throws Exception if the event doesn't exist
     */
    public void deleteById(Long eventId) throws Exception {
        if (!eventRepo.existsById(eventId)) {
            throw new Exception("ID does not exist");
        }
        eventRepo.deleteById(eventId);
    }

    /**
     * Query for retrieving an event by id.
     *
     * @param id the id of the event
     * @return on optional that may contain the event we were looking for
     */
    public Optional<Event> getById(Long id) {
        if (!eventRepo.existsById(id)) {
            return Optional.empty();
        } else {
            return eventRepo.findById(id);
        }
    }

    /**
     * Query to update an event by at least one field.
     *
     * @param userId the user's id
     * @param eventId the event's id
     * @param label the event label (name)
     * @param positions the list of needed positions
     * @param startTime the starting time of the event
     * @param endTime the hour it ends
     * @param certificate the certificate needed to enroll
     * @param isCompetitive if it's for a competitive event
     * @param type competition/training
     * @param organisation the organization the event is being organized by
     * @param editCompetition if it should also change the type of competitiveness
     * @return the newly updated event
     */
    public Optional<Event> updateById(Long userId, Long eventId, String label, List<Position> positions,
                                       String startTime, String endTime, Certificate certificate, boolean isCompetitive,
                                       EventType type, String organisation, boolean editCompetition) {
        Optional<Event> toUpdate = getById(eventId);
        if (toUpdate.isPresent()) {
            if (toUpdate.get().getOwningUser().equals(userId)) {
                System.out.println(toUpdate.get().getOwningUser() + " " + userId);
                return Optional.empty();
            }
            if (label != null) {
                toUpdate.get().setLabel(label);
            }
            if (startTime != null) {
                toUpdate.get().setStartTime(startTime);
            }
            if (endTime != null) {
                toUpdate.get().setEndTime(endTime);
            }
            if (certificate != null) {
                toUpdate.get().setCertificate(certificate);
            }
            if (type != null) {
                toUpdate.get().setType(type);
            }
            if (organisation != null) {
                toUpdate.get().setOrganisation(organisation);
            }
            if (editCompetition == true) {
                toUpdate.get().setCompetitive(isCompetitive);
            }
            if (positions != null) {
                toUpdate.get().setPositions(positions);
            }
            eventRepo.save(toUpdate.get());
        }
        return toUpdate;
    }


}
