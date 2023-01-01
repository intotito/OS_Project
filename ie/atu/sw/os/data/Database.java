package ie.atu.sw.os.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import ie.atu.sw.os.Savable;
import ie.atu.sw.os.User;

/**
 * This class accesses and writes to the underlying storage system of the
 * Application. Ordinary file with paths specified by the static variable
 * {@link #USER_FILE} and {@link #REPORT_FILE} are used to store the user and
 * report information.
 * 
 * @author intot
 *
 */
public class Database {
	/**
	 * The path to the file location where users information is stored
	 */
	private static String USER_FILE = "./user_repo.dat";
	/**
	 * The path to the file location where the reports are stored.
	 */
	private static String REPORT_FILE = "./report_repo.dat";
	// List of all reports
	private List<Report> records = new LinkedList<>();
	// List of all user
	private List<User> users = new LinkedList<>();

	/**
	 * Creates a new instance of the database. This constructor also loads all the
	 * values in the external storage to a list for the users and reports
	 * respectively.
	 * 
	 * @throws IOException
	 */
	public Database() throws IOException {
		load(REPORT_FILE, (s) -> records.add(new Report(s)));
		load(USER_FILE, (s) -> users.add(new User(s)));
	}

	/**
	 * Get the list of users.
	 * 
	 * @return - A list all users
	 */
	public List<User> getUsers() {
		// System.out.println(users.stream().collect(Collectors.toList()));
		return users.stream().collect(Collectors.toList());
	}
/**
 * Get the list of reports according to the parameter specified
 * @param code - 0 to get all reports, 1 to get only assigned reports
 * @return - A list of reports
 */
	public List<Report> getReports(int code) { // 0 - All reportt, 1 - Assigned
//		System.out.println(records.stream().collect(Collectors.toList()));
		return records.stream().filter((r) -> (code == 0 ? true : !r.isAssigned())).collect(Collectors.toList());
	}
/**
 * Loads information in the external storage to volatile memory.
 * @param FILE_PATH - The path to locate the file in the file storage
 * @param consumer - A {@link Consumer} functional interface that consumes each line of text from the file.
 * Could be specified as a lambda function.
 * @throws IOException - If anything goes wrong during file access
 */
	private void load(String FILE_PATH, Consumer<String> consumer) throws IOException {
		// System.out.println("Loaading what " + FILE_PATH);
		File file = new File(FILE_PATH);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str = null;
			while ((str = br.readLine()) != null) {
				consumer.accept(str);
			}
			br.close();
		} catch (FileNotFoundException fnf) {
			// System.out.println("File not there already, but proceed");
		}
	}
/**
 * Saves the current state in volatile memory to file storage.
 * @param toSave - Generic List of items that implements {@link Savable Savable}
 * @param FILE_PATH - File location inthefile system
 * @throws IOException - if anything goes wrong during file access
 */
	private void saveToFile(List<? extends Savable> toSave, String FILE_PATH) throws IOException {
		File file = new File(FILE_PATH);
		PrintWriter pr = new PrintWriter(new FileWriter(file, false));
		toSave.stream().forEach((s) -> pr.write(s.asString()));
		pr.close();
	}
/**
 * Add a new report to the Application. 
 * This information is saved to file storage as soon as it is added for reliability.
 * @param report - The report to be added to the Application
 * @return - The unique identification {@link Report#id for the report. 
 * @throws IOException - if anything goes wrong during file access
 */
	public int addReport(Report report) throws IOException {
		int code = -1;
		synchronized (records) {
			code = records.size();
			report.setId(code);
			records.add(report);
			saveToFile(records, REPORT_FILE);
		}
		return code;
	}
/**
 * Assigns a user to a report.
 * The information is saved to the file storage after assignment for reliability.
 * @param reportId - The unique identification of the report
 * @param userId - The unique identification of the user
 * @return response code of the assignment, 0 for Success; 1 for Record doesn't exist; 2 for User doesn't exist
 * @throws IOException - if anything goes wrong during file access
 */
	public int assign(int reportId, String userId) throws IOException {
		Report report = null;
		User user = null;
		int code = 0;
		synchronized (records) {
			report = records.stream().filter((r) -> r.getId() == reportId).findFirst().orElse(null);
			// System.out.println("Report is this for me assign: " + report);
		}
		if (report != null) {
			synchronized (users) {
				user = users.stream().filter((u) -> u.getId().equals(userId)).findFirst().orElse(null);
			}
			if (user != null) {
				report.setAssignee(userId);
				// System.out.println("WIll set " + reportId + " for " + userId + report);
				synchronized (records) {
					saveToFile(records, REPORT_FILE);
					records.clear();
					load(REPORT_FILE, (s) -> records.add(new Report(s)));
				}
			} else {
				code = 1;
			}
		} else {
			code = 1 << 1;
		}
		return code; // 0 -OK, 1 - Records doesn't exist, 2 - User doesn't exist
	}
/**
 * Updates a given report.
 * The information is saved to the file storage after update for reliability
 * @param reportId - The unique identification of the report
 * @param status - The {@link Report.STATUS STATUS} set the report
 * @return - response code of the update, 0 for Success; 1 for Incorrect Report ID
 * @throws IOException - if anything goes wrong during file access
 */
	public int updateReport(int reportId, Report.STATUS status) throws IOException {
		int code = 0;
		Report report = null;
		synchronized (records) {
			report = records.stream().filter((r) -> r.getId() == reportId).findFirst().orElse(null);
			// System.out.println("Report is this for me update: " + report);
		}
		if (report != null) {
			report.setStatus(status);
			// System.out.println("will set status " + report);
			synchronized (records) {
				saveToFile(records, REPORT_FILE);
				records.clear();
				load(REPORT_FILE, (s) -> records.add(new Report(s)));
			}
		} else {
			code = 1;
		}
		return code; // 0 - OK, 1 - Report ID icorrect
	}
/**
 * Attempts to login a given user
 * @param userID - The unique identification of the user
 * @return - Response code for the login operation, 0 for Doesn't exist; 1 for Successful
 */
	public int login(String userID) {
		long code = 0; // 0 - Doesn't Exist, 1 - OK (Found)
//		System.out.printf("\nChecking for userID: %s", userID);
		synchronized (users) {
			code = users.stream().filter((u) -> u.getId().equals(userID)).count();
		}
		return (int) code;
	}
/**
 * Attempts to register a user in the application.
 * The information is saved to the file storage after a successful attempt. 
 * @param user - The user to be registered
 * @return Response code for the register operation, 0 for Successful; 1 for Email already exist; 2 for ID already exist; 3 for Email and ID already exists
 * @throws IOException - if anything goes wrong during file access
 */
	public int register(User user) throws IOException {
		List<User> existing = null;
		synchronized (users) {
			existing = users.stream().filter(
					(u) -> (u.getId().equalsIgnoreCase(user.getId()) || u.getEmail().equalsIgnoreCase(user.getEmail())))
					.collect(Collectors.toList());
			if (existing.isEmpty()) {
				users.add(user);
				saveToFile(users, USER_FILE);
			}
		}
		int code = 0; // 0 - OK, 1 - Same Email, 2 - Same ID, 3 - Same Email & ID
		for (int i = 0; i < existing.size(); i++) {
			if (existing.get(i).getId().equalsIgnoreCase(user.getId())) {
				code += (1 << 1);
			}
			if (existing.get(i).getEmail().equalsIgnoreCase(user.getEmail())) {
				code += 1;
			}
		}
		return code;
	}
}
