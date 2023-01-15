package nl.tudelft.sem.template.shared.entities;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.EventType;
import nl.tudelft.sem.template.shared.enums.PositionName;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventModel {
    private Long owningUser;
    private String label;
    private List<PositionName> positions;
    private TimeSlot timeslot;
    private Certificate certificate;
    private EventType type;
    private boolean isCompetitive;
    private String gender;
    private String organisation;

    public Event getEvent() {
        Event event = new Event(owningUser,
                label,
                positions,
                timeslot,
                certificate,
                type,
                isCompetitive,
                gender,
                organisation);
        return event;
    }

}

