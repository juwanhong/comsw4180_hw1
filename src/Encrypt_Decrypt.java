

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encrypt_Decrypt {

	public static byte[] aes_encrypt(String filepath, SecretKey K) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, IllegalBlockSizeException, BadPaddingException {
		// instantiate cipher
		Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		aesCipher.init(Cipher.ENCRYPT_MODE,K);
		// convert file to byte[]
		Path path = Paths.get(filepath);
		byte[] file = Files.readAllBytes(path);
		
		//encrypt file
		byte[] encryptedFile = aesCipher.doFinal(file);
	
		return encryptedFile;
	}
	
	public static void aes_decrypt(byte[] encryptedFile, SecretKey K, String filepath) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
		// instantiate cipher
		Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] iv = {0};
		IvParameterSpec ivspec = new IvParameterSpec(iv);
		aesCipher.init(Cipher.DECRYPT_MODE, K, ivspec);
		// decrypt file
		byte[] decryptedFile = aesCipher.doFinal(encryptedFile);
		// save file
		Path path = Paths.get(filepath);
		Files.write(path,decryptedFile);
		
	}
	
	public static byte[] rsa_encrypt(String keypath, SecretKey K) throws InvalidKeyException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException {
		// get public key from keypath
		Path path = Paths.get(keypath);
		byte[] publicKeyBytes = Files.readAllBytes(path);
		X509EncodedKeySpec ks = new X509EncodedKeySpec(publicKeyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PublicKey publicKey = kf.generatePublic(ks);
		// Initialize cipher
		Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		rsaCipher.init(Cipher.ENCRYPT_MODE,publicKey);
		// encrypt
		byte[] encryptedK = rsaCipher.doFinal(K.getEncoded());
		
		return encryptedK;
	}
	
	public static SecretKey rsa_decrypt(byte[] encryptedK, String keypath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		// get private key from keypath
		Path path = Paths.get(keypath);
		byte[] privateKeyBytes = Files.readAllBytes(path);
		PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(privateKeyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = kf.generatePrivate(ks);
		// Initialize cipher
		Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		rsaCipher.init(Cipher.DECRYPT_MODE,privateKey);
		// encrypt
		SecretKey K = new SecretKeySpec(rsaCipher.doFinal(encryptedK), "AES");
		
		return K;
	}
}
