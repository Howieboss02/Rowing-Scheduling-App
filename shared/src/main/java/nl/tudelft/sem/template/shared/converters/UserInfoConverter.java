package nl.tudelft.sem.template.shared.converters;

import java.util.Arrays;
import java.util.List;
import javax.persistence.AttributeConverter;
import nl.tudelft.sem.template.shared.domain.UserInfo;
import nl.tudelft.sem.template.shared.enums.Certificate;

public class UserInfoConverter implements AttributeConverter<UserInfo, String> {
    private static final String SPLIT = ",";

    /**
     * Converts UserInfo to a string.
     *
     * @param userInfo the entity attribute value to be converted
     * @return a string representing the given schedule
     */
    @Override
    public String convertToDatabaseColumn(UserInfo userInfo) {
        if (userInfo == null) {
            return "";
        }

        String netId = userInfo.getNetId() == null ? "" : userInfo.getNetId();
        String name = userInfo.getName() == null ? "" : userInfo.getName();
        String org = userInfo.getOrganization() == null ? "" : userInfo.getOrganization();
        String email = userInfo.getEmail() == null ? "" : userInfo.getEmail();
        String gender = userInfo.getGender() == null ? "" : userInfo.getGender();
        String cert = userInfo.getCertificate() == null ? "" : userInfo.getCertificate().toString();

        return netId + SPLIT + name + SPLIT + org + SPLIT + email + SPLIT
		        + gender + SPLIT + cert;
    }

    @Override
    public UserInfo convertToEntityAttribute(String dbData) {
        if (dbData.equals("")) {
            return null;
        }

        List<String> userInfo = Arrays.asList(dbData.split(SPLIT, -1));
        try {
            return new UserInfo(userInfo.get(0), userInfo.get(1),
                    userInfo.get(2), userInfo.get(3), userInfo.get(4),
                    Certificate.valueOf(userInfo.get(5)));
        } catch (Exception e) {
            return new UserInfo(userInfo.get(0), userInfo.get(1),
                    userInfo.get(2), userInfo.get(3), userInfo.get(4),
                    null);
        }

    }
}
