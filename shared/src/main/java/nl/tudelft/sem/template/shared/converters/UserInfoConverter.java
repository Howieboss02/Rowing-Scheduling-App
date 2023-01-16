package nl.tudelft.sem.template.shared.converters;

import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.domain.UserInfo;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.Day;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.List;

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

		return userInfo.getNetId() + SPLIT +
				userInfo.getName() + SPLIT +
				userInfo.getOrganization() + SPLIT +
				userInfo.getEmail() + SPLIT +
				userInfo.getGender() + SPLIT +
				userInfo.getCertificate();
	}

	@Override
	public UserInfo convertToEntityAttribute(String dbData) {
		if (dbData.equals("")) {
			return null;
		}

		List<String> userInfo = Arrays.asList(dbData.split(SPLIT));
		return new UserInfo(userInfo.get(0), userInfo.get(1),
				userInfo.get(2), userInfo.get(3), userInfo.get(4),
				Certificate.valueOf(userInfo.get(5)));
	}
}
