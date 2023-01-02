package ie.atu.sw.os.reqres;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import ie.atu.sw.os.Savable;
import ie.atu.sw.os.User;
import ie.atu.sw.os.data.Report;
import ie.atu.sw.os.exception.MenuCancelException;
import ie.atu.sw.os.exception.MyException;
import ie.atu.sw.os.server.Server;
/**
 * This class represents a response sent from the server to the client and 
 * contains information about the result of prior request sent by the client.
 * It implements the {@link Serializable} interface and
 * thus can be deconstructed and reconstructed while being sent as packets.
 * 
 * @author intot
 *
 */
public abstract class Response implements Serializable, Formatter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6974337194609606334L;
	private String message;
	protected String[] options;
/**
 * Constructs a message in the form of option menus to be displayed to the user
 * @param indent - The indentation to construct the message with
 */
	protected void buildMessage(int indent) {
		StringBuilder sb = new StringBuilder();
//		sb.append(getHeaderAsString());
//		Arrays.stream(options).forEach(System.out::println);

		sb.append(getStandardOptionsAsString(options, indent));
		setMessage(sb.toString());
	}
	/**
	 * Gets the name of the Response
	 * @return - The response name as a String
	 */
	public String getName() {
		return this.getClass().getCanonicalName().substring(getClass().getCanonicalName().lastIndexOf('.') + 1);
	}
	/**
	 * Specifies the type of exception this response throws
	 * @see ie.atu.sw.os.exception
	 * @return - The exception to be thrown
	 */
	public MyException getException() {
		return new MenuCancelException();
	}
