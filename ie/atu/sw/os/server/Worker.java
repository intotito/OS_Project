package ie.atu.sw.os.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import ie.atu.sw.os.data.Database;
import ie.atu.sw.os.reqres.Request;
import ie.atu.sw.os.reqres.Response;

public class Worker extends Thread {
	private ObjectOutputStream os;
	private ObjectInputStream is;
	private Database database;
	private String userId;

	protected Worker(InputStream is, OutputStream os, Database database) throws IOException {
		this.os = new ObjectOutputStream(os);
		this.is = new ObjectInputStream(is);
//		System.out.println("Worker constructed");
		
		this.database = database;
	}
	private void log(Request request) {
		if(request instanceof Request.Login) {
			userId = ((Request.Login)request).getId();
		} else if(request instanceof Request.Register) {
			userId = ((Request.Register)request).getValue(0);
		}
		String[] values = {userId, 
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
				request.getName(), "OK"
				};
		Server.printFeed(values);
	}
	@Override
	public void run() {
		try {
			Server.CONNECTIONS.incrementAndGet();
			Response response = Response.getResponse("connect0");
			String[] values = {"Anonymous", 
					LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
					response.getName(), "OK"
					};
			Server.printFeed(values);
	//		System.out.println("Worker running..");
			
			os.writeObject(response);
			while (true) {
				Request request = (Request) is.readObject();
	//			System.out.println("SEEEE: " + request.getName());
				log(request);
	//			System.out.println("Read in " + request);
				response = request.process(database);
				os.writeObject(response);
				os.flush();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
	//		e.printStackTrace();
		} catch(NullPointerException npe) {
//			System.out.println("Client Terminated Connection");
			try {
				os.close();
				is.close();
			} catch (IOException e) {
	//			e.printStackTrace();
			}
			
		} finally {
			Server.CONNECTIONS.decrementAndGet();
			String[] values = {userId == null ? "Anonymous" : userId, 
					LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
					"Terminate", "OK"
					};
			Server.printFeed(values);
		}

	}
}
