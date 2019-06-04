package com.fairysupport.run;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Enc {

	private IvParameterSpec iv = null;
	private SecretKeySpec key = null;

	private Cipher encrypter;

	public Enc(String keyPath) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
		
		String ivAndKey = FileUtil.read(keyPath);
		byte[] ivAndKeyByte = Base64.getDecoder().decode(ivAndKey);
		byte[] ivByte = new byte[16];
		byte[] keyByte = new byte[128 / 8];
		
		if ((ivByte.length + keyByte.length) != ivAndKeyByte.length) {
			throw new RuntimeException("wrong key " + keyPath);
		}

		System.arraycopy(ivAndKeyByte, 0, ivByte, 0, ivByte.length);
		System.arraycopy(ivAndKeyByte, 16, keyByte, 0, keyByte.length);

		IvParameterSpec ivParameterSpec = new IvParameterSpec(ivByte);
		SecretKeySpec key = new SecretKeySpec(keyByte, "AES");

		this.iv = ivParameterSpec;
		this.key = key;

		this.encrypter = Cipher.getInstance("AES/CBC/PKCS5Padding");
		this.encrypter.init(Cipher.ENCRYPT_MODE, this.key, this.iv);

	}

	public String encrypto(String text) throws IllegalBlockSizeException, BadPaddingException {
		byte[] crypto = encrypter.doFinal(text.getBytes());
		byte[] str64 = Base64.getEncoder().encode(crypto);
		return new String(str64);
	}

}
