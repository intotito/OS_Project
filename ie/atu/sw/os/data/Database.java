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

public class Database {
	private static String USER_FILE = "./user_repo.dat";
	private static String REPORT_FILE = "./report_repo.dat";
	private List<Report> records = new LinkedList<>();
	private List<User> users = new LinkedList<>();

	public Database() throws IOException {
		load(REPORT_FILE, (s) -> records.add(new Report(s)));
		load(USER_FILE, (s) -> users.add(new User(s)));
	}

	public List<User> getUsers(){
	//	System.out.println(users.stream().collect(Collectors.toList()));
		return users.stream().collect(Collectors.toList());
	}
	
	public List<Report> getReports(int code){ // 0 - All reportt, 1 - Assigned
//		System.out.println(records.stream().collect(Collectors.toList()));
		return records.stream().filter((r) -> (code == 0 ? true : !r.isAssigned())).collect(Collectors.toList());
	}

	private void load(String FILE_PATH, Consumer<String> consumer) throws IOException {
	//	System.out.println("Loaading what " + FILE_PATH);
		File file = new File(FILE_PATH);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str = null;
			while ((str = br.readLine()) != null) {
				consumer.accept(str);
			}
			br.close();
		} catch (FileNotFoundException fnf) {
		//	System.out.println("File not there already, but proceed");
		}
	}

	private void saveToFile(List<? extends Savable> toSave, String FILE_PATH) throws IOException {
		File file = new File(FILE_PATH);
		PrintWriter pr = new PrintWriter(new FileWriter(file, false));
		toSave.stream().forEach((s) -> pr.write(s.asString()));
		pr.close();
	}

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

	public int assign(int reportId, String userId) throws IOException {
		Report report = null;
		User user = null;
		int code = 0;
		synchronized (records) {
			report = records.stream().filter((r) -> r.getId() == reportId).findFirst().orElse(null);
		//	System.out.println("Report is this for me assign: " + report);
		}
		if (report != null) {
			synchronized (users) {
				user = users.stream().filter((u) -> u.getId().equals(userId)).findFirst().orElse(null);
			}
			if (user != null) {
				report.setAssignee(userId);
		//		System.out.println("WIll set " + reportId + " for " + userId + report);
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
	
	public int updateReport(int reportId, Report.STATUS status) throws IOException {
		int code = 0;
		Report report = null;
		synchronized(records) {
			report = records.stream().filter((r) -> r.getId() == reportId).findFirst().orElse(null);
	//		System.out.println("Report is this for me update: " + report);
		}
		if(report != null) {
			report.setStatus(status);
	//		System.out.println("will set status " + report);
			synchronized(records) {
				saveToFile(records, REPORT_FILE);
				records.clear();
				load(REPORT_FILE, (s) -> records.add(new Report(s)));
			}
		} else {
			code = 1;
		}
		return code; // 0 - OK, 1 - Report ID icorrect
	}

	public int login(String userID) {
		long code = 0; // 0 - Doesn't Exist, 1 - OK (Found)
//		System.out.printf("\nChecking for userID: %s", userID);
		synchronized (users) {
			code = users.stream().filter((u) -> u.getId().equals(userID)).count();
		}
		return (int) code;
	}

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
