package ie.atu.sw.os;

import java.io.Serializable;

public class User implements Savable, Serializable{
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", dept=" + dept + "]";
	}

	private String id, name, email, dept;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public User(String str) {
		String[] arrays = str.split(",");
		this.id = arrays[0].replace(ESCAPE_CHAR, ',');
		this.name = arrays[1].replace(ESCAPE_CHAR, ',');
		this.email = arrays[2].replace(ESCAPE_CHAR, ',');
		this.dept = arrays[3].replace(ESCAPE_CHAR, ',');
	}
	
	public User(String id, String name, String email, String dept) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.dept = dept;
	}

	@Override
	public String asString() {
		StringBuilder sb = new StringBuilder();
		sb.append(id.replace(',', ESCAPE_CHAR)).append(",")
		.append(name.replace(',', ESCAPE_CHAR)).append(",")
		.append(email.replace(',', ESCAPE_CHAR)).append(",")
		.append(dept.replace(',', ESCAPE_CHAR)).append("\n");
		return sb.toString();
	}
	
}
