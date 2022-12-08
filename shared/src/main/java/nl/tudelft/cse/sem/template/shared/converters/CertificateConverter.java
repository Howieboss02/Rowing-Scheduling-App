package nl.tudelft.cse.sem.template.shared.converters;

import nl.tudelft.cse.sem.template.shared.enums.Certificate;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CertificateConverter implements AttributeConverter<Certificate, String> {
    // Convert from the enum to the database column
    @Override
    public String convertToDatabaseColumn(Certificate certificate) {
        if (certificate == null) {
            return null;
        }
        return certificate.name();
    }

    // Convert from the database column to the enum
    @Override
    public Certificate convertToEntityAttribute(String certificate) {
        if (certificate == null) {
            return null;
        }
        return Certificate.valueOf(certificate);
    }


}
