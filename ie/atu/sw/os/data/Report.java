package ie.atu.sw.os.data;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import ie.atu.sw.os.Savable;

/**
 * This class represents a Bug Report in the Application
 * @author intot
 *
 */
public class Report implements Savable, Serializable{
	/** 
	 * This Enumeration represents the different states the Report could be in
	 * @author intot
	 *
	 */
	public static enum STATUS{
		OPEN("0PEN"), ASSIGNED("ASSIGNED"), CLOSED("CLOSED");
		private STATUS(final String text) {
		}
	}
	
	/**
	 * Creates a new instance of Report with properties specified as parameters.
	 * @param appName - The name of the application in the Bug report.
	 * @param platform - The platform of the application in the Bug report.
	 * @param descr - The description of the Report.
	 */
	public Report(String appName, String platform, String descr) {
		this.appName = appName;
		this.platform = platform;
		this.descr = descr;
		this.date = LocalDateTime.now();
	}
	/**
	 * Creates a new instance of Report by decoding a string containing all 
	 * the properties of the report separated by a comma in CSV format.
	 * Special Escape characters to retrieve commas ',
	 * @param str - A string representation of the report in CSV format.
	 */
	public Report(String str) {
		int i = 0;
		String[] arrays = str.split(",");
		this.id = Integer.parseInt(arrays[i++]);
		this.appName = arrays[i++].replace(ESCAPE_CHAR, ',');
		this.platform = arrays[i++].replace(ESCAPE_CHAR, ',');
		this.descr = arrays[i++].replace(ESCAPE_CHAR, ',');
		this.status = STATUS.valueOf(arrays[i++]);
		this.date = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(arrays[i++])), ZoneId.systemDefault());
		this.assignee = arrays[i++].replace(ESCAPE_CHAR, ',');
//		System.out.println(date);
	}
	private int id = -1;
	private String appName, platform, descr;
	private STATUS status = STATUS.OPEN;
	private LocalDateTime date;
	
	private String assignee = " "; // Must not be an empty string

	@Override
	public String asString() {
		StringBuilder sb = new StringBuilder()
		.append(id).append(",")
		.append(appName.replace(',', ESCAPE_CHAR)).append(",")
		.append(platform.replace(',', ESCAPE_CHAR)).append(",")
		.append(descr.replace(',', ESCAPE_CHAR)).append(",")
		.append(status).append(",")
		.append(date.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli()).append(",")
		.append(assignee.replace(',', ESCAPE_CHAR)).append(",\n");
		return sb.toString();
	}
	/**
	 * A variation of @see Report#asString() that returns a formatted date for visual display
	 */
	public String asStandardString() {
		StringBuilder sb = new StringBuilder()
		.append(id).append(",")
		.append(appName.replace(',', ESCAPE_CHAR)).append(",")
		.append(platform.replace(',', ESCAPE_CHAR)).append(",")
		.append(descr.replace(',', ESCAPE_CHAR)).append(",")
		.append(status).append(",")
		.append(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append(",")
		.append(assignee.replace(',', ESCAPE_CHAR)).append(",\n");
		return sb.toString();
	}
	/**
	 * Accessor method to set the Id
	 * @param id - The id to assign to the report
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * Accessor method to get the Id
	 * @return - The Id of the report
	 */
	public int getId() {
		return this.id;
	}
	/**
	 * Accessor method to query the assigned status of the report
	 * @return - If the report has been assigned to  a user
	 */
	public boolean isAssigned() {
		return !assignee.equals(" ");
	}
	/**
	 * Accessor method to set the assignee of the report
	 * @param assignee - The id of the assignee
	 */
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	/**
	 * Accessor method to set the status of the report
	 * @param status - The status to set the reoport to
	 */
	public void setStatus(STATUS status) {
		this.status = status;
	}
}
