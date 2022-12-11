package nl.tudelft.sem.template.shared.enities;


import lombok.AllArgsConstructor;
import lombok.Data;
import nl.tudelft.sem.template.shared.converters.PositionsToFIllListConverter;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.EventType;

import javax.persistence.Column;
import javax.persistence.Convert;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class EventModel {
    private Long owningUser;
    private String label;
    private List<Position> positions;
    private String startTime;
    private String endTime;
    private Certificate certificate;
    private boolean isCompetitive;
    private EventType type;
    private String organisation;

}

