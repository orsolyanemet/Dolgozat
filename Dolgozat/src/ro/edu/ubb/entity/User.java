package ro.edu.ubb.entity;

import java.io.Serializable;

/**
 * Entity for the users.
 * 
 * @author Nemet Orsolya
 *
 */
public class User implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer idUser;

	private String firstName;

	private String lastName;

	private String email;

	private String username;

	private String pdUser;

	private RoleType roleType;
	
	public User() {
		super();
	}
	
	public User(Integer idUser, String firstname, String lastname, String email, String username, String pdUser, RoleType roleType) {
		super();
		this.idUser = idUser;
		this.firstName = firstname;
		this.lastName = lastname;
		this.email = email;
		this.username = username;
		this.pdUser = pdUser;
		this.roleType = roleType;
	}

	public Integer getIdUser() {
		return idUser;
	}

	public void setIdUser(Integer idUser) {
		this.idUser = idUser;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPdUser() {
		return pdUser;
	}

	public void setPdUser(String pdUser) {
		this.pdUser = pdUser;
	}

	public RoleType getRoleType() {
		return roleType;
	}

	public void setRoleType(RoleType roleType) {
		this.roleType = roleType;
	}

	@Override
	public String toString() {
		return "User [idUser=" + idUser + ", firstname=" + firstName + ", lastname=" + lastName + ", email=" + email
				+ ", username=" + username + ", password=" + pdUser  + ", roleType="
				+ roleType + "]";
	}

}
