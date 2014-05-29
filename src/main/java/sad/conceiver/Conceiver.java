package sad.conceiver;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sad.handler.ParamHandler;


public class Conceiver {

	public static final Logger logger=LoggerFactory.getLogger("Conceiver");

	//interface implemented by the manipulator
	ParamHandler paramHandler;
 	
 	
	public Conceiver (ParamHandler paramHandler){
		
		this.paramHandler=paramHandler;
	}
	
	public void logAllEndpoints(){
		
		 paramHandler.logAllEndpoints();
		
	}
	
	public void logAllLiveEndpoints(){
		paramHandler.logAliveEndpoints();
	}
	
	public void logAliveAndRespondingEndpoints(){
		paramHandler.logAliveAndRespondingEndpoints();
		
	}
	
	public void execQryOverEndpointsStream(){
		paramHandler.queryEndpointsStream();
	}
        
	public void execQryOverLoggedEndpoints(String file){
	
		paramHandler.queryLoggedEndpoints(file);
	}
    
	
        
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
