package ie.atu.sw.os.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import ie.atu.sw.os.reqres.Request;
import ie.atu.sw.os.reqres.Response;
import ie.atu.sw.os.server.Server;

public class Client implements Runnable{
	public Client() {
	}
	
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
			do {
				response = (Response)is.readObject();
				System.out.println("Did we connect? " + response);
				request = response.process();
				System.out.println("PRocessing" + request);
				os.writeObject(request);
				os.flush();
			}while(true);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch(NullPointerException npe) {
			npe.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Client client = new Client();
		client.startService();
	}
}
