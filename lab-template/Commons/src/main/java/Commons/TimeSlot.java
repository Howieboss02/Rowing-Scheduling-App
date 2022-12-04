package Commons;

import org.springframework.data.util.Pair;

public class TimeSlot {
    Integer week;
    Integer day;
    Pair<Integer, Integer> time;

    /**
     * Constructor for TimeSlot
     * @param week number of the week of current year
     * @param day day of the week (values from 0 to 6)
     * @param time time interval in seconds
     */
    public TimeSlot(Integer week, Integer day, Pair<Integer, Integer> time) {
        this.week = week;
        this.day = day;
        this.time = time;
    }

}
