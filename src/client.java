

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

public class client {
	
	public static void Client(String[] clientArgs) throws BadPaddingException {
		String password = clientArgs[1];
		String filepath = clientArgs[2];
		String action = clientArgs[3];
		InetAddress serverIP;
		try {
			serverIP = InetAddress.getByName(clientArgs[4]);
			int serverPort = Integer.parseInt(clientArgs[5]);
			
			// Open socket to server and input/output streams
			Socket clientSocket = new Socket(serverIP, serverPort);
			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
			DataInputStream in = new DataInputStream(clientSocket.getInputStream());
			
			Path curPath = Paths.get("");
			String path = curPath.toAbsolutePath().toString();
			
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
				byte[] encryptedFile = cipher.aes_encrypt(filepath,K);
				// encrypt K
				byte[] encryptedK = cipher.rsa_encrypt(path + "/server/key.pub", K);
				
				// send encrypted K and then encrypted file
				out.writeInt(encryptedK.length);
				out.write(encryptedK);
				out.writeInt(encryptedFile.length);
				out.write(encryptedFile);
				
				break;
				
			case "b":
				// sign file using sha256 and rsa private key and send to server
				byte[] signatureB = cipher.sign(filepath, path + "/client/key.key");
				byte[] fileB = Files.readAllBytes(Paths.get(filepath));
				// send file and then signature
				out.writeInt(fileB.length);
				out.write(fileB);
				out.writeInt(signatureB.length);
				out.write(signatureB);
				
				break;
				
			case "c":
				// sign file using sha256 and rsa private key and send to server
				byte[] signatureC = cipher.sign(filepath, path + "/client/key.key");
				byte[] fileC = Files.readAllBytes(Paths.get(filepath));
				if(fileC[0] == 0x00) {
					fileC[0] = (byte) 0xFF;
				}
				else {
					fileC[0] = (byte) 0x00;
				}
				
				// send file and then signature
				out.writeInt(fileC.length);
				out.write(fileC);
				out.writeInt(signatureC.length);
				out.write(signatureC);
				
				break;
				
			default:
				System.out.println("Valid modes are a, b, and c.");
				break;
			}
			
			in.close();
			out.close();
			clientSocket.close();
		
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("Server could not be found at specified IP address. Retry with correct address.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Socket read has failed. Please retry.");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			System.out.println("Encryption/Decryption has failed. Please retry.");
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			System.out.println("Key is not compatible. Please retry with new key.");
		}
	}	

}
