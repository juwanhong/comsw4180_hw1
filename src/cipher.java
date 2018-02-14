

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
import java.security.Signature;
import java.security.SignatureException;
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

public class cipher {


	public static byte[] aes_encrypt(String filepath, SecretKey K) {
		// instantiate cipher
		Cipher aesCipher;
		try {
			aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			aesCipher.init(Cipher.ENCRYPT_MODE,K, FileTransfer.ivspec);
			// convert file to byte[]
			Path path = Paths.get(filepath);
			byte[] file = Files.readAllBytes(path);
			
			//encrypt file
			byte[] encryptedFile = aesCipher.doFinal(file);
			
			return encryptedFile;
			
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			System.out.println("Error occured during AES encryption.");
			return null;
		} catch (NullPointerException e) {
			System.out.println("File not found.");
			return null;
		}
		
		
	}
	
	public static void aes_decrypt(byte[] encryptedFile, SecretKey K, String filepath) {
		// instantiate cipher
		Cipher aesCipher;
		try {
			aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			aesCipher.init(Cipher.DECRYPT_MODE, K, FileTransfer.ivspec);
			// decrypt file
			byte[] decryptedFile = aesCipher.doFinal(encryptedFile);
			// save file
			Path path = Paths.get(filepath);
			Files.write(path,decryptedFile);
			
		}  catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			System.out.println("Error occured during AES decryption.");
		}
		
		
	}
	
	public static byte[] rsa_encrypt(String keypath, SecretKey K) {
		// get public key from keypath
		Path path = Paths.get(keypath);
		byte[] publicKeyBytes;
		try {
			publicKeyBytes = Files.readAllBytes(path);
			X509EncodedKeySpec ks = new X509EncodedKeySpec(publicKeyBytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PublicKey publicKey = kf.generatePublic(ks);
			// Initialize cipher
			Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			rsaCipher.init(Cipher.ENCRYPT_MODE,publicKey);
			// encrypt
			byte[] encryptedK = rsaCipher.doFinal(K.getEncoded());
			
			return encryptedK;
			
		}  catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			System.out.println("Error occured during RSA encryption.");
			return null;
		} catch (NullPointerException e) {
			System.out.println("File not found.");
			return null;
		}
		
		
	}
	
	public static SecretKey rsa_decrypt(byte[] encryptedK, String keypath) {
		// get private key from keypath
		Path path = Paths.get(keypath);
		byte[] privateKeyBytes;
		try {
			privateKeyBytes = Files.readAllBytes(path);
			PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(privateKeyBytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = kf.generatePrivate(ks);
			// Initialize cipher
			Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			rsaCipher.init(Cipher.DECRYPT_MODE,privateKey);
			// encrypt
			SecretKey K = new SecretKeySpec(rsaCipher.doFinal(encryptedK), "AES");
			
			return K;
			
		}  catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			System.out.println("Error occured during RSA decryption.");
			return null;
		}
		
	}
	
	public static byte[] sign(String filepath, String keypath) {
		
		try {
			// convert file to byte[]
			Path filePath = Paths.get(filepath);
			byte[] file = Files.readAllBytes(filePath);
			// get private key from keypath
			Path keyPath = Paths.get(keypath);
			byte[] privateKeyBytes = Files.readAllBytes(keyPath);
			PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(privateKeyBytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = kf.generatePrivate(ks);
			
			// Sign
			Signature privateSignature = Signature.getInstance("SHA256withRSA");
			privateSignature.initSign(privateKey);
			privateSignature.update(file);
			byte[] signature = privateSignature.sign();
			
			return signature;
			
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
			// TODO Auto-generated catch block
			System.out.println("Error occured during signature.");
			return null;
		} catch (NullPointerException e) {
			System.out.println("File not found.");
			return null;
		}
	}
	
	public static boolean verify(byte[] file, byte[] signature, String keypath) {
		boolean verified = false;
		try {
			// get private key from keypath
			Path keyPath = Paths.get(keypath);
			byte[] publicKeyBytes;
			publicKeyBytes = Files.readAllBytes(keyPath);
			X509EncodedKeySpec ks = new X509EncodedKeySpec(publicKeyBytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PublicKey publicKey = kf.generatePublic(ks);
			// verify with public key
			Signature publicSignature = Signature.getInstance("SHA256withRSA");
			publicSignature.initVerify(publicKey);
			publicSignature.update(file);
			verified = publicSignature.verify(signature);
			
			
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
			// TODO Auto-generated catch block
			System.out.println("Error occured during signature verification.");
		}
		
		return verified;
		
	}
}
