package com.fairysupport.run;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class EncKey {

	public EncKey() {
	}

	public void generate(String keyPath) throws NoSuchAlgorithmException, IOException {

		IvParameterSpec iv = this.generateIV();
		SecretKey key = this.generateKey();

		byte[] ivByte = iv.getIV();
		byte[] keyByte = key.getEncoded();

		byte[] concatArray = new byte[keyByte.length + ivByte.length];
		System.arraycopy(ivByte, 0, concatArray, 0, ivByte.length);
		System.arraycopy(keyByte, 0, concatArray, ivByte.length, keyByte.length);

		byte[] encodeByte = Base64.getEncoder().encode(concatArray);
		String encodeStr = new String(encodeByte);

		FileUtil.write(keyPath, encodeStr);

	}

	public SecretKey generateKey() throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		keyGen.init(128, random);
		return keyGen.generateKey();
	}

	public IvParameterSpec generateIV() throws NoSuchAlgorithmException {
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		byte[] iv = new byte[16];
		random.nextBytes(iv);
		return new IvParameterSpec(iv);
	}

}
