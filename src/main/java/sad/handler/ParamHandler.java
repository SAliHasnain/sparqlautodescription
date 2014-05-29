package sad.handler;

import java.util.List;
import java.util.Map;


public interface ParamHandler { 

	// this method logs all the endpoints 
	public void logAllEndpoints();
	
	//this method logs all the live endpoints
	public void logAliveEndpoints();
	
	//this method logs all the endpoint which are alive and responding to  the select query
	public void logAliveAndRespondingEndpoints();
	//this method executes queries from given configuration file over live endpoints
	public void queryEndpointsStream();
	
	//this method executes queries from given configuration file over file based logged endpoints
	public void queryLoggedEndpoints(String file);
	
}
