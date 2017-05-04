package org.komparator.security;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

import javax.crypto.*;
import java.util.*;

import org.junit.*;

import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static org.junit.Assert.*;

public class CryptoUtilTest {

    // static members
	final static String CERTIFICATE = "example.cer";

	final static String KEYSTORE = "example.jks";
	final static String KEYSTORE_PASSWORD = "1nsecure";

	final static String KEY_ALIAS = "example";
	final static String KEY_PASSWORD = "ins3cur3";
	
	private final String plainText = "This is a test message!";
	
	/** Plain text bytes. */
	private final byte[] plainBytes = plainText.getBytes();
	
	private static PublicKey publicKey = null;
	private static PrivateKey privateKey = null;
	
	private static KeyPair randomKeyPair = null;
	
	private static CryptoUtil crypto = null;

    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() {
        // runs once before all tests in the suite
    	
		try {
			publicKey = CertUtil.getX509CertificateFromResource(CERTIFICATE).getPublicKey();
		} catch (CertificateException | IOException e) {
			// TODO Auto-generated catch block
			e.getMessage();
		}
		
		
		try {
			privateKey = CertUtil.getPrivateKeyFromKeyStoreResource(KEYSTORE, 
					KEYSTORE_PASSWORD.toCharArray(), KEY_ALIAS, KEY_PASSWORD.toCharArray());
		} catch (UnrecoverableKeyException | FileNotFoundException | KeyStoreException e) {
			// TODO Auto-generated catch block
			e.getMessage();
		}
		
		randomKeyPair = generateRandomKeyPair();
		crypto = new CryptoUtil();
	}

    @AfterClass
    public static void oneTimeTearDown() {
        // runs once after all tests in the suite
    }

    // members

    // initialization and clean-up for each test
    @Before
    public void setUp() {
        // runs before each test
    }

    @After
    public void tearDown() {
        // runs after each test
    }

    @Test(expected = CryptoUtilException.class)
    public void asymCipherNullContentTest() throws CryptoUtilException{
    	crypto.asymCipher(null,publicKey);
	}
    
    @Test(expected = CryptoUtilException.class)
    public void asymCipherNullPublicKeyTest() throws CryptoUtilException{
    	crypto.asymCipher(plainBytes,null);
	}
    
    @Test(expected = CryptoUtilException.class)
    public void asymDecipherNullContentTest() throws CryptoUtilException{
    	crypto.asymDecipher(null,privateKey);
	}
    
    @Test(expected = CryptoUtilException.class)
    public void asymDecipherNullPrivateKeyTest() throws CryptoUtilException {
    	crypto.asymDecipher(plainBytes,null);
	}
    
    @Test(expected = CryptoUtilException.class)
    public void asymDecipherWithAdulteratedDataTest() throws CryptoUtilException{
    	byte[] bChipher = crypto.asymCipher(plainBytes, publicKey);
    	bChipher[6] = bChipher[2];
    	crypto.asymDecipher(bChipher, privateKey);
    }
    
    @Test(expected = CryptoUtilException.class)
    public void asymDecipherWithWrongPrivateKeyTest() throws CryptoUtilException{
    	byte[] bChipher = crypto.asymCipher(plainBytes, publicKey);
    	crypto.asymDecipher(bChipher, randomKeyPair.getPrivate());
    }
    
    @Test(expected = CryptoUtilException.class)
    public void asymCipherWithWrongPublicKeyTest() throws CryptoUtilException{
    	byte[] bChipher = crypto.asymCipher(plainBytes, randomKeyPair.getPublic());
    	crypto.asymDecipher(bChipher, privateKey);
    }
    
    @Test(expected = CryptoUtilException.class)
    public void makeDigitalSignNullContentTest() throws CryptoUtilException{
    	crypto.makeDigitalSignature(null, privateKey);
    }
    
    @Test(expected = CryptoUtilException.class)
    public void makeDigitalSignNullPrivateKeyTest() throws CryptoUtilException{
    	crypto.makeDigitalSignature(plainBytes, null);
    }
    
