

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileTransfer_Server {
	
	public static void Server(String[] serverArgs) {
		int portNumber = Integer.parseInt(serverArgs[1]);
		try {
			// Create serverSocket
			ServerSocket serverSocket = new ServerSocket(portNumber);
			while(true) {
				// Listen for client connections
				Socket clientSocket = serverSocket.accept();
				// Start serverThread
				FileTransfer_Thread serverThread = new FileTransfer_Thread(clientSocket, serverArgs);
				serverThread.start();
			}
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
