package ie.atu.sw.os.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import ie.atu.sw.os.data.Database;

public class Server implements Runnable {
	private static final int STATUS_STARTING = 0, STATUS_RUNNING = 1, STATUS_STOPPED = 2;
	public static final String[] MAIN_MENU = {"Add", "Assign", "View", "Update"};
	public static final int PORT = 4048;
	private ServerSocket server;
	private int status = STATUS_STARTING;
	
	private Database database;

	public Server() throws IOException {
		database = new Database();
		
		server = new ServerSocket(PORT);
	}

	private void startService() {
		new Thread(this).start();
	}

	private void serviceClient(Socket socket) {
		try {
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			System.out.println("Connected with client");
			Worker worker = new Worker(is, os, database);
			worker.start();
			System.out.println("Where are you mazi?");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		Server server = new Server();
		server.startService();
	}

	@Override
	public void run() {
		status = STATUS_RUNNING;
		do {
			try {
				System.out.format("Server Running at Port: %d", PORT);
				Socket client = server.accept();
				
				serviceClient(client);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (status == STATUS_RUNNING);
	}
}
