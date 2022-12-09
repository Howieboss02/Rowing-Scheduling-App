package nl.tudelft.sem.template.authentication.domain.user;


import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA Converter for the Email value object.
 */
@Converter
public class EmailConverter implements AttributeConverter<Email, String> {

    @Override
    public String convertToDatabaseColumn(Email email) {
        return email.toString();
    }

    @Override
    public Email convertToEntityAttribute(String dbData) {
        try {
            return new Email(dbData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
