package shared.converters;


import shared.domain.Position;
import shared.enums.PositionName;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Converter
public class PositionsToFIllListConverter implements AttributeConverter<List<Position>, String> {
    private static final String OBJECT_SPLIT_CHAR = ";";
    private static final String FIELD_SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(List<Position> positions) {
        StringBuilder positionsString = new StringBuilder();
        for (Position position : positions) {
            positionsString.append(position.getName().toString()).append(FIELD_SPLIT_CHAR).append(position.isCompetitive()).append(OBJECT_SPLIT_CHAR);
        }
        return positionsString.toString();
    }

    @Override
    public List<Position> convertToEntityAttribute(String positions) {
        String[] positionsString = positions.split(OBJECT_SPLIT_CHAR);
        List<Position> positionsList = new ArrayList<>();
        for (String positionString : positionsString) {
            List<String> positionFields = Arrays.asList(positionString.split(FIELD_SPLIT_CHAR));
            positionsList.add(new Position(PositionName.valueOf(positionFields.get(0)), Boolean.parseBoolean(positionFields.get(1))));
        }
        return positionsList;
    }


}