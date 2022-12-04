package Commons;

import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import static org.junit.jupiter.api.Assertions.*;

class TimeSlotTest {
    @Test
    void testConstructor() {
        TimeSlot t = new TimeSlot(1, 1, Pair.of(1, 2));
        assertEquals(t.week, 1);
        assertEquals(t.day, 1);
        assertEquals(t.time, Pair.of(1, 2));
    }
}