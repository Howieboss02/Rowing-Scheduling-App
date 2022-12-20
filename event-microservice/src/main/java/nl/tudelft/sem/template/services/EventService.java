package nl.tudelft.sem.template.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.database.EventRepository;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.domain.Request;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.EventType;
import nl.tudelft.sem.template.shared.enums.PositionName;
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

    public List<Event> getAllEventsByUser(Long userId) {
        return eventRepo.findByOwningUser(userId);
    }

    /**
     * Insert an event into the database.
     *
     * @param event the type of event
     * @return List of events
     * @throws IllegalArgumentException exception when the event is not found
     */
    public Event insert(Event event) throws IllegalArgumentException {
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
     * @throws Exception the exception when the event is not found
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
     * @throws IllegalArgumentException exception when the event is not found
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
     * @param type type of the event
     * @param organisation organisation of the event
     * @return the updated event
     */
    public Optional<Event> updateById(Long userId, Long eventId, String label, List<PositionName> positions,
                                      String startTime, String endTime, Certificate certificate,
                                      EventType type, boolean isCompetitive, String organisation) {
        Optional<Event> toUpdate = getById(eventId);
        if (toUpdate.isPresent()) {
            if (!toUpdate.get().getOwningUser().equals(userId)) {
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

            if (type != null) {
                toUpdate.get().setCompetitive(isCompetitive);
            }

            if (organisation != null) {
                toUpdate.get().setOrganisation(organisation);
            }

            if (positions != null) {
                toUpdate.get().setPositions(positions);
            }

            eventRepo.save(toUpdate.get());
        }
        return toUpdate;
    }

    /**
     * Gets the request queue for an event.
     *
     * @param id the id of the event
     * @return the queue of requests
     */
    public List<Request> getRequests(Long id) {
        Optional<Event> event = getById(id);
        if (event.isEmpty()) {
            return new ArrayList<>();
        }
        return event.get().getQueue();
    }

    /**
     * Adds a request to the queue of an event.
     *
     * @param id the id of the event
     * @param user the user who wants to enqueue
     * @param position the position the user wants to fill
     */
    public void enqueueById(Long id, User user, PositionName position) {
        Optional<Event> event = getById(id);

        if (event.isPresent()) {
            event.get().enqueue(user.getNetId(), position);

            eventRepo.save(event.get());
        }
    }

    /**
     * Removes a request from the queue of an event.
     *
     * @param id the id of the event
     * @param request the request to remove
     * @return true if the request was removed, otherwise false
     */
    public boolean dequeueById(Long id, Request request) {
        Optional<Event> event = getById(id);

        if (event.isEmpty()) {
            return false;
        } else {
            boolean success = event.get().dequeue(request);

            eventRepo.save(event.get());
            return success;
        }
    }

    /**
     * Removes a position from the position list of an event.
     *
     * @param id the id of the event
     * @param position the name of the position to remove
     * @return true if the position was removed, otherwise false
     */
    public boolean removePositionById(Long id, PositionName position) {
        Optional<Event> event = getById(id);

        if (event.isEmpty()) {
            return false;
        } else {
            boolean success = event.get().removePosition(position);

            eventRepo.save(event.get());
            return success;
        }
    }


    /**
     * finds the events a user is suitable for.
     *
     * @param user the user for which the returned events should match
     * @return events that match the user
     */
    public List<Event> getMatchedEvents(User user) {
        List<Event> e1 = eventRepo.findMatchingTrainings(user.getCertificate(), user.getId(), EventType.TRAINING);
        List<Event> e2 = eventRepo.findMatchingCompetitions(user.getCertificate(), user.getOrganization(),
                                                            user.getId(), EventType.COMPETITION);
        List<Event> matchedEvents = new ArrayList<>();
        List<Position> positions = new ArrayList<>();
        positions.addAll(user.getPositions());

        for (Event e : e1) {
            for (Position p : positions) {
                if (e.getPositions().contains(p.getName()) && e.isCompetitive() == p.isCompetitive()) {
                    matchedEvents.add(e);
                    break;
                }
            }
        }
        for (Event e : e2) {
            for (Position p : positions) {
                if (e.getPositions().contains(p.getName()) && e.isCompetitive() == p.isCompetitive()) {
                    matchedEvents.add(e);
                    break;
                }
            }
        }
        return matchedEvents;
    }
}
