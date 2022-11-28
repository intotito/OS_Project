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
