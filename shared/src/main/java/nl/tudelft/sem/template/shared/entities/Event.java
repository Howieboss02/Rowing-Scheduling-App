package nl.tudelft.sem.template.shared.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.shared.converters.PositionsToFillListConverter;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.EventType;

@Data
@Entity
@Table(name = "event")
@AllArgsConstructor
@NoArgsConstructor
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
    @Convert(converter = PositionsToFillListConverter.class)
    private List<Position> positions = new ArrayList<>();

    @Column(name = "startTime", nullable = false)
    private String startTime;

    @Column(name = "endTime", nullable = false)
    private String endTime;

    @Column(name = "certificate", nullable = false)
    private Certificate certificate;

    private boolean isCompetitive;

    private EventType type;

    @Column(name = "organisation")
    private String organisation;

    /**
     * Constructor for the Event class containing all information.
     *
     * @param owningUser the id of the user that created the event
     * @param label the name of the event
     * @param positions the positions that need to be filled
     * @param startTime the start time of the event
     * @param endTime the end time of the event
     * @param certificate the certificate that is required for the event
     * @param isCompetitive whether the event is competitive or not
     * @param type the type of the event
     * @param organisation the organisation that created the event
     * @throws IllegalArgumentException if any of the parameters are null
     */
    public Event(Long owningUser, String label, List<Position> positions, String startTime,
                 String endTime, Certificate certificate, boolean isCompetitive,
                 EventType type, String organisation) throws IllegalArgumentException {
        this.owningUser = owningUser;
        this.label = label;
        this.positions = positions;
        this.startTime = startTime;
        this.endTime = endTime;
        this.certificate = certificate;
        this.isCompetitive = isCompetitive;
        this.type = type;
        this.organisation = organisation;
    }

    public void addPosition(Position position) {
        positions.add(position);
    }

    public void removePosition(Position position) {
        positions.remove(position);
    }

    /**
     * Method for converting info about an event to notification message.
     *
     * @return a string containing relevant data for a user
     */
    public String messageConverter() {
        return getLabel() + " - " + getType() + " from " + getStartTime() + " until " + getEndTime() + ".\n";
    }

}

