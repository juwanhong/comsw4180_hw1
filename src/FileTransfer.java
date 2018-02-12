

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

public class FileTransfer {
	
	public static void main(String[] args) {
		String mode = args[0];
		
		try {
			Path curPath = Paths.get("");
			String path = curPath.toAbsolutePath().toString();
			createRSAKeys(path + "/server/key");
			createRSAKeys(path + "/client/key");
		} catch (NoSuchAlgorithmException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		switch (mode) {
		case "-s": //server
			FileTransfer_Server.Server(args);
			
		case "-c": //client
			try {
				FileTransfer_Client.Client(args);
			} catch (InvalidKeyException | InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException
					| IllegalBlockSizeException | BadPaddingException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
