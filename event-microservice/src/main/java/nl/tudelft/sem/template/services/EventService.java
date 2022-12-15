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
    private transient EventRepository eventRepo;

    @Autowired
    public EventService(EventRepository eventRepo) {
        this.eventRepo = eventRepo;
    }

    public List<Event> getAllEvents() {
        return eventRepo.findAll();
    }

    /**
     * Insert an event into the database.
     *
     * @param event the type of event
     * @return List of events
     */
    public Event insert(Event event) throws Exception {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        } else {
            return eventRepo.save(event);
        }
    }

    /**
     * Delete an event from the database.
     *
     * @param eventId id of the event to delete
     * @throws Exception exception when the event is not found
     */
    public void deleteById(Long eventId) throws Exception {
        if (!eventRepo.existsById(eventId)) {
            throw new Exception("ID does not exist");
        }
        eventRepo.deleteById(eventId);
    }

    /**
     * Get an event by id.
     *
     * @param id of the event to get
     * @return the event
     * @throws Exception exception when the event is not found
     */
    public Optional<Event> getById(Long id) {
        if (!eventRepo.existsById(id)) {
            return Optional.empty();
        } else {
            return eventRepo.findById(id);
        }
    }

    /**
     * Update an event.
     *
     * @param userId id of the user
     * @param eventId event id
     * @param label label of the event
     * @param positions positions of the event
     * @param startTime start time of the event
     * @param endTime endtime of the event
     * @param certificate certificate of the event
     * @param isCompetitive is the event competitive
     * @param type type of the event
     * @param organisation organisation of the event
     * @param editCompetition edit competition
     * @return the updated event
     */
    public Optional<Event> updateById(Long userId, Long eventId, String label, List<Position> positions,
                                      String startTime, String endTime, Certificate certificate,
                                      boolean isCompetitive, EventType type, String organisation,
                                      boolean editCompetition) {
        Optional<Event> toUpdate = getById(eventId);
        if (toUpdate.isPresent()) {
            if (!toUpdate.get().getOwningUser().equals(userId)) {
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
