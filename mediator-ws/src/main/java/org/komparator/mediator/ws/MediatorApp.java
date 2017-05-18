package org.komparator.mediator.ws;

import java.util.Timer;

import org.komparator.security.handler.SignatureHandler;
import org.komparator.security.handler.TimeStampHandler;

public class MediatorApp {

	public static void main(String[] args) throws Exception {
		// Check arguments
		if (args.length == 0 || args.length == 2) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + MediatorApp.class.getName() + " wsURL OR uddiURL wsName wsURL");
			return;
		}
		String uddiURL = null;
		String wsName = null;
		String wsURL = null;
		String wsI = null;

		// Create server implementation object, according to options
		MediatorEndpointManager endpoint = null;
		if (args.length == 1) {
			wsURL = args[0];
			endpoint = new MediatorEndpointManager(wsURL);
		} else if (args.length >= 3) {
			uddiURL = args[0];
			
			wsName = args[1];
			SignatureHandler.idEmissor = wsName;
			wsURL = args[2];
			wsI = args[3];
			if(wsI.equals("1")){
				System.out.println("------ Primary Mediator ------");
				
				endpoint = new MediatorEndpointManager(uddiURL, wsName, wsURL);
			}else{
				System.out.println("------ Secondary Mediator ------");
				endpoint = new MediatorEndpointManager(uddiURL,wsName,wsURL,wsI);
			}
			
			endpoint.setVerbose(true);
		}
		
		LifeProof lifeproof = new LifeProof(wsI,uddiURL);
		try {
			endpoint.start();
			synchronized(lifeproof){
				
				if(!wsI.equals("1")){
					System.out.println("Waiting for Primary Mediator to start...");
					while(LastTimeAliveSingleton.getInstance().getLastTimeAlive() == null);
				}
				lifeproof.start();
				endpoint.awaitConnections();
			}
			
			
		} finally {
			
			endpoint.stop();
			lifeproof.stopThread();
			
			
		}

	}

}
