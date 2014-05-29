package sad.initializer;


import java.io.File;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import endpoint.write.file.WriteToFile;
import endpoint.write.file.impl.WriteToFileImp;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sad.conceiver.Conceiver;
import sad.endpoints.provider.impl.DataHUB;
import sad.paramhandler.impl.Manipulator;

public class Main 
{
static final Logger logger= LoggerFactory.getLogger("Main");

String sourceFile=System.getProperty("user.home")+File.separator+"Endpoints"+File.separator+"2014-05-26-ALIVE_RESPONDING_ENDPOINTS";// 

	final static Set<Conceiver> conc=new HashSet<Conceiver>();
	static{ // open to add other configurations and classes like DataHub
	
		conc.add(new Conceiver(new Manipulator(new QryConfigurations(),new DataHUB())));
		
	}
	
	
	/*
	 * main entry point for the sad application
	 */
	public static void main( String[] args )
    {
        new Main().progStartPoint();
    	
    }

    
    private void progStartPoint(){
    	
    	Scanner scanner= new Scanner(System.in);
    	System.out.println("Enter 1 to start queries test \nEnter 2 to start file writing \nEnter any other integer value to exit the system");
    	
    	int i= scanner.nextInt();
    	
    	switch(i){
    	case 1:  for(Conceiver con:conc){
					//con.logAllLiveEndpoints();
    				//con.execQryOverLoggedEndpoints(sourceFile);
    				con.execQryOverEndpointsStream();
    			}
    		
    		break;
    	case 2: // to write the csv file
    			WriteToFile endPoints= new WriteToFileImp();
    		
    			try{
    				Scanner scanner2= new Scanner(System.in);
	    			System.out.println("give source file name (for example: endpointlists.txt) located on desktop otherewise program will start to query endpoints directly from DATAHUB");
	    	
	    			String i2= scanner2.next();
	    			endPoints.setSourceFile(i2);
	    			scanner2.close();
    				endPoints.provider();
    			}catch(Exception ioe){
    				
    			}
    		
    		break;
    	default:
    		System.exit(0);
    		scanner.close();
    	
    	}
    	
    }
}
