

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class FileTransfer_Thread extends Thread{
	protected Socket threadSocket;
	protected String[] serverArgs;
	
	public FileTransfer_Thread(Socket serverSocket, String[] serverArgs) {
		this.threadSocket = serverSocket;
		this.serverArgs = serverArgs;
	}
	
	public void run() {
		try {
			// Create object input/output streams
			ObjectInputStream in = new ObjectInputStream(threadSocket.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(threadSocket.getOutputStream());
			
			
		}
		
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
