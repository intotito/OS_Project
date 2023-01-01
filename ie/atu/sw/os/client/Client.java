package ie.atu.sw.os.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import ie.atu.sw.os.exception.MenuCancelException;
import ie.atu.sw.os.exception.MyException;
import ie.atu.sw.os.reqres.Formatter;
import ie.atu.sw.os.reqres.Request;
import ie.atu.sw.os.reqres.Response;
import ie.atu.sw.os.server.Server;

/**
 * This class represents a client using the application
 * @author intot
 *
 */
public class Client implements Runnable {
	/**
	 * Creates a new instance of Client
	 */
	public Client() {
	}
/**
 * Starts the client service on a new thread
 */
	public void startService() {
		new Thread(this).start();
	}
	


	@Override
	public void run() {
		try {
			Socket socket = new Socket("127.0.0.1", Server.PORT);
			Response response = null;
			Request request = null;
			ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
			out:
			do {
				response = (Response) is.readObject();
				do {
					try {
						request = response.process();
					} catch (MenuCancelException e) {
				//		e.printStackTrace();
						response = null;
						response = Response.getResponse("connect0");
						continue;
					} catch(MyException mye) {
						// quit
						socket.close();
						break out;
					}
					break;
				} while (true);
				os.writeObject(request);
				os.flush();
			} while (true);
		} catch (UnknownHostException e) {
		//	e.printStackTrace();
		} catch (IOException e) {
		//	e.printStackTrace();
		} catch (ClassNotFoundException e) {
		//	e.printStackTrace();
		} catch (NullPointerException npe) {
		//	npe.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Client client = new Client();
		Formatter.printBoxedTitled("     Bug Tracker     ", "       Client       ", 2, '+', '|', '-', 1);
		client.startService();
	}
}