
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

public class serverThread extends Thread {

	protected Socket threadSocket;
	protected String[] serverArgs;

	public serverThread(Socket serverSocket, String[] serverArgs) {
		this.threadSocket = serverSocket;
		this.serverArgs = serverArgs;
	}

	public void run() {
		String mode = serverArgs[2];
		Path curPath = Paths.get("");
		String path = curPath.toAbsolutePath().toString();
		try {
			// Create object input/output streams
			DataInputStream in = new DataInputStream(threadSocket.getInputStream());
			DataOutputStream out = new DataOutputStream(threadSocket.getOutputStream());
			
			switch(mode) {
			
			case "d":
				// read encrypted K
				int lengthK = in.readInt();
				byte[] encryptedK = new byte[lengthK];
				in.read(encryptedK);
				
				// read encrypted File
				int lengthEncryptedFile = in.readInt();
				byte[] encryptedFile = new byte[lengthEncryptedFile];
				in.read(encryptedFile);
				
				// decrypt K with private key (RSA)
				SecretKey K = Encrypt_Decrypt.rsa_decrypt(encryptedK, path + "/server/key.key");
				
				// decrypt file with K (AES)
				Encrypt_Decrypt.aes_decrypt(encryptedFile, K, path + "/server/file");
				
				System.out.println(">>>> File recieved from client. File saved to: " + path + "/server/file");
				
				break;
				
			case "v":
				// read file
				int lengthFile = in.readInt();
				byte[] file = new byte[lengthFile];
				in.read(file);
				
				// read signature
				int lengthSignature = in.readInt();
				byte[] signature = new byte[lengthSignature];
				in.read(signature);
				
				// verify signature
				boolean verified = Encrypt_Decrypt.verify(file, signature, path + "/client/key.pub");
				
				// print output
				if(verified) {
					System.out.println(">>>> Signature is valid.");
				}
				else {
					System.out.println(">>>> Signature is invalid.");
				}
				
			}

			return;
		}

		catch (IOException e) {
			System.out.println("Failed to read socket input. Please retry.");
			return;
		}
	}

}
