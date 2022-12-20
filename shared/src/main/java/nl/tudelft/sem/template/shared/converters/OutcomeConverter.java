package nl.tudelft.sem.template.shared.converters;

import javax.persistence.AttributeConverter;
import nl.tudelft.sem.template.shared.enums.Outcome;

public class OutcomeConverter implements AttributeConverter<Outcome, String> {

    @Override
    public String convertToDatabaseColumn(Outcome attribute) {
        if (attribute == null) {
            return "";
        }
        return attribute.toString();
    }

    @Override
    public Outcome convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return Outcome.valueOf(dbData);
    }
}
