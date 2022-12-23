package nl.tudelft.sem.template.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.database.EventRepository;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.domain.Request;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.EventType;
import nl.tudelft.sem.template.shared.enums.MicroservicePorts;
import nl.tudelft.sem.template.shared.enums.PositionName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EventService {
    private final transient EventRepository eventRepo;
    private static final String apiPrefix = "http://localhost:";
    private static final String userPath = "/api/user/";

    @Autowired
    private transient RestTemplate restTemplate;
    
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
     * @param timeslot the time and date of the event
     * @param certificate certificate of the event
     * @param type type of the event
     * @param gender the gender required in case of event
     * @param organisation organisation of the event
     * @param updateIsCompetitive helps in deciding weather to update an event or not
     * @return the updated event
     */
    public Optional<Event> updateById(Long userId, Long eventId, String label, List<PositionName> positions,
                                       TimeSlot timeslot, Certificate certificate,
                                       EventType type, boolean isCompetitive, String gender,
                                       String organisation, boolean updateIsCompetitive) {
        Optional<Event> toUpdate = getById(eventId);
        if (toUpdate.isPresent()) {
            if (!toUpdate.get().getOwningUser().equals(userId)) {
                return Optional.empty();
            }

            if (label != null) {
                toUpdate.get().setLabel(label);
            }

            if  (timeslot != null) {
                toUpdate.get().setTimeslot(timeslot);
            }

            if (certificate != null) {
                toUpdate.get().setCertificate(certificate);
            }

            if (type != null) {
                toUpdate.get().setType(type);
            }

            if (updateIsCompetitive) {
                toUpdate.get().setCompetitive(isCompetitive);
            }

            if (gender != null) {
                toUpdate.get().setGender(gender);
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
     * Adds a request to the queue of an event iff the user should be able to join the event.
     *
     * @param id the id of the event
     * @param user the user who wants to enqueue
     * @param position the position the user wants to fill
     */
    public boolean enqueueById(Long id, User user, PositionName position) {
        Optional<Event> event = getById(id);

        if (event.isEmpty() || user.getPositions() == null) {
            return false;
        }
        Event actualEvent = event.get();

        // Check if user that wants to enqueue is not creator
        if (user.getId().equals(actualEvent.getOwningUser())) {
            return false;
        }

        // Check if event is competitive but user is not
        List<Position> userPositions = user.getPositions().stream()
                .filter(u -> u.getName() == position)
                .collect(Collectors.toList());
        Position toFind = new Position(position, true);
        if (actualEvent.getType() == EventType.COMPETITION
                && actualEvent.isCompetitive() && !userPositions.contains(toFind)) {
            return false;
        }

        // Check if gender and organization match in case of competition
        if (actualEvent.getType() == EventType.COMPETITION
                && (!actualEvent.getOrganisation().equals(user.getOrganization())
                || !actualEvent.getGender().equals(user.getGender()))) {
            return false;
        }

        // Check if the certificate level is high enough
        if (user.getCertificate().compareTo(actualEvent.getCertificate()) < 0) {
            return false;
        }

        boolean success = actualEvent.enqueue(user.getNetId(), position);
        eventRepo.save(actualEvent);
        return success;
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
                                                            user.getId(), EventType.COMPETITION, user.getGender());
        List<Event> matchedEvents = new ArrayList<>();
        List<Position> positions = new ArrayList<>();
        positions.addAll(user.getPositions());

        for (Event e : e1) {
            for (Position p : positions) {
                if (e.getPositions().contains(p.getName()) && (!e.isCompetitive() || p.isCompetitive())
                        && e.getTimeslot().matchSchedule(user.getSchedule())) {
                    matchedEvents.add(e);
                    break;
                }
            }
        }
        for (Event e : e2) {
            for (Position p : positions) {
                if (e.getPositions().contains(p.getName()) && (!e.isCompetitive() || p.isCompetitive())
                        && e.getTimeslot().matchSchedule(user.getSchedule())) {
                    matchedEvents.add(e);
                    break;
                }
            }
        }
        return matchedEvents;
    }

    public User getUserById(Long id) {
        return restTemplate.getForObject(apiPrefix + MicroservicePorts.USER.port + userPath + id, User.class);
    }
}
