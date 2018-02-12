

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class FileTransfer_Client {
	
	public static void Client(String[] clientArgs) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		String password = clientArgs[1];
		String filepath = clientArgs[2];
		String action = clientArgs[3];
		InetAddress serverIP = InetAddress.getByName(clientArgs[4]);
		int serverPort = Integer.parseInt(clientArgs[5]);
		
		// server public key
		
		
		// Open socket to server and input/output streams
		Socket clientSocket = new Socket(serverIP, serverPort);
		ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
		ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
				
		switch (action) {
		case "a": 
			// use pw to generate 128-bit K, encrypt w/ AES (CBC) using K, 
			// encrypt K with server's public key, send encrypted K and file
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(password.toCharArray());
			SecretKey K = factory.generateSecret(spec);
			
			// encrypt file
			byte[] encryptedFile = Encrypt_Decrypt.aes_encrypt(filepath,K);
			// encrypt K
			byte[] encryptedK = Encrypt_Decrypt.rsa_encrypt("../server/key.pub", K);
			
			// send encrypted K and then encrypted file
			out.write(encryptedK);
			out.write(encryptedFile);			
			
			
		case "b":
			
		case "c":
		}
	}

}
