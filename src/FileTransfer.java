

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

public class FileTransfer {
	public static byte[] iv = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	public static IvParameterSpec ivspec = new IvParameterSpec(iv);
	
	public static void main(String[] args) throws BadPaddingException {
		if(args.length != 6 || args.length != 3) {
			System.out.println("Input error!");
			System.out.println("Input format should be:");
			System.out.println("	server: java FileTransfer -s [port] [mode:(d or v)]");
			System.out.println("	client: java FileTransfer -c [password:(16 char)] [file path] [mode:(a, b, or c)] [server ip] [port]");
			
			return;
		}
		String mode = args[0];
		
/*		try {
			Path curPath = Paths.get("");
			String path = curPath.toAbsolutePath().toString();
			createRSAKeys(path + "/server/key");
			createRSAKeys(path + "/client/key");
		} catch (NoSuchAlgorithmException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		
		switch (mode) {
		case "-s": //server
			if(args.length == 3) {
				server.Server(args);
			}
			else {
				System.out.println("Input error!");
				System.out.println("Input format should be:");
				System.out.println("	server: java FileTransfer -s [port] [mode:(d or v)]");
			}
			
		case "-c": //client
			if(args.length == 6 && args[1].length() == 16) {
				client.Client(args);
			}
			else {
				System.out.println("Input format should be:");
				System.out.println("	client: java FileTransfer -c [password:(16 char)] [file path] [mode:(a, b, or c)] [server ip] [port]");

			}

		default:
			System.out.println("Use [-s] for server and [-c] for client");
		}
	}

	public static void createRSAKeys(String path) throws NoSuchAlgorithmException, IOException {
		// generate private/public key pair
		KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
		keygen.initialize(2048);
		KeyPair keyPair = keygen.generateKeyPair();
		Key publicKey = keyPair.getPublic();
		Key privateKey = keyPair.getPrivate();
		
		// save to binary file
		FileOutputStream out = new FileOutputStream(path + ".pub");
		out.write(publicKey.getEncoded());
		out.close();
		
		out = new FileOutputStream(path + ".key");
		out.write(privateKey.getEncoded());
		out.close();
		
	}
	
}
