package nl.tudelft.sem.template.shared.enities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.shared.converters.PositionsToFIllListConverter;
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
    @Convert(converter = PositionsToFIllListConverter.class)
    private List<Position> positions = new ArrayList<>();

    @Column(name = "startTime", nullable = false)
    private String startTime;

    @Column(name = "endTime", nullable = false)
    private String endTime;

    @Column(name = "certificate", nullable = false)
    private Certificate certificate;

    private boolean isCompetitive;

    private EventType type;




    public void addPosition(Position position) {
        positions.add(position);
    }

    public void removePosition(Position position) {
        positions.remove(position);
    }


}

