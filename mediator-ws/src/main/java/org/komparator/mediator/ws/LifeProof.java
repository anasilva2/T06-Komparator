package org.komparator.mediator.ws;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.mediator.ws.cli.MediatorClientException;
import org.komparator.security.handler.TimeStampHandler;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;

public class LifeProof extends Thread{

	public static String SECONDARYMEDIATORURL = "http://localhost:8072/mediator-ws/endpoint";
	private String UDDIURL = null;
	public static MediatorClient clientMediator;
	private int imAliveTime = 5; /*periodo do imAlive*/
	private int secondaryTime = 7;
	private int latency = 1;
	public static Timer timer = new Timer();
	static boolean flag = false;
	
	
	Exception exception;
    
    /** WS I indicates if it's primary or secondary mediator **/
    private String wsI = null;
    
    public LifeProof(String wsI,String uddiURL){
  
    	this.wsI = wsI;
    	this.UDDIURL = uddiURL;
    }

    public Exception getException() {
        synchronized(this) {
            return this.exception;
        }
    }
    
    public void terminate(){
    	System.out.println(this.getClass().getSimpleName() + " stopped...");
    	System.exit(0);
    }
    

    
	public void run(){
		
		
			try {
				System.out.println("Creating MediatorClient to Secondary Mediator at " + SECONDARYMEDIATORURL);
				clientMediator = new MediatorClient(SECONDARYMEDIATORURL);
				
			} catch (Exception e) {
				System.out.println("Secondary Mediator isn't available");
			}
			
			/*Mediator Primário*/
			if(wsI.equals("1")){
				System.out.println(this.getClass().getSimpleName() + " running for Primary Mediator");

				
					
					
				timer.scheduleAtFixedRate(new TimerTask(){

					@Override
					public void run() {
							
						clientMediator.imAlive(); 
					}
						
						
				}, 0, imAliveTime*1000);
					
			}else{
				System.out.println("SECONDARY MEDIATOR");
				int limit = secondaryTime + latency; 
				
				
		
			   TimerTask task = new TimerTask(){

				@Override
				public void run() {
					
					Date d = new Date();
					Date d2 = LastTimeAliveSingleton.getInstance().getLastTimeAlive();
					
					SimpleDateFormat dateFormatter1 = new SimpleDateFormat("HH:mm:ss");
					int timeTaken = (int) (d.getTime() - d2.getTime())/1000;
					System.out.println("Actual Time = " + dateFormatter1.format(d));
					System.out.println("Last Time was alive = " + dateFormatter1.format(d2));
					System.out.println("Time taken = " + timeTaken);
					
					if(timeTaken >= limit){
						System.out.println("Primary Mediator is down...");
						registerUDDI();
						timer.cancel();
						
					}else{
						// Esta a receber imAlive
					}
					
				}
				   
			   };
				
			   timer.schedule(task,0,secondaryTime*1000);
				
			}
	}
	
	public void stopThread(){
		
		this.terminate();
		
	}
	private void registerUDDI(){
		System.out.println("Secondary Mediator is going to be registered at UDDI...");
		try {
			
			UDDINaming uddiNaming = new UDDINaming(UDDIURL);
			uddiNaming.rebind("T06_Mediator", SECONDARYMEDIATORURL);
			System.out.println("Success registering Secondary Mediator");
			
		} catch (UDDINamingException e) {
			// TODO Auto-generated catch block
			System.out.println("Failed to register the secondary mediator at UDDI");
			e.getMessage();
		}
	}
	
}
