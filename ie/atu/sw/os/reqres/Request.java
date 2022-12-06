package ie.atu.sw.os.reqres;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

import ie.atu.sw.os.User;
import ie.atu.sw.os.data.Database;
import ie.atu.sw.os.data.Report;

public abstract class Request implements Serializable {
	protected String[] options;

	public abstract Response process(Database database) throws IOException;

	public static Request getRequest(String req) throws IOException {
		System.out.println("Attempting " + req);
		if (req.equalsIgnoreCase("register")) {
			return new Register();
		} else if (req.equalsIgnoreCase("login")) {
			return new Login();
		} else if(req.matches("^Add.*")) {
			return new AddReport();
		} else if(req.matches("^Assign.*")) {
			return new Assign();
		} else if(req.equalsIgnoreCase("view")) {
			return new View();
		}
		
		throw new IllegalArgumentException(String.format("'%s' Requested Not Supported", req));
	}
	
	public static class AddReport extends Request{
		private static int NAME = 0, PLATFORM = 1, DESCR = 2;
		private String[] values = new String[5];
		
		private AddReport() throws IOException{
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String[] menus = {"Application Name", "Platform", "Bug Description"};
			for (int i = 0; i < menus.length; i++) {
				System.out.printf("\n\tEnter %s:>", menus[i]);
				values[i] = reader.readLine();
			}
		}
		@Override
		public Response process(Database database) throws IOException {
			int id = database.addReport(new Report(values[NAME], values[PLATFORM], values[DESCR]));
			return Response.getResponse("Add" + id);
		}
		
	}
	
	public static class Assign extends Request{
		private static int REPORT_ID = 0, USER_ID = 1;
		private String[] values = new String[2];
		
		private Assign() throws IOException{
			BufferedReader reader  = new BufferedReader(new InputStreamReader(System.in));
			String[] menus = {"Report Id", "User Id"};
			for (int i = 0; i < menus.length; i++) {
				System.out.printf("\n\tEnter %s:>", menus[i]);
				values[i] = reader.readLine();
			}
		}
		
		@Override
		public Response process(Database database) throws NumberFormatException, IOException {
			int code = database.assign(Integer.parseInt(values[REPORT_ID]), values[USER_ID]);
			Response response = Response.getResponse("assign" + code);
			return response;
		}
	}

	public static class View extends Request implements Formatter{
		private String res;
		private int code = -1; // 1 - Assigned, 1 - Unassigned
		
		private View() throws IOException{
			String[] menus = {"Reports", "Users"};
			System.out.printf(getHeaderAsString());
			System.out.printf(getOptionsAsString(menus, 1));
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			try {
				int value = -1;
				do {
					value = Integer.parseInt(reader.readLine().trim());
					if (hasCancelOption() && (value == menus.length + 1)) {
						throw new IOException("Connection Terminated");
					}
					if (value < 1 || value > menus.length) {
						System.out.format("\tInvalid Option '%d' Entered%s", value, getSelectionsAsString(menus, 1));
						continue;
					}
				} while (value < 1 || value > menus.length);
				res = menus[value - 1];
				if(res.equals("Reports")) {
					String[] subMenus = {"All", "Unassigned"};
					System.out.printf("\t\tView Reports");
					System.out.printf(getOptionsAsString(subMenus, 2));
					do {
						value = Integer.parseInt(reader.readLine().trim());
						if (hasCancelOption() && (value == subMenus.length + 1)) {
							throw new IOException("Connection Terminated");
						}
						if (value < 1 || value > subMenus.length) {
							System.out.format("\tInvalid Option '%d' Entered%s", value, getSelectionsAsString(subMenus, 2));
							continue;
						}
					}while(value < 1 || value > menus.length);
					code = value - 1; // 0 - Reports, 1 - Users
					System.out.println("Code : " + code);
				} 
				System.out.println("RES: " + res);
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public Response process(Database database) throws IOException {
			if(res.equalsIgnoreCase("reports")) {
				return Response.getResponse(res + code);
			} else if(res.equalsIgnoreCase("users")) {
				System.out.println("Process users requeest");
				Response.Users u = (Response.Users)Response.getResponse(res);
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
	}
	public static class Login extends Request {
		private static int ID = 0;
		private String [] values = new String[1];
		
		private Login() throws IOException{
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String[] menus = { "ID" };
			for (int i = 0; i < menus.length; i++) {
				System.out.printf("\n\tEnter %s:>", menus[i]);
				values[i] = reader.readLine();
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

		private Register() throws IOException {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String[] menus = { "ID", "Name", "Email", "Department" };
			for (int i = 0; i < menus.length; i++) {
				System.out.printf("\n\tEnter %s:>", menus[i]);
				values[i] = reader.readLine();
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
