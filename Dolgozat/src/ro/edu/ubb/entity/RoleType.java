package ro.edu.ubb.entity;

/**
 * Enum for the types of user roles.
 * 
 * @author Nemet Orsolya
 *
 */
public enum RoleType {
	ADMINISTRATOR("ADMINISTRATOR"), USER("USER");
	private String role;

	RoleType(String role) {
		this.role = role;
	}

	public String getRoleType() {
		return role;
	}
}
