package org.komparator.security.handler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.Set;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.komparator.security.CertUtil;
import org.komparator.security.CryptoUtil;
import org.komparator.security.CryptoUtilException;
import org.w3c.dom.NodeList;

import pt.ulisboa.tecnico.sdis.ws.cli.CAClient;
import pt.ulisboa.tecnico.sdis.ws.cli.CAClientException;

public class CreditCardHandler implements SOAPHandler<SOAPMessageContext>{

	public static final String CONTEXT_PROPERTY = "my.property";
	
	private final String caURL = "http://sec.sd.rnl.tecnico.ulisboa.pt:8081/ca";
	private final String mediatorName = "T06_Mediator";
	private final String keyAlias = "t06_mediator";
	private final String password = "CT6tR3zV";
	
	
	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void close(MessageContext smc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean handleFault(SOAPMessageContext smc) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		
		QName operation = (QName) smc.get(MessageContext.WSDL_OPERATION);
		
		if(operation.getLocalPart().equals("buyCart")){
			
			Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if(outbound.booleanValue()){
				cipherCreditCardNumber(smc);
			}else{
				decipherCreditCardNumber(smc);
			}
			
		}
		
		return true;
	}

	private void decipherCreditCardNumber(SOAPMessageContext smc) {
		System.out.println("-------SERVER-------");
		
		CryptoUtil crypto = new CryptoUtil();

		try {
			
			// get SOAP envelope
			SOAPMessage msg = smc.getMessage();
			SOAPPart sp = msg.getSOAPPart();
			SOAPEnvelope se = sp.getEnvelope();
			SOAPBody body = se.getBody();
			
			NodeList children = body.getFirstChild().getChildNodes();
			
			//printSOAPMessage(smc);
			
			for(int i = 0; i < children.getLength(); i++){
				
				if(children.item(i).getNodeName().equals("creditCardNr")){
					
					String ccNumberChiphered = children.item(i).getTextContent();
					byte[] b = parseBase64Binary(ccNumberChiphered);
					String path = "/"+mediatorName+".jks";
					InputStream in = this.getClass().getResourceAsStream(path);
					KeyStore keyStore = CertUtil.readKeystoreFromStream(in, password.toCharArray());
					
					PrivateKey priv = CertUtil.getPrivateKeyFromKeyStore(keyAlias, password.toCharArray(), keyStore);
					
					byte[] dec = crypto.asymDecipher(b, priv);
					
					String cc = new String(dec);
					
					children.item(i).setTextContent(cc);
					msg.saveChanges();
					
				}
			}
			
		} catch (Exception  e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Erro no handler CreditCard");
		}
		
		
	}

	private void cipherCreditCardNumber(SOAPMessageContext smc) {
		System.out.println("-------CLIENTE-------");
		CryptoUtil crypto = new CryptoUtil();
		
		
		try {
			
			CAClient ca = new CAClient(caURL);
			
			// get SOAP envelope
			SOAPMessage msg = smc.getMessage();
			SOAPPart sp = msg.getSOAPPart();
			SOAPEnvelope se = sp.getEnvelope();
			SOAPBody body = se.getBody();
			
			//printSOAPMessage(smc);
			
			NodeList children = body.getFirstChild().getChildNodes();
		
			for(int i = 0; i < children.getLength(); i++){
				
				if(children.item(i).getNodeName().equals("creditCardNr")){
					
					String ccNumber = children.item(i).getTextContent();
					String certificate = ca.getCertificate(mediatorName);
					PublicKey publicK = CertUtil.getX509CertificateFromBytes(certificate.getBytes()).getPublicKey();
					byte[] b = crypto.asymCipher(ccNumber.getBytes(), publicK);
					
					String encondedciphra = printBase64Binary(b);
					
					children.item(i).setTextContent(encondedciphra);
					msg.saveChanges();
				}
			}
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Erro no handler CreditCard");
		}
		
	}
	
	
	private void printSOAPMessage(SOAPMessageContext smc){
		
		
		try {
			SOAPMessage msg = smc.getMessage();
			ByteArrayOutputStream o = new ByteArrayOutputStream();
			msg.writeTo(o);
			String s = new String(o.toByteArray());
			System.out.println(s);
		} catch (SOAPException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

	

}
