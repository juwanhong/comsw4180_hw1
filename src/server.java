

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class server {
	
	public static void Server(String[] serverArgs) {
		int portNumber = Integer.parseInt(serverArgs[1]);
		try {
			// Create serverSocket
			ServerSocket serverSocket = new ServerSocket(portNumber);
			while(true) {
				System.out.println(">> Listening for client connection...");
				// Listen for client connections
				Socket clientSocket = serverSocket.accept();
				// Start serverThread
				serverThread serverThread = new serverThread(clientSocket, serverArgs);
				serverThread.start();
				
				clientSocket.close();
			}
			
		}
		catch (IOException e) {
			System.out.println("Socket connection error - retry with different port number.");
		}
	}

}
