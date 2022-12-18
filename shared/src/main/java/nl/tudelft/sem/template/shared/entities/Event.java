package nl.tudelft.sem.template.shared.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.shared.converters.PositionsToFillListConverter;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.EventType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany
    private List<User> queue;

    public Event(Long owningUser, String label, List<Position> positions, String startTime, String endTime, Certificate certificate, boolean isCompetitive, EventType type, String organisation) throws IllegalArgumentException{
        this.owningUser = owningUser;
        this.label = label;
        this.positions = positions;
        this.startTime = startTime;
        this.endTime = endTime;
        this.certificate = certificate;
        this.isCompetitive = isCompetitive;
        this.type = type;
        this.organisation = organisation;
        this.queue = new ArrayList<>();
    }

    public void addPosition(Position position) {
        positions.add(position);
    }

    public void removePosition(Position position) {
        positions.remove(position);
    }

    public void enqueue(User user){
        queue.add(user);
    }

    public void dequeue(User user){
        queue.remove(user);
    }

    /**
     * Method for converting info about an event to notification message
     * @return a string containing relevant data for a user
     */
    public String messageConverter(){
        return  getLabel() + " - " + getType() + " from " + getStartTime() + " until " + getEndTime() + ".\n";
    }

}

