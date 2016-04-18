package com.zeppamobile.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * String encryption utility
 * @author Eric
 * 
 */
public class StringCipher {
	// javax.crypto.Cipher options as described here:
	// https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#Cipher
	private final static String ALGORITHM = "AES";
	private final static String MODE = "CBC";
	private final static String PADDING = "PKCS5Padding";
	
	// This is very bad practice -- the initialization vector should be randomly generated each time.
	// If multiple pieces of information are encoded with the same key and IV attackers can gain at least
	// partial information about the contents. Check the Wikipedia page on IVs for more info.
	// Unfortunately, we need a way to search encrypted data so we need a 1:1 mapping (if the IV is random we won't
	// know what to search for unless we decode each piece of info in the DB to check it. Easier to search for
	// an encoded piece of information.
	private final static byte[] ivBytes = {-77, -110, 7, -36, 7, 7, 38, 80, 10, 88, 111, 127, -28, -28, -122, -48};

	// simple main for testing/demo
	public static void main(String[] args) {
		String text = "the quick brown fox jumped over the lazy dog";

		// generate random key
		SecureRandom random = new SecureRandom();
		byte[] keyBytes = new byte[16];
		random.nextBytes(keyBytes);

		byte[] encrypted = encrypt(text, keyBytes);
		System.out.println(Arrays.toString(encrypted));

		String decrypted = decrypt(encrypted, keyBytes);
		System.out.println(decrypted);
	}

	public static byte[] encrypt(String data, final byte[] PRIVATE_KEY) {
		byte[] encrypted = null;
		// uncomment for random init vector
//		byte[] ivBytes = null;

		// create the key to pass to cipher
		SecretKeySpec key = new SecretKeySpec(PRIVATE_KEY, ALGORITHM);

		String params = ALGORITHM + "/" + MODE + "/" + PADDING;

		try {
			Cipher cipher = Cipher.getInstance(params);
			
			// This is how the initialization vector should be generated
//			SecureRandom random = new SecureRandom();
//			ivBytes = new byte[cipher.getBlockSize()];
//			random.nextBytes(ivBytes);
						
			IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
			cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
			
			byte[] input = data.getBytes();
			encrypted = new byte[cipher.getOutputSize(input.length)];
			int enc_len = cipher.update(input, 0, input.length, encrypted, 0);
			enc_len += cipher.doFinal(encrypted, enc_len);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (ShortBufferException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

		// In order to recover the first block of the encrypted text we need the IV used to encode it.
		// so attach the IV to the front of the encrypted message and read it during decryption.
		// Uncomment to use with random init vector
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		try {
//			out.write(ivBytes);
//			out.write(encrypted);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		return out.toByteArray();
		
		return encrypted;
	}

	public static String decrypt(byte[] encrypted, final byte[] PRIVATE_KEY) {
		// In best practice, the IV would be extracted from the first block
		// of encrypted text and then be used as the IV to decrypt the first real block
		
		byte[] decrypted = null;
		
		// wrap key data in Key/IV specs to pass to cipher
		SecretKeySpec key = new SecretKeySpec(PRIVATE_KEY, ALGORITHM);

		String params = ALGORITHM + "/" + MODE + "/" + PADDING;

		try {
			Cipher cipher = Cipher.getInstance(params);
			
//			// recover the init vector used to encrypt the data
//			byte[] ivBytes = Arrays.copyOf(encrypted, cipher.getBlockSize());
////			System.out.println(Arrays.toString(ivBytes));
//			encrypted = Arrays.copyOfRange(encrypted, cipher.getBlockSize(), encrypted.length);
			
			int enc_len = encrypted.length;
			
			IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

			cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
			decrypted = new byte[cipher.getOutputSize(enc_len)];
			int dec_len = cipher.update(encrypted, 0, enc_len, decrypted, 0);
			dec_len += cipher.doFinal(decrypted, dec_len);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (ShortBufferException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

		if (decrypted != null) {
			try {
				// very important to use this specific character set (stackoverflow)
				return new String(decrypted, "ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		return "";
	}
}
