package ie.atu.sw.os;

import java.io.Serializable;

/**
 * This class maps to a single employee on the database. It encapsulates the
 * properties and possible actions of a given user of the system.
 * 
 * @author intot
 *
 */
public class User implements Savable, Serializable {
	/**
	 * Eclipse Auto generated Number
	 */
	private static final long serialVersionUID = -3609332166721774386L;

	/**
	 * Convenient toString method for debugging
	 */
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", dept=" + dept + "]";
	}

	/**
	 * Id of the User
	 */
	private String id;
	/**
	 * Name of the User
	 */
	private String name;
	/**
	 * Email of the User
	 */
	private String email;
	/**
	 * Department of the User
	 */
	private String dept;

	/**
	 * Accessor method to get the Id
	 * @return - The Id of the User
	 */
	public String getId() {
		return id;
	}
/**
 * Accessor method to set the Id
 * @param id - The id to assign to the user
 */
	public void setId(String id) {
		this.id = id;
	}
/**
 * Accessor method to get the Name
 * @return - The name of the User
 */
	public String getName() {
		return name;
	}
/**
 * Accessor method to set the Name
 * @param name - The name to assign to the user
 */
	public void setName(String name) {
		this.name = name;
	}
/**
 * Accessor method to get the email
 * @return - The email of the user
 */
	public String getEmail() {
		return email;
	}
/**
 * Accessor method to set the Email
 * @param email - The email to assign to the user
 */
	public void setEmail(String email) {
		this.email = email;
	}
/**
 * Accessor mthod to get the department
 * @return - The department of the user
 */
	public String getDept() {
		return dept;
	}
/**
 * Accessor method to set the department
 * @param dept - The department to assign to the user
 */
	public void setDept(String dept) {
		this.dept = dept;
	}
/**
 * Creates a new instance of a user using the passed in parameter.
 * The parameter is stripped of Special Escape Character {@link Savable#ESCAPE_CHAR} if present.
 * @param str - A string containing the properties of the user in csv format.
 */
	public User(String str) {
		String[] arrays = str.split(",");
		this.id = arrays[0].replace(ESCAPE_CHAR, ',');
		this.name = arrays[1].replace(ESCAPE_CHAR, ',');
		this.email = arrays[2].replace(ESCAPE_CHAR, ',');
		this.dept = arrays[3].replace(ESCAPE_CHAR, ',');
	}
/**
 * Creates a new instance of a user with the properties passed in as parameters.
 * @param id - The id of the user 
 * @param name - The name of the user
 * @param email - The email of the user
 * @param dept - The department of the user
 */
	public User(String id, String name, String email, String dept) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.dept = dept;
	}

	@Override
	/**
	 * @return - CSV representation of a user.
	 */
	public String asString() {
		StringBuilder sb = new StringBuilder();
		sb.append(id.replace(',', ESCAPE_CHAR)).append(",").append(name.replace(',', ESCAPE_CHAR)).append(",")
				.append(email.replace(',', ESCAPE_CHAR)).append(",").append(dept.replace(',', ESCAPE_CHAR))
				.append("\n");
		return sb.toString();
	}

}
