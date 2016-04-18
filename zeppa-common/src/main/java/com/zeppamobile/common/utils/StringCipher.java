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
 * 
 * @author Eric
 * 
 */
public class StringCipher {
	// javax.crypto.Cipher options as described here:
	// https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#Cipher
	private final static String ALGORITHM = "AES";
	private final static String MODE = "CBC";
	private final static String PADDING = "PKCS5Padding";

	public static void main(String[] args) {
		String text = "the quick brown fox jumped over the lazy dog";

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
		byte[] ivBytes = null;

		// wrap key data in Key/IV specs to pass to cipher
		SecretKeySpec key = new SecretKeySpec(PRIVATE_KEY, ALGORITHM);

		

		String params = ALGORITHM + "/" + MODE + "/" + PADDING;

		try {
			Cipher cipher = Cipher.getInstance(params);
			
			SecureRandom random = new SecureRandom();
			ivBytes = new byte[cipher.getBlockSize()];
			random.nextBytes(ivBytes);
			
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

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			out.write(ivBytes);
			out.write(encrypted);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return out.toByteArray();
	}

	public static String decrypt(byte[] encrypted, final byte[] PRIVATE_KEY) {
		int enc_len = encrypted.length;
		byte[] decrypted = null;

		// wrap key data in Key/IV specs to pass to cipher
		SecretKeySpec key = new SecretKeySpec(PRIVATE_KEY, ALGORITHM);

		String params = ALGORITHM + "/" + MODE + "/" + PADDING;

		try {
			Cipher cipher = Cipher.getInstance(params);
			//SecureRandom random = new SecureRandom();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] ivBytes = new byte[cipher.getBlockSize()];
			
			//random.nextBytes(ivBytes);
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
				return new String(decrypted, "ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		return "";
	}
}
