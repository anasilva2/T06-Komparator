package org.komparator.security;

import static javax.xml.bind.DatatypeConverter.printHexBinary;
import java.io.*;
import java.security.*;
import javax.crypto.*;
import java.util.*;

public class CryptoUtil {

	/**
	 * Asymmetric cipher: combination of algorithm, block processing, and
	 * padding.
	 */
	private static final String ASYM_CIPHER = "RSA/ECB/PKCS1Padding";
	
	/** Digital signature algorithm. */
	private static final String SIGNATURE_ALGO = "SHA256withRSA";
	
	public byte[] asymCipher(byte[] content, PublicKey publicKey) throws CryptoUtilException{
		
		byte[] cipherBytes = null;
		
		if(content == null)
			throw new CryptoUtilException("Content to be chiphered cannot be null");
		
		if(publicKey == null)
			throw new CryptoUtilException("Public Key in chipher cannot be null");
		
		// get an RSA cipher object
		Cipher cipher;
		
		try {
			cipher = Cipher.getInstance(ASYM_CIPHER);
			
			// encrypt the plain text using the public key
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			
			cipherBytes = cipher.doFinal(content);
			
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | 
				InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			throw new CryptoUtilException("Invalid Asymethric Chipher");
		}
		
		
		return cipherBytes;
	}
	
	public byte[] asymDecipher(byte[] content, PrivateKey privateKey) throws CryptoUtilException {
		
		byte[] decipherBytes = null;
		
		if(content == null)
			throw new CryptoUtilException("Content to be deciphered cannot be null");
		
		if(privateKey == null)
			throw new CryptoUtilException("Private Key in Decipher cannot be null");
		
		// get an RSA cipher object
		Cipher cipher;
		
		try {
			cipher = Cipher.getInstance(ASYM_CIPHER);
		
			// decipher the ciphered digest using the private key
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			
			decipherBytes = cipher.doFinal(content);
			
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			throw new CryptoUtilException("Invalid Asymethric Decipher");
		}
		
		return decipherBytes;
		
	}
	
	public byte[] makeDigitalSignature(byte[] content, PrivateKey privateKey) throws CryptoUtilException{
	
		byte[] digitalSignature = null;
		
		if(content == null)
			throw new CryptoUtilException("Content for Digital Signature cannot be null");
		
		if(privateKey == null)
			throw new CryptoUtilException("Private Key for Digital Signature cannot be null");
		
		digitalSignature = CertUtil.makeDigitalSignature(SIGNATURE_ALGO, privateKey, content);
		
		
		if(digitalSignature == null)
			throw new CryptoUtilException("Digital Signature cannot be made");

		return digitalSignature;
	}
	
	public boolean verifyDigitalSignature(byte[] content, PublicKey publicKey, byte[] digitalSignature) 
			throws CryptoUtilException{
		
		boolean result = false;
		
		if(content == null)
			throw new CryptoUtilException("Content to Verify Digital Signature cannot be null");
		
		if(publicKey == null)
			throw new CryptoUtilException("Public Key to Verify Digital Signature cannot be null");
	
		if(digitalSignature == null)
			throw new CryptoUtilException("Digital Signature to be verified cannot be null");
		
		result = CertUtil.verifyDigitalSignature(SIGNATURE_ALGO, publicKey, content, digitalSignature);
		
		return result;
	}
	
	
	
}
