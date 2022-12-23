package nl.tudelft.sem.template.shared.converters;

import javax.persistence.AttributeConverter;
import nl.tudelft.sem.template.shared.domain.Node;

// Converter from a Pair of Integers being minutes to a String
// representing the time in the format HH:MM-HH:MM

public class TextTimeToMinutesConverter implements AttributeConverter<Node, String> {
    private static final String SPLIT_CHAR = "-";

    @Override
    public String convertToDatabaseColumn(Node time) {
        if (time == null) {
            return "";
        }
        return String.format("%02d:%02d", time.getFirst() / 60, time.getFirst() % 60)
                + SPLIT_CHAR
                + String.format("%02d:%02d", time.getSecond() / 60, time.getSecond() % 60);
    }

    @Override
    public Node convertToEntityAttribute(String time) {
        if (time == null || time.isEmpty()) {
            return null;
        }
        if (!time.matches("^[0-9]{2}:[0-9]{2}-[0-9]{2}:[0-9]{2}$")) {
            return null;
        }
        String[] times = time.split(SPLIT_CHAR);
        return new Node(Integer.parseInt(times[0].split(":")[0]) * 60
                + Integer.parseInt(times[0].split(":")[1]),
                Integer.parseInt(times[1].split(":")[0]) * 60
                        + Integer.parseInt(times[1].split(":")[1]));
    }

}