/**
 * This method is ran on the client side of the Application and performs the necessary
 * actions on the response according the result of the prior request.
 * @return - Next request from the client after processing the response
 * @throws MyException - If cancel option is selected while generating the next request
 */
	public Request process() throws MyException {
		// System.out.println(this.getClass().getName() + " Calling process");
		System.out.print(getMessage());
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			int value = -1;
			String valString = null;
			do {
				try {
					valString = reader.readLine().trim();
					value = Integer.parseInt(valString);
				} catch (NumberFormatException nfe) {
			//		System.out.format("Invalid Option '%s' Entered%s", valString, getSelectionsAsString(options));
					Formatter.printError(String.format("Invalid Option '%s' Entered", valString), 0);
					System.out.print("\n" + getStandardSelectionAsString(options, 0));
					continue;
				}
				if (hasCancelOption() && (value == options.length + 1)) {
					throw getException();
				}
				if (value < 1 || value > options.length) {
		//			System.out.format("Invalid Option '%d' Entered%s", value, getSelectionsAsString(options));
					Formatter.printError(String.format("Invalid Option '%d' Entered", value), 0);
					System.out.print("\n" + getStandardSelectionAsString(options, 0));
					continue;
				}
			} while (value < 1 || value > options.length);
			// System.out.println("VAlue %%%%%%%%%%%%:::::::: " + options[value - 1]);
			return Request.getRequest(options[value - 1]);
		} catch (IOException e) {
	//		e.printStackTrace();
		}
		throw new IllegalStateException("What went wrong");
	}
	/**
	 * A factory method for retrieving a type of response.
	 * @param req - String representation of the type of response to retrieve
	 * @return - The Response specified
	 * @throws IOException - If anything goes wrong
	 */
	public static Response getResponse(String res) {
		if (res.toLowerCase().matches("connect\\d")) {
			return new Connect(Integer.parseInt(res.substring("connect".length())));
		} else if (res.toLowerCase().matches("register\\d")) {
			return new Register(Integer.parseInt(res.substring("register".length())));
		} else if (res.toLowerCase().matches("login\\d")) {
			return new Login(Integer.parseInt(res.substring("login".length())));
		} else if (res.toLowerCase().matches("add\\d")) {
			return new AddReport(Integer.parseInt(res.substring("add".length())));
		} else if (res.toLowerCase().matches("assign\\d")) {
			return new Assign(Integer.parseInt(res.substring("assign".length())));
		} else if (res.toLowerCase().matches("update\\d")) {
			return new Update(Integer.parseInt(res.substring("update".length())));
		} else if (res.toLowerCase().matches("users")) {
			// System.out.println("Will return new USers response");
			var a = new Users();
			// System.out.println("Check this: " + a);
			return a;
		} else if (res.toLowerCase().matches("reports\\d")) {
			return new Reports(Integer.parseInt(res.substring("reports".length())));
		}
		throw new IllegalArgumentException("Unknown Response type " + res);
	}
	/**
	 * A response that shows the user the initial menu options of the Application
	 * @see #Response
	 * @author intot
	 *
	 */
	public static class Connect extends Response {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4336548764913148708L;
		public static final int REG_LOGING = 0, MAIN_MENU = 1, VIEW_MENU = 2;
		int code;

		private Connect(int code) {
		//	System.out.println(this.getClass().getName());
			this.code = code;
//			System.out.println("Connect code: " + code);
			String[][] optionSuite = { { "Register", "Login" }, Server.MAIN_MENU, { "Reports", "Users" } };
			this.options = optionSuite[code];
			// Arrays.stream(options).forEach(System.out::println);
			buildMessage(0);
		}

		@Override
		public String getHeaderAsString() {
			return String.format("\nConnection Established with Server at port %d", Server.PORT);
		}

		@Override
		public String getDefaultCancelString() {
			return new String[] { "Exit", "Logout", "Logout" }[code];
		}

		@Override
		public boolean hasCancelOption() {
			return true;
		}

		@Override
		public MyException getException() {
	//		System.out.println("My code: " + code);
			return code == 0 ? (new MyException() {

				/**
				 * 
				 */
				private static final long serialVersionUID = -7269231705778976363L;
			}) : new MenuCancelException();
		}

		@Override
		public Request process() throws MyException {
			Formatter.printBoxed(
					code == 0 ? ("     MENU     ") : (code == 1 ? ("     MAIN MENU     ") : ("   VIEW     ")), 0, '+',
					'|', '-');

			return super.process();
		}
	}
	/**
	 * A response as a result of a {@link #Request.AddReport AddReport} request
	 * @see #Response
	 * @author intot
	 *
	 */
	public static class Reports extends Response {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3784542953419176453L;
		private int code;
		private List<Report> reports;

		private Reports(int code) {
			this.code = code;
			options = Server.MAIN_MENU;
//			System.out.println("Construction finished");
			buildMessage(0);

		}

		@Override
		public String getHeaderAsString() {
			return String.format("%s\n%s", code == 0 ? "All Reports" : "Unassigned Reports", showReports());
		}

		private String showReports() {
			return reports.toString();
		}

		public void loadReports(List<Report> reports) {
//			System.out.println("Loading reports");
//			reports.stream().map(Report::toString).forEach(System.out::println);
			this.reports = reports;
			buildMessage(0);
		}

		@Override
		public Request process() throws MyException {
			String[] title = { "Id", "App. Name", "Platform", "Description", "Status", "Date", "Asignee" };
			int[] ratios = { 2, 6, 7, 15, 4, 7, 6 };
			Function<Integer, String[]> function = (i) -> {
				String[] arrays = Arrays.stream(reports.get(i).asStandardString().split(",")).map((String s) -> {
					return s.replace(Savable.ESCAPE_CHAR, ',');
				}).toArray(String[]::new);
				return arrays;

			};
			Formatter.printTabular(title, reports.size(), function, ratios, 0, '+', '|', '-');
			Formatter.printBoxed("     MAIN MENU     ", 0, '+', '|', '-');
			return super.process();
		}
	}
	/**
	 * A response as a result of a {@link #Request.View View} request.
	 * Displays the list of users to the user
	 * @see #Response
	 * @author intot
	 *
	 */
	public static class Users extends Response {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7773466124451337928L;
		private List<User> users;

		private Users() {

			options = Server.MAIN_MENU;
			// buildMessage(0);
		}

		@Override
		public String getHeaderAsString() {
			return String.format("Registered Users\n%s", showUsers());
		}

		public void loadUsers(List<User> users) {
			// System.out.println("Loading Users");
			// users.stream().map(User::toString).forEach(System.out::println);
			this.users = users;
		}

		private String showUsers() {
//			System.out.println("How's calling me?");
			return users.toString();
		}

		@Override
		public Request process() throws MyException {
			buildMessage(0);
			String[] title = { "Id", "Name", "Email", "Department" };
			int[] ratios = { 8, 12, 12, 12 };
			Function<Integer, String[]> function = (i) -> {
				return Arrays.stream(users.get(i).asString().split(",")).map((s) -> {
					return s.replace(Savable.ESCAPE_CHAR, ',');
				}).toArray(String[]::new);
			};
			Formatter.printTabular(title, users.size(), function, ratios, 0, '+', '|', '-');
			Formatter.printBoxed("     MAIN MENU     ", 0, '+', '|', '-');
			return super.process();
		}
	}
	/**
	 * A response as a result of a {@link #Request.Login Login} request.
	 * Contains code to verify if the login was successful or not
	 * @see #Response
	 * @author intot
	 *
	 */
	public static class Login extends Response {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5227850576168707492L;
		private int status;

		private Login(int status) {
			this.status = status;
			// System.out.println("Attempted Login Successful with: " + status);
			if (this.status == 1) {
				options = Server.MAIN_MENU;
				buildMessage(0);
			} else {
				setMessage(String.format("\nLogin Unsuccessfull\nInvalid User ID"));
			}
		}

		@Override
		public String getHeaderAsString() {
			return "\nLogin Successful";
		}

		@Override
		public Request process() throws MyException {
			if (status == 1) { // Login Successful
				Formatter.printBoxed("  Login Successfull  ", 1, '+', '|', '-');
				return Response.getResponse("connect1").process();
			} else {
				// System.out.print(getMessage());
				Formatter.printError("Login Unsuccessfull: Invalid user Id", 1);
				return Response.getResponse("connect0").process();
			}
		}

		@Override
		public String getDefaultCancelString() {
			return "Exit";
		}

		@Override
		public boolean hasCancelOption() {
			return true;
		}
	}
	/**
	 * A response as a result of a {@link #Request.Update Update} request.
	 * Contains code to verify if the update was successful
	 * @see #Response
	 * @author intot
	 *
	 */
	public static class Update extends Response {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6012676569929338739L;
		private int code;

		private Update(int code) {
			this.code = code;
			options = Server.MAIN_MENU;
			buildMessage(0);
		}

		@Override
		public String getHeaderAsString() {
			return "Report updated Successfully!";
		}

		@Override
		public String getDefaultCancelString() {
			return "Logout";
		}

		@Override
		public boolean hasCancelOption() {
			return true;
		}

		@Override
		public Request process() throws MyException {
			String s = code == 0 ? ("Report Updated Successfully") : ("Report does not exists");
			if (code == 0) {
				Formatter.printBoxed(s, 1, '+', '|', '-');
			} else {
				Formatter.printError(s, 1);
			}
			Formatter.printBoxed("     MAIN MENU     ", 0, '+', '|', '-');
			return super.process();
		}
	}
	/**
	 * A response as a result of a {@link #Request.Assign Assign} request.
	 * Contains code to verify if the assignment was successful or not
	 * @see #Response
	 * @author intot
	 *
	 */
	public static class Assign extends Response {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7601101355773724267L;
		private int code;

		private Assign(int code) {
			this.code = code;
			options = Server.MAIN_MENU;
			buildMessage(0);
		}

		@Override
		public String getHeaderAsString() {
			return "User assigned to the Report Successfully!";
		}

		@Override
		public String getDefaultCancelString() {
			return "Logout";
		}

		@Override
		public boolean hasCancelOption() {
			return true;
		}

		@Override
		public Request process() throws MyException {
			String s = code == 0 ? ("Report Assigned Successfully")
					: (code == 1 ? ("Invalid User name") : ("Report does not exist"));
			if (code == 0) {
				Formatter.printBoxed(s, 1, '+', '|', '-');
			} else {
				Formatter.printError(s, 1);
			}
			Formatter.printBoxed("     MAIN MENU     ", 0, '+', '|', '-');
			return super.process();
		}
	}
	/**
	 * A response as a result of a {@link #Request.AddReport AddReport} request.
	 * Contains code to verify if the report was added was successful or not
	 * @see #Response
	 * @author intot
	 *
	 */
	public static class AddReport extends Response {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7635944868527004361L;
		private int id;

		private AddReport(int id) {
			this.id = id;
			options = Server.MAIN_MENU;
			buildMessage(0);
		}

		@Override
		public String getHeaderAsString() {
			return String.format("Report added successfully. ID: '%d'", id);
		}

		@Override
		public String getDefaultCancelString() {
			return "Quit";
		}

		@Override
		public boolean hasCancelOption() {
			return true;
		}

		@Override
		public Request process() throws MyException {
			Formatter.printBoxedTitled("Report Added Successfully", String.format("ID: '%d'", id), 1, '+', '|', '-', 1);
			Formatter.printBoxed("     MAIN MENU     ", 0, '+', '|', '-');
			return super.process();
		}
	}
	/**
	 * A response as a result of a {@link #Request.Register Register} request.
	 * Contains code to verify if the registration was successful or not
	 * @see #Response
	 * @author intot
	 *
	 */
	public static class Register extends Response {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6013702546376497327L;
		private int status;

		private Register(int status) {
			this.status = status;
			if (status == 0) {
				options = Server.MAIN_MENU;
				buildMessage(0);
			} else if (status == 1) {
				setMessage(String.format("\nRegisteration Failed\nEmail Address already exists"));
			} else if (status == 2) {
				setMessage(String.format("\nRegisteration Failed\nID already exists"));
			} else if (status == 3) {
				setMessage(String.format("\nRegisteration Failed\nID already exists\nEmail Address already exists"));
			} else {
				throw new IllegalArgumentException("Unknown status code " + status);
			}
		}

		@Override
		public String getHeaderAsString() {
			return String.format("\nRegistration Completed Successfully!!");
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		@Override
		public Request process() throws MyException {
			if (status == 0) {
				Formatter.printBoxed("    Registration Successfull   ", 1, '+', '|', '-');
				return Response.getResponse("connect1").process();
			} else {
//				System.out.print(getMessage());
				String s = status == 1 ? ("Registration Failed: Email Address already exists")
						: (status == 2 ? ("Registration Failed: Id already exists")
								: ("Registration Failed: Id and Email already exists"));
				Formatter.printError(s, 1);
				return Response.getResponse("connect0").process();
			}
		}

		@Override
		public String getDefaultCancelString() {
			return "Exit";
		}

		@Override
		public boolean hasCancelOption() {
			return true;
		}

	}

	private Response() {

	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}