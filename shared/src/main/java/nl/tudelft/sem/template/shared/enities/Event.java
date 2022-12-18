package nl.tudelft.sem.template.shared.enities;

import lombok.*;
import nl.tudelft.sem.template.shared.converters.PositionsToFIllListConverter;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.EventType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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
    @Convert(converter = PositionsToFIllListConverter.class)
    private List<Position> positions = new ArrayList<>();

    @Column(name = "startTime", nullable = false)
    private String startTime;

    @Column(name = "endTime", nullable = false)
    private String endTime;

    @Column(name = "certificate", nullable = false)
    private Certificate certificate;

    private EventType type;

    @Column(name = "organisation")
    private String organisation;

    public Event(Long owningUser, String label, List<Position> positions, String startTime, String endTime, Certificate certificate, EventType type, String organisation) throws IllegalArgumentException{
        this.owningUser = owningUser;
        this.label = label;
        this.positions = positions;
        this.startTime = startTime;
        this.endTime = endTime;
        this.certificate = certificate;
        this.type = type;
        this.organisation = organisation;
    }

    public void addPosition(Position position) {
        positions.add(position);
    }

    public void removePosition(Position position) {
        positions.remove(position);
    }


}

