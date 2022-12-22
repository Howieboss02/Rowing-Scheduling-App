package nl.tudelft.sem.template.shared.converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import nl.tudelft.sem.template.shared.domain.Request;
import nl.tudelft.sem.template.shared.enums.PositionName;

@Converter
public class RequestConverter implements AttributeConverter<List<Request>, String> {

    private static final String OBJECT_SPLIT_CHAR = ";";
    private static final String FIELD_SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(List<Request> queue) {
        if (queue == null || queue.isEmpty()) {
            return "";
        }
        StringBuilder queueString = new StringBuilder();
        for (Request request : queue) {
            queueString.append(request.getName())
                .append(FIELD_SPLIT_CHAR)
                .append(request.getPosition())
                .append(OBJECT_SPLIT_CHAR);
        }
        return queueString.toString();
    }

    @Override
    public List<Request> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new ArrayList<>();
        }
        String[] queueString = dbData.split(OBJECT_SPLIT_CHAR);
        List<Request> queue = new ArrayList<>();
        for (String object : queueString) {
            List<String> requestFields = Arrays.asList(object.split(FIELD_SPLIT_CHAR));

            queue.add(new Request(requestFields.get(0), PositionName.valueOf(requestFields.get(1))));
        }
        return queue;
    }
}
