package ie.atu.sw.os.reqres;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

import ie.atu.sw.os.reqres.Request.Login;
import ie.atu.sw.os.server.Server;

public abstract class Response implements Serializable, Formatter {
	private String message;
	protected String[] options;

	protected void buildMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append(getHeaderAsString());
		sb.append(getOptionsAsString(options));
		setMessage(sb.toString());
	}
	
	public Request process() {
		System.out.print(getMessage());
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			int value = -1;
			do {
				value = Integer.parseInt(reader.readLine().trim());
				if (hasCancelOption() && (value == options.length + 1)) {
					throw new IOException("Connection Terminated");
				}
				if (value < 1 || value > options.length) {
					System.out.format("Invalid Option '%d' Entered%s", value, getSelectionsAsString(options));
					continue;
				}
			} while (value < 1 || value > options.length);
			return Request.getRequest(options[value - 1]);
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Response getResponse(String res) {
		if (res.equalsIgnoreCase("connect")) {
			return new Connect();
		} else if (res.toLowerCase().matches("register\\d")) {
			return new Register(Integer.parseInt(res.substring("register".length())));
		} else if (res.toLowerCase().matches("login\\d")) {
			return new Login(Integer.parseInt(res.substring("login".length())));
		} else if(res.toLowerCase().matches("add\\d")) {
			return new AddReport(Integer.parseInt(res.substring("add".length())));
		}
		throw new IllegalArgumentException("Unknown Response type " + res);
	}

	public static class Connect extends Response {
		private Connect() {
			options = new String[] { "Register", "Login" };
			buildMessage();
		}
		
		@Override
		public String getHeaderAsString() {
			return String.format("\nConnection Established with Server at port %d", Server.PORT);
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
	
	public static class Login extends Response{
		private int status;
		private Login(int status) {
			this.status = status;
			System.out.println("Attempted Login Successful with: " + status);
			if(this.status == 1) {
				options = Server.MAIN_MENU;
				buildMessage();
			} else {
				setMessage(String.format("\nLogin Unsuccessfull\nInvalid User ID"));
			}
		}
		@Override
		public String getHeaderAsString() {
			return "\nLogin Successful";
		}
		@Override
		public Request process() {
			if(status == 1) {	
				return super.process();
			} else {
				System.out.print(getMessage());
				return Response.getResponse("connect").process();
			}
		}
		@Override
		public String getDefaultCancelString() {
			return "Quit";
		}

		@Override
		public boolean hasCancelOption() {
			return true;
		}	
	}
	
	public static class AddReport extends Response{
		private int id;
		private AddReport(int id) {
			this.id = id;
			options = Server.MAIN_MENU;
			buildMessage();
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
	}

	public static class Register extends Response {

		private int status;

		private Register(int status) {
			this.status = status;
			if (status == 0) {
				options = Server.MAIN_MENU;
				buildMessage();
			} else if(status == 1) {
				setMessage(String.format("\nRegisteration Failed\nEmail Address already exists"));
			} else if(status == 2) {
				setMessage(String.format("\nRegisteration Failed\nID already exists"));
			} else if(status == 3) {
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
		public Request process() {
			if(status == 0) {
				return super.process();
			} else {
				return Response.getResponse("connect").process();
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