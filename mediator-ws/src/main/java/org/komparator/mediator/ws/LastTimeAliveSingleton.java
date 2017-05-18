package org.komparator.mediator.ws;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LastTimeAliveSingleton {

	private static LastTimeAliveSingleton instance = null;
	
	private Date lastImAliveReceived = null;
	
	public LastTimeAliveSingleton(){}
	
	public static synchronized  LastTimeAliveSingleton getInstance(){
		if(instance == null)
			instance = new LastTimeAliveSingleton();
		return instance;
	}
	
	public void setLastTimeAlive(Date sdf){
		getInstance().lastImAliveReceived = sdf;
	}
	
	public Date getLastTimeAlive(){
		return getInstance().lastImAliveReceived;
	}
	
}
