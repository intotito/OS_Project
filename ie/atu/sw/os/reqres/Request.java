package ie.atu.sw.os.reqres;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Arrays;

import ie.atu.sw.os.User;
import ie.atu.sw.os.data.Database;
import ie.atu.sw.os.data.Report;
import ie.atu.sw.os.exception.MenuCancelException;
import ie.atu.sw.os.exception.MyException;
import ie.atu.sw.os.exception.ViewMenuCancelException;

public abstract class Request implements Serializable {
	protected String[] options;

	public abstract Response process(Database database) throws IOException;

	public static Request getRequest(String req) throws IOException {
		System.out.println("Attempting " + req);
		try {
			if (req.equalsIgnoreCase("register")) {
				return new Register();
			} else if (req.equalsIgnoreCase("update")) {
				return new Update();
			} else if (req.equalsIgnoreCase("login")) {
				return new Login();
			} else if (req.matches("^Add.*")) {
				return new AddReport();
			} else if (req.matches("^Assign.*")) {
				return new Assign();
			} else if (req.equalsIgnoreCase("view")) {
				new View();
			}
		} catch (ViewMenuCancelException vmce) {

		} catch (MenuCancelException mce) {
			do {
				try {
					return Response.getResponse("connect1").process();
				} catch (MyException e) {
					e.printStackTrace();
				}
				break;
			} while (true);
		}
		throw new IllegalArgumentException(String.format("'%s' Requested Not Supported", req));
	}

	public static class AddReport extends Request {
		private static int NAME = 0, PLATFORM = 1, DESCR = 2;
		private String[] values = new String[5];

