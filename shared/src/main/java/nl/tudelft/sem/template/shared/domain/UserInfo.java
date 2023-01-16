package nl.tudelft.sem.template.shared.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import nl.tudelft.sem.template.shared.enums.Certificate;

@Data
@AllArgsConstructor
public class UserInfo {
	private String netId;
	private String name;
	private String organization;
	private String email;
	private String gender;
	private Certificate certificate;
}
