package nl.tudelft.sem.template.shared.domain;

import nl.tudelft.sem.template.shared.enums.Day;
import org.springframework.data.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TimeSlot {
    private Integer week;
    private Day day;
    private Pair<Integer, Integer> time;

    /**
     * Calculate the intersection between the time of this timeslot
     * and a list of time intervals
     * @param schedule the list of time intervals
     * @return a list of timeslots representing the intersection
     */
    public List<TimeSlot> intersect(List<TimeSlot> schedule) {
        List<TimeSlot> intersection = new ArrayList<>();
        for (TimeSlot entry : schedule) {
            Pair<Integer, Integer> time = entry.getTime();
            if (time.getFirst() < this.time.getSecond() &&
                    time.getSecond() > this.time.getFirst()) {
                intersection.add(new TimeSlot(week, day,
                        Pair.of(Integer.max(time.getFirst(), this.time.getFirst()),
                                Integer.min(time.getSecond(), this.time.getSecond())
                        )));
            }
        }
        return intersection;
    }

    /**
     * Calculate the difference between the time of this timeslot
     * and a list of time intervals (the time intervals that appear
     * in the timeslot but not in the given list)
     * @param schedule the list of time intervals
     * @return a list of timeslots representing the intersection
     */
    public List<TimeSlot> difference(List<TimeSlot> schedule) {
        List<TimeSlot> intersection = this.intersect(schedule);
        List<TimeSlot> difference = new ArrayList<>();

        Integer begin = this.time.getFirst();
        for (TimeSlot slot : intersection) {
            if (begin < slot.getTime().getFirst())
                difference.add(new TimeSlot(week, day,
                        Pair.of(begin, slot.getTime().getFirst())));

            begin = slot.getTime().getSecond();
        }
        if (begin < this.time.getSecond())
            difference.add(new TimeSlot(week, day,
                    Pair.of(begin, this.time.getSecond())));
        return difference;
    }
}