package ie.atu.sw.os.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import ie.atu.sw.os.data.Database;
import ie.atu.sw.os.reqres.Formatter;
/**
 * This class represents the Server Application
 * @author intot
 *
 */
public class Server implements Runnable {
	// The various states the Server could be in.
	private static final int STATUS_STARTING = 0, STATUS_RUNNING = 1, STATUS_STOPPED = 2;
	// Array containing the main menus of the Application
	public static final String[] MAIN_MENU = { "Add", "Assign", "View", "Update" };
	// The port the Server Application will be running at.
	public static final int PORT = 4048;
	private ServerSocket server;
	// The status of the server initialized to STARTING
	private int status = STATUS_STARTING;
	// A thread save integer variable to hold the number of connections
	public static AtomicInteger CONNECTIONS = new AtomicInteger(0);
	// An instance of the database
	private Database database;

	/**
	 * Creates a new instance of the Server and initialize the database
	 * @throws IOException - If anything goes wrong with the connection
	 */
	public Server() throws IOException {
		database = new Database();

		server = new ServerSocket(PORT);
	}

	private void startService() {
		new Thread(this).start();
	}
/**
 * Creates a new instance of Worker Thread to service a particular client
 * @param socket
 */
	private void serviceClient(Socket socket) {
		try {
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
//			System.out.println("Connected with client");
			Worker worker = new Worker(is, os, database);
			worker.start();
			// System.out.println("Where are you mazi?");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
/**
 * This static method outputs information to the console as status feed
 * @param entry - An array of data to be printed to the console
 */
	public static void printFeed(String[] entry) {
		System.out.print('\r');
		// Width of individual columns of the tables to be rendered by the Formatter
		int[] ratios = { 15, 8, 10, 8 };
		Formatter.printTabularFeed(entry, ratios, 0, '+', '|', '-');
		System.out.format("|Number of Connections: %d", CONNECTIONS.get());
	}
/**
 * Application Entry method for the Server
 * @param args - Command line arguments
 * @throws IOException - If server terminated forcefully
 */
	public static void main(String[] args) throws IOException {
		Server server = new Server();
		Formatter.printBoxedTitled("     Bug Tracker     ", "       Server       ", 2, '+', '|', '-', 1);
		System.out.println('\n');
		// Title of the individual columns of the table to be displayed by the Formatter
		String[] title = { "Client", "Time", "Request", "Status" };
		// Width of the respective columns
		int[] ratios = { 15, 8, 10, 8 };
		Function<Integer, String[]> function = null;
		Formatter.printTabular(title, 0, function, ratios, 0, '+', '|', '-');
		System.out.format("|Number of Connections: %d", CONNECTIONS.get());
//		new Thread(() -> {
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			printFeed(title);
//		}).start();

		server.startService();
	}

	@Override
	public void run() {
		status = STATUS_RUNNING;
		do {
			try {
				// System.out.format("Server Running at Port: %d", PORT);
				Socket client = server.accept();

				serviceClient(client);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (status == STATUS_RUNNING);
	}
}
