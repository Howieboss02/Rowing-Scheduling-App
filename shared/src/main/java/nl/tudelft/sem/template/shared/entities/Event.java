package nl.tudelft.sem.template.shared.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.*;
import lombok.*;
import nl.tudelft.sem.template.shared.converters.RequestConverter;
import nl.tudelft.sem.template.shared.converters.StringTimeToMinutesConverter;
import nl.tudelft.sem.template.shared.converters.TimeSlotConverter;
import nl.tudelft.sem.template.shared.domain.Request;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.EventType;
import nl.tudelft.sem.template.shared.enums.PositionName;

@Getter
@Setter
@Entity
@ToString
@Table(name = "event")
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "owningUser", nullable = false)
    private Long owningUser;

    @Column(name = "label", nullable = false, unique = true)
    private String label;

    @Column(name = "positions")
    @ElementCollection(targetClass = PositionName.class)
    private List<PositionName> positions = new ArrayList<>();

    @Column(name = "timeslot")
    @Convert(converter = TimeSlotConverter.class)
    private TimeSlot timeslot = new TimeSlot();

    @Column(name = "certificate", nullable = false)
    private Certificate certificate;

    private EventType type;

    private boolean isCompetitive;

    private String gender;

    @Column(name = "organisation")
    private String organisation;

    @Convert(converter = RequestConverter.class)
    private List<Request> queue;

    /**
     * Empty constructor for Event.
     */
    public Event() {
        queue = new ArrayList<>();
    }

    /**
     * Constructor for the Event class containing all information.
     *
     * @param owningUser the id of the user that created the event
     * @param label the name of the event
     * @param positions the positions that need to be filled
     * @param timeslot the time and date of the event
     * @param certificate the certificate that is required for the event
     * @param type the type of the event
     * @param isCompetitive the competitiveness of the event
     * @param gender the gender, in case of a competition
     * @param organisation the organisation that created the event
     * @throws IllegalArgumentException if any of the parameters are null
     */
    public Event(Long owningUser, String label, List<PositionName> positions, TimeSlot timeslot, Certificate certificate,
                 EventType type, boolean isCompetitive, String gender, String organisation) throws IllegalArgumentException {
        this.owningUser = owningUser;
        this.label = label;
        this.positions = positions;
        this.timeslot = timeslot;
        this.certificate = certificate;
        this.type = type;
        this.isCompetitive = isCompetitive;
        this.gender = gender;
        this.organisation = organisation;
        this.queue = new ArrayList<>();
    }

    public void addPosition(PositionName position) {
        positions.add(position);
    }

    public boolean removePosition(PositionName position) {
        return positions.remove(position);
    }

    /**
     * Enqueues a user to a position if that position is desired.
     *
     * @param name the name of the user that enqueues
     * @param position position to enqueue for
     * @return true iff the enqueue was successfull
     */
    public boolean enqueue(String name, PositionName position) {
        return queue.add(new Request(name, position));
    }

    public boolean dequeue(Request request) {
        return queue.remove(request);
    }

    /**
     * Method for converting info about an event to notification message.
     *
     * @return a string containing relevant data for a user
     */
    public String messageConverter() {
        StringTimeToMinutesConverter sc = new StringTimeToMinutesConverter();
        return getLabel() + " - " + getType() + " from "
                + sc.convertToEntityAttribute(timeslot.getTime().getFirst()) + " until "
                + sc.convertToEntityAttribute(timeslot.getTime().getSecond()) + " in week "
                + timeslot.getWeek() + ", on " + timeslot.getDay().toString() + ".\n";
    }

    /**
     * Method for updating event with data from another event.
     *
     * @param eventModel the event to update from
     * @param updateIsCompetitive whether to update the competitiveness
     *
     * @return a copy of updated event
     */
    public Event merge(EventModel eventModel, boolean updateIsCompetitive) {

        if (!eventModel.getOwningUser().equals(this.getOwningUser())) {
            return null;
        }
        String label = eventModel.getLabel();
        if (label != null) {
            this.setLabel(label);
        }
        TimeSlot timeslot = eventModel.getTimeslot();
        if  (timeslot != null) {
            this.setTimeslot(timeslot);
        }
        Certificate certificate = eventModel.getCertificate();
        if (certificate != null) {
            this.setCertificate(certificate);
        }
        EventType type = eventModel.getType();
        if (type != null) {
            this.setType(type);
        }

        if (updateIsCompetitive) {
            this.setCompetitive(eventModel.isCompetitive());
        }
        String gender = eventModel.getGender();
        if (gender != null) {
            this.setGender(gender);
        }
        String organisation = eventModel.getOrganisation();
        if (organisation != null) {
            this.setOrganisation(organisation);
        }
        List<PositionName> positions = eventModel.getPositions();
        if (positions != null) {
            this.setPositions(positions);
        }
        return this;
    }

    /**
     * Checks if an object is equal to the event by comparing their labels,
     * which are unique and non-null for every event.
     *
     * @param o the object to compare
     * @return true iff the object is equal to the event
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Event event = (Event) o;
        return label.equals(event.label);
    }

    /**
     * Hashes te event label, since this is unique and non-null for every event.
     *
     * @return the hashcode for the event
     */
    @Override
    public int hashCode() {
        return Objects.hash(label);
    }
}

