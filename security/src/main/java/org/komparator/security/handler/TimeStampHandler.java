package org.komparator.security.handler;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 * This SOAPHandler outputs the contents of inbound and outbound messages.
 */
public class TimeStampHandler implements SOAPHandler<SOAPMessageContext> {

	//
	// Handler interface implementation
	//

	public static final String CONTEXT_PROPERTY = "my.property";
	
	/**
	 * Gets the header blocks that can be processed by this Handler instance. If
	 * null, processes all.
	 */
	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	/**
	 * The handleMessage method is invoked for normal processing of inbound and
	 * outbound messages.
	 */
	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if(outbound.booleanValue())
			addTimeStamp(smc);
		else
			try {
				checkTime(smc);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.getMessage();
			} catch (SOAPException e) {
				// TODO Auto-generated catch block
				e.getMessage();
			}
			
		return true;
	}

	/** The handleFault method is invoked for fault message processing. */
	@Override
	public boolean handleFault(SOAPMessageContext smc) {
		
		return true;
	}

	/**
	 * Called at the conclusion of a message exchange pattern just prior to the
	 * JAX-WS runtime dispatching a message, fault or exception.
	 */
	@Override
	public void close(MessageContext messageContext) {
		// nothing to clean up
	}

	/** Date formatter used for outputting timestamps in ISO 8601 format */
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	/**
	 * Check the MESSAGE_OUTBOUND_PROPERTY in the context to see if this is an
	 * outgoing or incoming message. Write a brief message to the print stream
	 * and output the message. The writeTo() method can throw SOAPException or
	 * IOException
	 */
	
	private void addTimeStamp(SOAPMessageContext smc) {

		try {
			System.out.println("Writing header in outbound SOAP message...");

			// get SOAP envelope
			SOAPMessage msg = smc.getMessage();
			SOAPPart sp = msg.getSOAPPart();
			SOAPEnvelope se = sp.getEnvelope();

			// add header
			SOAPHeader sh = se.getHeader();
			if (sh == null)
				sh = se.addHeader();
			
			// add header element (name, namespace prefix, namespace)
			Name name = se.createName("TimeStamp", "ts", "http://org.komparator/security");
			SOAPHeaderElement element = sh.addHeaderElement(name);

			// add header element value
			String valueString = dateFormatter.format(new Date()).toString();
			element.addTextNode(valueString);
			
			int i = 5000;
			
			try {
				System.out.println("Going to sleep for " + i/1000 + " seconds");
				Thread.sleep(i);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (Exception e) {
			System.out.print("Caught exception in handleMessage: ");
			System.out.println(e);
			System.out.println("Continue normal processing...");
		}
	}
	
	private boolean checkTime(SOAPMessageContext smc) throws ParseException, SOAPException {
		

			
		// get SOAP envelope
		SOAPMessage msg = smc.getMessage();
		SOAPPart sp = msg.getSOAPPart();
		SOAPEnvelope se = sp.getEnvelope();

		// add header
		SOAPHeader sh = se.getHeader();
		if (sh == null)
			sh = se.addHeader();
			
		Name name = se.createName("TimeStamp", "ts", "http://org.komparator/security");
		Iterator it = sh.getChildElements(name);
			
		if (!it.hasNext()) {
			System.out.println("Header element not found.");
			return true;
		}
			
		SOAPElement element = (SOAPElement) it.next();
			
		// get header element value
		String dateFormatter = element.getValue();
		SimpleDateFormat dateFormatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			
		Date d1 = dateFormatter1.parse(dateFormatter);
			
			
		Date actualDate = new Date();		
			
		int timeTaken = (int) (actualDate.getTime() - d1.getTime())/1000;
			
		if(timeTaken > 3)
			throw new RuntimeException("Time taken was greater than 3");
				

		return true;
		
	}
}