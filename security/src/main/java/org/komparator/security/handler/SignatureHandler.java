package org.komparator.security.handler;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import org.komparator.security.CertUtil;
import org.komparator.security.CryptoUtil;
import org.komparator.security.CryptoUtilException;

public class SignatureHandler implements SOAPHandler<SOAPMessageContext>{

	public static final String CONTEXT_PROPERTY = "my.property";
	
	public static String idEmissor;
	public static PrivateKey privateKey;
	private final String password = "CT6tR3zV";
		
	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		
		Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if(outbound.booleanValue())
			createSignature(smc);
		return true;
	}
	
	private void createSignature(SOAPMessageContext smc) {
		
		CryptoUtil crypto = new CryptoUtil();
		
		
		try {
			
			//Obter o private key do emissor atraves do path
			String path = "/"+idEmissor+".jks";
			privateKey = CertUtil.getPrivateKeyFromKeyStoreResource(path, password.toCharArray(), idEmissor.toLowerCase(), password.toCharArray());
			
			// get SOAP envelope
			SOAPMessage msg = smc.getMessage();
			SOAPPart sp = msg.getSOAPPart();
			SOAPEnvelope se = sp.getEnvelope();
			
			//Converte a soap message para bytes 
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			msg.writeTo(bos);
			
			if(privateKey == null)
				throw new RuntimeException("Private Key at Signature Handler cannot be null");
			
			
			//Efectua a assinatura do soap message
			byte[] signature = crypto.makeDigitalSignature(bos.toByteArray(), privateKey);
			
			// add header
			SOAPHeader sh = se.getHeader();
			if (sh == null)
				throw new RuntimeException("Header of SOAP Message doesn't exist");
			
			//adiciona ao cabeçalho da soap message a assinatura e o nome do emissor
			// add header element (name, namespace prefix, namespace)
			Name name = se.createName("Signature", "sgn", "http://org.komparator/security");
			SOAPHeaderElement element = sh.addHeaderElement(name);
			
			// add header element (name, namespace prefix, namespace)
			Name nameEmissor = se.createName("IdEmissor", "idE", "http://org.komparator/security");
			SOAPHeaderElement nameEmissorElement = sh.addHeaderElement(nameEmissor);
			
			String encondedsignature = printBase64Binary(signature);
			
			element.addTextNode(encondedsignature);
			
			nameEmissorElement.addTextNode(idEmissor);
			
		} catch (Exception e) {
			
			e.getMessage();
		}
	}

	@Override
	public void close(MessageContext arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean handleFault(SOAPMessageContext arg0) {
		// TODO Auto-generated method stub
		return true;
	}

	

}
