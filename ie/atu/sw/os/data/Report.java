package ie.atu.sw.os.data;

import java.time.LocalDate;

import ie.atu.sw.os.Savable;

public class Report implements Savable{
	public static enum STATUS{
		OPEN("0PEN"), ASSIGNED("ASSIGNED"), CLOSED("CLOSED");
		private STATUS(final String text) {
		}
	}
	
	public Report(String appName, String platform, String descr) {
		this.appName = appName;
		this.platform = platform;
		this.descr = descr;
		this.date = LocalDate.now().toEpochDay();
	}
	public Report(String str) {
		int i = 0;
		String[] arrays = str.split(",");
		this.id = Integer.parseInt(arrays[i++]);
		this.appName = arrays[i++].replace(ESCAPE_CHAR, ',');
		this.platform = arrays[i++].replace(ESCAPE_CHAR, ',');
		this.descr = arrays[i++].replace(ESCAPE_CHAR, ',');
		this.status = STATUS.valueOf(arrays[i++]);
		this.date = Long.parseLong(arrays[i++]);
		this.assignee = arrays[i++].replace(ESCAPE_CHAR, ',');
	}
	private int id = -1;
	private String appName, platform, descr;
	private STATUS status = STATUS.OPEN;
	private long date;
	
	private String assignee = " "; // Must not be an empty string

	@Override
	public String asString() {
		StringBuilder sb = new StringBuilder()
		.append(id).append(",")
		.append(appName.replace(',', ESCAPE_CHAR)).append(",")
		.append(platform.replace(',', ESCAPE_CHAR)).append(",")
		.append(descr.replace(',', ESCAPE_CHAR)).append(",")
		.append(status).append(",")
		.append(date).append(",")
		.append(assignee.replace(',', ESCAPE_CHAR)).append(",\n");
		return sb.toString();
	}
	public void setId(int id) {
		this.id = id;
	}

}
