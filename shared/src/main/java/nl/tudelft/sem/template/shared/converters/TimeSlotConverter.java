package nl.tudelft.sem.template.shared.converters;

import java.util.Arrays;
import java.util.List;
import javax.persistence.AttributeConverter;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.enums.Day;
import org.springframework.data.util.Pair;

public class TimeSlotConverter implements AttributeConverter<TimeSlot, String> {

    private static final String SPLIT = ",";

    /**
     * Converts a TimeSlot to a string.
     *
     * @param timeslot the entity attribute value to be converted
     * @return a string representing the given schedule
     */
    @Override
    public String convertToDatabaseColumn(TimeSlot timeslot) {
        if (timeslot == null) {
            return "";
        }

        StringBuilder timeSlotString = new StringBuilder();

        timeSlotString.append(timeslot.getWeek()).append(SPLIT)
                .append(timeslot.getDay()).append(SPLIT)
                .append(timeslot.getTime().getFirst()).append(SPLIT)
                .append(timeslot.getTime().getSecond());

        return timeSlotString.toString();
    }

    @Override
    public TimeSlot convertToEntityAttribute(String dbData) {
        if (dbData.equals("")) {
            return null;
        }
        List<String> timeslotFields = Arrays.asList(dbData.split(SPLIT));
        return new TimeSlot(Integer.parseInt(timeslotFields.get(0)),
                Day.valueOf(timeslotFields.get(1)),
                Pair.of(Integer.parseInt(timeslotFields.get(2)), Integer.parseInt(timeslotFields.get(3))));
    }
}
