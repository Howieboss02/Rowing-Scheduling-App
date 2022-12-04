package Commons;

import org.springframework.data.util.Pair;

public class TimeSlot {
    // The number of the week of the current year
    Integer week;
    // Day of the week (values from 0 to 6)
    Integer day;
    // Time interval in seconds
    Pair<Integer, Integer> time;
}
