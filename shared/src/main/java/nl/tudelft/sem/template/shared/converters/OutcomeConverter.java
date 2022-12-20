package nl.tudelft.sem.template.shared.converters;

import nl.tudelft.sem.template.shared.enums.Outcome;

import javax.persistence.AttributeConverter;

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
        if(dbData.isEmpty() || dbData == null) return null;
        return Outcome.valueOf(dbData);
    }
}
