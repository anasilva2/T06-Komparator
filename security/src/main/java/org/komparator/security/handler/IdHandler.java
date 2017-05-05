package org.komparator.security.handler;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class IdHandler implements SOAPHandler<SOAPMessageContext>{

	public static final String CONTEXT_PROPERTY = "my.property";
	
	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if(outbound.booleanValue())
			addId(smc);
		else
			checkId(smc);
		return true;
	}

	private void checkId(SOAPMessageContext smc) {
		
		
		try {
			
			// get SOAP envelope
			SOAPMessage msg = smc.getMessage();
			SOAPPart sp = msg.getSOAPPart();
			SOAPEnvelope se = sp.getEnvelope();
			
			// add header
			SOAPHeader sh = se.getHeader();
			if(sh == null)
				throw new RuntimeException("Header in IdHandler Doesn't Exist");
			
			Name name = se.createName("Identifier", "id", "http://org.komparator/security");
			Iterator it = sh.getChildElements(name);
			
			if (!it.hasNext()) {
				throw new RuntimeException("Cannot find TimeStamp at Header");
			}
			
			SOAPElement element = (SOAPElement) it.next();
			
			String randomNumber = element.getValue();
			
			if(Singleton.getInstance().getArray().contains(randomNumber))
				throw new RuntimeException("This message has been received before");
			
			
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Erro no handler IdHandler");
		}
		
	}


	private void addId(SOAPMessageContext smc) {
		
		String random = secureRandomNumber();
		if(random != null){
			
			
			
			try {
				
				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se;
				
				se = sp.getEnvelope();
				
				// add header
				SOAPHeader sh = se.getHeader();
				
				if (sh == null)
					throw new RuntimeException("Header in IdHandler cannot be null");
				
				Name name = se.createName("Identifier", "id", "http://org.komparator/security");
				
				SOAPHeaderElement element = sh.addHeaderElement(name);
				element.addTextNode(random);
				
				Singleton.getInstance().addElementArray(random);
				
			} catch (SOAPException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException("Erro no handler IdHandler");
			}
		}
	}


	@Override
	public boolean handleFault(SOAPMessageContext context) {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public void close(MessageContext context) {
		// TODO Auto-generated method stub
		
	}
	
	
	private String secureRandomNumber(){
		
		SecureRandom random;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
			System.out.println("Generating random byte array ...");

			final byte array[] = new byte[32];
			random.nextBytes(array);

			String b = printHexBinary(array);
			return b;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Erro generation a secure random number");
		}

	}
	
	public static  class Singleton{
		
		private static Singleton singleton;
		private ArrayList<String> list = null;
		
		private Singleton(){
			list = new ArrayList<String>();
		}
		
		public static Singleton getInstance(){
			if(singleton == null)
				singleton = new Singleton();
			
			return singleton;
		}
		
		public ArrayList<String> getArray(){
			return this.list;
		}
		
		public void addElementArray(String value){
			list.add(value);
		}
	}

}
