package nl.tudelft.sem.template.shared.entities;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.EventType;

@Data
@AllArgsConstructor
public class EventModel {
    private Long owningUser;
    private String label;
    private List<Position> positions;
    private String startTime;
    private String endTime;
    private Certificate certificate;
    private EventType type;
    private String organisation;

}

