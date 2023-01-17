package nl.tudelft.sem.template.shared.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.sem.template.shared.enums.Certificate;
import org.apache.commons.lang3.builder.EqualsBuilder;

@Getter
@Setter
@AllArgsConstructor
public class UserInfo {
	private String netId;
	private String name;
	private String organization;
	private String email;
	private String gender;
	private Certificate certificate;

	/**
	 * Equals method for UserInfo
	 * @param obj the object to compare to
	 * @return true iff objects are equal
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj) && obj instanceof UserInfo;
	}
}
