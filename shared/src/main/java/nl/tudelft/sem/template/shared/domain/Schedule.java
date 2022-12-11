package nl.tudelft.sem.template.shared.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
public class Schedule {
    /**
     * List of recurring slots
     */
    private List<TimeSlot> recurringSlots;
    /**
     * Recurring slots that were removed for a specific week
     */
    private List<TimeSlot> removedSlots;
    /**
     * Slots that were added for a specific week
     */
    private List<TimeSlot> addedSlots;

    /**
     * Empty constructor for the Schedule class
     */
    public Schedule() {
        recurringSlots = new ArrayList<>();
        removedSlots = new ArrayList<>();
        addedSlots = new ArrayList<>();
    }

    /**
     * Add a recurring slot
     * @param slot the slot to add
     */
    public void addRecurringSlot(TimeSlot slot) {
        recurringSlots.add(slot);
    }

    /**
     * Removes slot from the recurring slots for a specific week
     * @param slot the time slot that should be temporarily removed
     */
    public void removeSlot(TimeSlot slot) {
        Predicate<TimeSlot> condition = a -> a.getDay().equals(slot.getDay());
        List<TimeSlot> toAdd = slot.intersect(
                recurringSlots.stream().filter(condition).collect(Collectors.toList())
        );
        removedSlots.addAll(toAdd);
    }

    /**
     * Adds a slot that is not recurring for a specific week
     * @param slot the time slot that should be temporarily added
     */
    public void addSlot(TimeSlot slot) {
        Predicate<TimeSlot> condition = a -> a.getDay().equals(slot.getDay());
        List<TimeSlot> toAdd = slot.difference(
                recurringSlots.stream().filter(condition).collect(Collectors.toList())
        );
        addedSlots.addAll(toAdd);
    }

    /**
     * Remove added/removed slots from lists that are older
     * than the current week
     * @param currentWeek the current week
     */
    public void cleanSlots(int currentWeek) {
        Predicate<TimeSlot> condition = a -> a.getWeek() >= currentWeek;
        removedSlots =
                removedSlots.stream().filter(condition).collect(Collectors.toList());
        addedSlots =
                addedSlots.stream().filter(condition).collect(Collectors.toList());
    }
}
