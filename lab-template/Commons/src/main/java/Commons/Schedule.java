package Commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

public class Schedule {
    /**
     * For each of the 7 days of the week we store
     * a list of time intervals in seconds
     */
    List<List<Pair<Integer, Integer>>> recurringSlots;
    /**
     * Recurring slots that were temporarily removed
     * (for a specific week)
     */
    List<TimeSlot> removedSlots;
    /**
     * Slots that were temporarily added
     * (for a specific week)
     */
    List<TimeSlot> addedSlots;

    /**
     * Empty constructor for the Schedule class
     */
    public Schedule() {
        recurringSlots = new ArrayList<>(7);
        for (int i  = 0; i < 7; ++ i)
            recurringSlots.set(i, new ArrayList<>());

        removedSlots = new ArrayList<>();
        addedSlots = new ArrayList<>();
    }

    /**
     * Add a recurring slot
     * @param day the day of the slot (values from 0 to 6)
     * @param time the time interval in seconds of the slot
     */
    public void addRecurringSlot(Integer day, Pair<Integer, Integer> time) {
        if (day > 6 || day < 0) return;
        recurringSlots.get(day).add(time);
    }

    /**
     * Temporarily removes slot
     * @param slot the time slot that should be temporarily removed
     */
    public void removeSlot(TimeSlot slot) {
        // TODO: Only remove parts of slot that were actually filled in recurring table
        removedSlots.add(slot);
    }

    /**
     * Temporarily adds slot
     * @param slot the time slot that should be temporarily added
     */
    public void addSlot(TimeSlot slot) {
        // TODO: Only add parts of slot that were not already filled in recurring table
        addedSlots.add(slot);
    }

    /**
     * Remove added/removed slots from lists that are older
     * than the current week
     * @param currentWeek the current week
     */
    public void cleanSlots(int currentWeek) {
        // TODO: use getter
        Predicate<TimeSlot> condition = a -> a.week >= currentWeek;
        removedSlots =
                removedSlots.stream().filter(condition).toList();
        addedSlots =
                addedSlots.stream().filter(condition).toList();
    }

    /**
     * A method that uses an API supportive version of the "equals" method.
     *
     * @param obj a random type of object to be compared to
     * @return a boolean whether these 2 objects are the same or not
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * A method that uses an API supportive version of hashing.
     *
     * @return a hash code of the Schedule object
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * A method that uses an API supportive method of transforming data into a string.
     *
     * @return a string containing every detail about the schedule
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
