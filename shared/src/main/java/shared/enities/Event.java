package shared.enities;

import lombok.Data;
import shared.converters.PositionsToFIllListConverter;
import shared.domain.Position;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "owning-user", nullable = false)
    private Long owningUser;

    @Column(name = "label", nullable = false, unique = true)
    private String label;

    @Column(name = "positions")
    @Convert(converter = PositionsToFIllListConverter.class)
    private List<Position> positions;

    @Column(name = "startTime", nullable = false)
    private String startTime;

    @Column(name = "endTime", nullable = false)
    private String endTime;

    public void addPosition(Position position) {
        positions.add(position);
    }

    public void removePosition(Position position) {
        positions.remove(position);
    }


}

