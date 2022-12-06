package nl.tudelft.cse.sem.template.shared.domain;

import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TimeSlotTest {

    @Test
    void intersect() {
        TimeSlot slot = new TimeSlot(1, 1, Pair.of(2, 10));
        List<Pair<Integer, Integer>> list = new ArrayList<>();

        list.add(Pair.of(1, 2));
        list.add(Pair.of(4, 5));
        list.add(Pair.of(8, 11));

        List<TimeSlot> result = new ArrayList<>();
        result.add(new TimeSlot(1, 1, Pair.of(4, 5)));
        result.add(new TimeSlot(1, 1, Pair.of(8, 10)));

        assertEquals(slot.intersect(list), result);

    }

    @Test
    void difference() {
        TimeSlot slot = new TimeSlot(1, 1, Pair.of(2, 10));
        List<Pair<Integer, Integer>> list = new ArrayList<>();

        list.add(Pair.of(1, 2));
        list.add(Pair.of(4, 5));
        list.add(Pair.of(8, 11));

        List<TimeSlot> result = new ArrayList<>();
        result.add(new TimeSlot(1, 1, Pair.of(2, 4)));
        result.add(new TimeSlot(1, 1, Pair.of(5, 8)));

        assertEquals(slot.difference(list), result);
    }
}