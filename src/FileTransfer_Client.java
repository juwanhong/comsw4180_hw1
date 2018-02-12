

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
		DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
		DataInputStream in = new DataInputStream(clientSocket.getInputStream());
				
		switch (action) {
		case "a": 
			// use pw to generate 128-bit K, encrypt w/ AES (CBC) using K, 
			// encrypt K with server's public key, send encrypted K and file
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] salt = new String("12345678").getBytes();
			KeySpec spec = new PBEKeySpec(password.toCharArray(),salt,1024,128);
			SecretKey K = factory.generateSecret(spec);
			K = new SecretKeySpec(K.getEncoded(),"AES");
			
			// encrypt file
			byte[] encryptedFile = Encrypt_Decrypt.aes_encrypt(filepath,K);
			// encrypt K
			byte[] encryptedK = Encrypt_Decrypt.rsa_encrypt("../server/key.pub", K);
			
			// send encrypted K and then encrypted file
			out.writeInt(encryptedK.length);
			out.write(encryptedK);
			out.writeInt(encryptedFile.length);
			out.write(encryptedFile);			
			
		case "b":
			
		case "c":
		}
	}

}