		private AddReport() {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String[] menus = { "Application Name", "Platform", "Bug Description" };
			for (int i = 0; i < menus.length; i++) {
				System.out.printf("\n\tEnter %s:>", menus[i]);
				try {
					values[i] = reader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public Response process(Database database) throws IOException {
			int id = database.addReport(new Report(values[NAME], values[PLATFORM], values[DESCR]));
			return Response.getResponse("Add" + id);
		}

	}

	public static class Update extends Request implements Formatter {
		private static int REPORT_ID = 0, STATUS_ID = 1;
		private String[] values = new String[2];

		private Update() throws MenuCancelException {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.printf("\n\tEnter %s:>", "Report Id");
			try {
				values[REPORT_ID] = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String[] menus = Arrays.stream(Report.STATUS.values()).map(Enum::toString).toArray(String[]::new);
			System.out.printf(getHeaderAsString());
			System.out.printf(getOptionsAsString(menus, 1));
			int value = -1;
			String valString = null;
			do {
				try {
					valString = reader.readLine().trim();
					value = Integer.parseInt(valString);
				} catch (NumberFormatException nfe) {
					System.out.format("Invalid Option '%s' Entered%s", valString, getSelectionsAsString(menus));
					continue;
				} catch (IOException ie) {
					ie.printStackTrace();
				}
				if (hasCancelOption() && (value == menus.length + 1)) {
					throw new MenuCancelException();
				}
				if (value < 1 || value > menus.length) {
					System.out.format("\tInvalid Option '%d' Entered%s", value, getSelectionsAsString(menus, 1));
					continue;
				}
			} while (value < 1 || value > menus.length);
			values[STATUS_ID] = menus[value - 1];
		}

		@Override
		public String getHeaderAsString() {
			return "\tSelect Status";
		}

		@Override
		public Response process(Database database) throws IOException {
			int code = 1; // Report does not exist
			try {
				code = database.updateReport(Integer.parseInt(values[REPORT_ID]),
						Report.STATUS.valueOf(values[STATUS_ID]));
			} catch (NumberFormatException nfe) {
			}
			Response response = Response.getResponse("update" + code);
//			response.setCode(code);
			return response;
		}

		@Override
		public boolean hasCancelOption() {
			return true;
		}
	}

	public static class Assign extends Request {
		private static int REPORT_ID = 0, USER_ID = 1;
		private String[] values = new String[2];

		private Assign() {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String[] menus = { "Report Id", "User Id" };
			for (int i = 0; i < menus.length; i++) {
				System.out.printf("\n\tEnter %s:>", menus[i]);
				try {
					values[i] = reader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public Response process(Database database) throws IOException {
			int code = 2; // Report does not exist
			try {
				code = database.assign(Integer.parseInt(values[REPORT_ID]), values[USER_ID]);
			} catch (NumberFormatException nfe) {
			}
			Response response = Response.getResponse("assign" + code);
			return response;
		}
	}

	public static class View extends Request implements Formatter {
		private String res;
		private int code = -1; // 1 - Assigned, 1 - Unassigned

		private View() throws ViewMenuCancelException, MenuCancelException {
			String[] menus = { "Reports", "Users" };
			System.out.printf(getHeaderAsString());
			System.out.printf(getOptionsAsString(menus, 1));
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			try {
				int value = -1;
				String valString = null;
				do {
					try {
						valString = reader.readLine().trim();
						value = Integer.parseInt(valString);
					} catch (NumberFormatException nfe) {
						System.out.format("Invalid Option '%s' Entered%s", valString, getSelectionsAsString(menus));
						continue;
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (hasCancelOption() && (value == menus.length + 1)) {
						throw new MenuCancelException();
					}
					if (value < 1 || value > menus.length) {
						System.out.format("\tInvalid Option '%d' Entered%s", value, getSelectionsAsString(menus, 1));
						continue;
					}
				} while (value < 1 || value > menus.length);
				res = menus[value - 1];
				if (res.equals("Reports")) {
					String[] subMenus = { "All", "Unassigned" };
					System.out.printf("\t\tView Reports");
					System.out.printf(getOptionsAsString(subMenus, 2));
					do {
						try {
							valString = reader.readLine().trim();
							value = Integer.parseInt(valString);
						} catch (NumberFormatException nfe) {
							System.out.format("Invalid Option '%s' Entered%s", valString,
									getSelectionsAsString(subMenus));
							continue;
						} catch (IOException e) {
							e.printStackTrace();
						}
						if (hasCancelOption() && (value == subMenus.length + 1)) {
							throw new ViewMenuCancelException();
						}
						if (value < 1 || value > subMenus.length) {
							System.out.format("\tInvalid Option '%d' Entered%s", value,
									getSelectionsAsString(subMenus, 2));
							continue;
						}
					} while (value < 1 || value > menus.length);
					code = value - 1; // 0 - Reports, 1 - Users
					System.out.println("Code : " + code);
				}
				System.out.println("RES: " + res);
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
			}
			/*
			 * catch (IOException e) { e.printStackTrace(); }
			 */
		}

		@Override
		public Response process(Database database) throws IOException {
			if (res.equalsIgnoreCase("reports")) {
				Response.Reports r = (Response.Reports) Response.getResponse(res + code);
				System.out.println("Process reports request");

				System.out.println(r);
				r.loadReports(database.getReports(code));
				return r;
			} else if (res.equalsIgnoreCase("users")) {
				System.out.println("Process users requeest");
				Response.Users u = (Response.Users) Response.getResponse(res);
				System.out.println(u);
				u.loadUsers(database.getUsers());

				return u;
			}
			throw new IllegalStateException("Unrecognized response " + res);
		}

		@Override
		public String getHeaderAsString() {
			return "\tView";
		}

		@Override
		public boolean hasCancelOption() {
			return true;
		}

		@Override
		public String getDefaultCancelString() {
			return "Logout";
		}
	}

	public static class Login extends Request {
		private static int ID = 0;
		private String[] values = new String[1];

		private Login() {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String[] menus = { "ID" };
			for (int i = 0; i < menus.length; i++) {
				System.out.printf("\n\tEnter %s:>", menus[i]);
				try {
					values[i] = reader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public Response process(Database database) {
			int code = database.login(values[ID]);
			Response response = Response.getResponse("login" + code);
			return response;
		}

	}

	public static class Register extends Request {
		private static int ID = 0, NAME = 1, EMAIL = 2, DEPT = 3;
		private String[] values = new String[4];

		private Register() {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String[] menus = { "ID", "Name", "Email", "Department" };
			for (int i = 0; i < menus.length; i++) {
				System.out.printf("\n\tEnter %s:>", menus[i]);
				try {
					values[i] = reader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public String getValue(int VALUE) {
			return values[VALUE];
		}

		@Override
		public Response process(Database database) throws IOException {
			int code = database.register(new User(values[ID], values[NAME], values[EMAIL], values[DEPT]));
			Response response = Response.getResponse("register" + code);
			return response;
		}

	}

	private Request() {
	}

	public static void main(String[] a) throws IOException {
		System.out.println(Request.getRequest("register1"));
	}
}