    @Test(expected = CryptoUtilException.class)
    public void verifiyDigitalSignNullContentTest() throws CryptoUtilException{
    	byte[] b = crypto.makeDigitalSignature(plainBytes, privateKey);
    	crypto.verifyDigitalSignature(null, publicKey,b);
    }
    
    @Test(expected = CryptoUtilException.class)
    public void verifiyDigitalSignNullPrivateKeyTest() throws CryptoUtilException{
    	byte[] b = crypto.makeDigitalSignature(plainBytes, privateKey);
    	crypto.verifyDigitalSignature(plainBytes, null,b);
    }
    
    @Test(expected = CryptoUtilException.class)
    public void verifyDigitalSignNullDigitalSignTest() throws CryptoUtilException{
    	crypto.verifyDigitalSignature(plainBytes, publicKey,null);
    }
    
    // tests
    @Test
    public void asymChipherNotNutllTest() throws CryptoUtilException{
    	
    	byte[] b = crypto.asymCipher(plainBytes, publicKey);
    	 assertNotNull(b);
        
    }
    
    @Test
    public void asymDecipherNotNutllTest() throws CryptoUtilException{
    	byte[] bChipher = crypto.asymCipher(plainBytes, publicKey);
    	byte[] b = crypto.asymDecipher(bChipher, privateKey);
    	assertNotNull(b);
        
    }
    
    @Test
    public void asymCipherAndDecipherGetCorrectTextTest() throws CryptoUtilException{
    	byte[] bChipher = crypto.asymCipher(plainBytes, publicKey);
    	byte[] b = crypto.asymDecipher(bChipher, privateKey);
    	String a = new String(b);
    	assertEquals(a,plainText);
    }
    
    @Test
    public void asymDecipherTwiceTest() throws CryptoUtilException{
    	byte[] bChipher = crypto.asymCipher(plainBytes, publicKey);
    	byte[] b = crypto.asymDecipher(bChipher, privateKey);
    	byte[] b2 = crypto.asymDecipher(bChipher, privateKey);
    	assertEquals(printHexBinary(b), printHexBinary(b2));
    }
    
    @Test
    public void asymCipherTwiceTest() throws CryptoUtilException{
    	byte[] bChipher = crypto.asymCipher(plainBytes, publicKey);
    	byte[] bChipher2 = crypto.asymCipher(plainBytes, publicKey);
    	assertNotEquals(printHexBinary(bChipher), printHexBinary(bChipher2));
    }
    
    @Test
    public void makeDigitalSignatureNotNull() throws CryptoUtilException{
    	byte[] b = crypto.makeDigitalSignature(plainBytes, privateKey);
    	assertNotNull(b);
    }
    
    @Test
    public void verifyDigitalSignatureIsTrue() throws CryptoUtilException{
    	byte[] b = crypto.makeDigitalSignature(plainBytes, privateKey);
    	boolean x = crypto.verifyDigitalSignature(plainBytes, publicKey,b);
    	assertTrue(x);
    }
    
    @Test
    public void verifyDigitalSignatureIsFalse() throws CryptoUtilException{
    	byte[] b = crypto.makeDigitalSignature(plainBytes, privateKey);
    	b[4] = b[9];
    	boolean x = crypto.verifyDigitalSignature(plainBytes, publicKey,b);
    	assertFalse(x);
    }
    
    @Test
    public void verifyDigitalSignatureWithFalsePublicKey() throws CryptoUtilException{
    	byte[] b = crypto.makeDigitalSignature(plainBytes, privateKey);
    	boolean x = crypto.verifyDigitalSignature(plainBytes, randomKeyPair.getPublic(),b);
    	assertFalse(x);
    }
    
    private static KeyPair generateRandomKeyPair(){
    	
    	KeyPairGenerator keyGen;
    	KeyPair keyPair = null;
		try {
			keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(2048);
			keyPair = keyGen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.getMessage();
		}
		 
		return keyPair;
    }
    

}
