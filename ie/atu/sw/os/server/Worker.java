package ie.atu.sw.os.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import ie.atu.sw.os.data.Database;
import ie.atu.sw.os.reqres.Request;
import ie.atu.sw.os.reqres.Response;

public class Worker extends Thread {
	private ObjectOutputStream os;
	private ObjectInputStream is;
	private Database database;

	protected Worker(InputStream is, OutputStream os, Database database) throws IOException {
		this.os = new ObjectOutputStream(os);
		this.is = new ObjectInputStream(is);
		System.out.println("Worker constructed");
		
		this.database = database;
		
	}

	@Override
	public void run() {
		try {
			System.out.println("Worker running..");
			Response response = Response.getResponse("connect");
			os.writeObject(response);
			while (true) {
				Request request = (Request) is.readObject();
				response = request.process(database);
				os.writeObject(response);
				os.flush();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
