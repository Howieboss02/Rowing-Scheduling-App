package nl.tudelft.sem.template.shared.entities;

import java.util.ArrayList;
import java.util.List;
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
@EqualsAndHashCode
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
        if (!positions.contains(position)) {
            return false;
        }

        queue.add(new Request(name, position));
        return true;
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
}

