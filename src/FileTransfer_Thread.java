

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

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
			DataInputStream in = new DataInputStream(threadSocket.getInputStream());
			DataOutputStream out = new DataOutputStream(threadSocket.getOutputStream());
			// read encrypted K
			int lengthK = in.readInt();
			byte[] encryptedK = new byte[lengthK];
			in.read(encryptedK);
			// read encrypted File
			int lengthFile = in.readInt();
			byte[] encryptedFile = new byte[lengthFile];
			in.read(encryptedFile);
			// decrypt K with private key (RSA)
			Path curPath = Paths.get("");
			String path = curPath.toAbsolutePath().toString();
			SecretKey K = Encrypt_Decrypt.rsa_decrypt(encryptedK, path + "/server/key.key");
			// decrypt file with K (AES)
			Encrypt_Decrypt.aes_decrypt(encryptedFile, K, path + "/server/file");
		}
		
		catch (IOException | InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
	}

}
