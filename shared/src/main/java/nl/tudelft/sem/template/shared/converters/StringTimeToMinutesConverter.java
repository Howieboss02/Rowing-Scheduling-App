package nl.tudelft.sem.template.shared.converters;

import javax.persistence.AttributeConverter;

public class StringTimeToMinutesConverter implements AttributeConverter<String, Integer> {

        @Override
        public Integer convertToDatabaseColumn(String time) {
            if (time == null || time.isEmpty()) {
                return null;
            }
            if (!time.matches("^[0-9]{2}:[0-9]{2}$")) {
                return null;
            }
            return Integer.parseInt(time.split(":")[0]) * 60
                    + Integer.parseInt(time.split(":")[1]);
        }

        @Override
        public String convertToEntityAttribute(Integer time) {
            if (time == null) {
                return "";
            }
            return String.format("%02d:%02d", time / 60, time % 60);
        }
}
