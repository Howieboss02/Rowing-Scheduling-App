package nl.tudelft.sem.template.shared.converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import nl.tudelft.sem.template.shared.domain.Schedule;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.enums.Day;
import org.springframework.data.util.Pair;

@Converter
public class ScheduleConverter implements AttributeConverter<Schedule, String> {

    private static final String LIST_SPLIT_CHAR = "-";
    private static final String SLOT_SPLIT_CHAR = ";";
    private static final String FIELD_SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(Schedule schedule) {
        if (schedule == null) {
            return "";
        }
        StringBuilder scheduleString = new StringBuilder();

        // For recurring slots
        timeSlotListToString(scheduleString, schedule.getRecurringSlots());
        scheduleString.append(LIST_SPLIT_CHAR);

        // For removed slots
        timeSlotListToString(scheduleString, schedule.getRemovedSlots());
        scheduleString.append(LIST_SPLIT_CHAR);

        // For added slots
        timeSlotListToString(scheduleString, schedule.getAddedSlots());

        return scheduleString.toString();
    }

    @Override
    public Schedule convertToEntityAttribute(String schedule) {
        if (schedule == null || schedule.isEmpty()) {
            return new Schedule();
        }

        String[] scheduleString = schedule.split(LIST_SPLIT_CHAR, -1);
        List<TimeSlot> recurringList = stringToTimeSlotList(scheduleString[0]);
        List<TimeSlot> removedList = stringToTimeSlotList(scheduleString[1]);
        List<TimeSlot> addedList = stringToTimeSlotList(scheduleString[2]);

        return new Schedule(recurringList, removedList, addedList);
    }

    private void timeSlotListToString(StringBuilder scheduleString,
                                          List<TimeSlot> slots) {
        for (TimeSlot slot : slots) {
            scheduleString.append(slot.getWeek())
                    .append(FIELD_SPLIT_CHAR).append(slot.getDay())
                    .append(FIELD_SPLIT_CHAR).append(slot.getTime().getFirst())
                    .append(FIELD_SPLIT_CHAR).append(slot.getTime().getSecond())
                    .append(SLOT_SPLIT_CHAR);
        }
    }

    private List<TimeSlot> stringToTimeSlotList(String s) {
        List<TimeSlot> slotList = new ArrayList<>();

        if (s.isEmpty()) {
            return slotList;
        }

        String[] scheduleString = s.split(SLOT_SPLIT_CHAR);

        for (String slotString : scheduleString) {
            List<String> slotFields = Arrays.asList(slotString.split(FIELD_SPLIT_CHAR));
            slotList.add(new TimeSlot(Integer.parseInt(slotFields.get(0)),
                    Day.valueOf(slotFields.get(1)),
                    Pair.of(Integer.parseInt(slotFields.get(2)), Integer.parseInt(slotFields.get(3)))));
        }
        return slotList;
    }
}
