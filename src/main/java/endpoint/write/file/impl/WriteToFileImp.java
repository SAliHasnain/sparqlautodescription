package endpoint.write.file.impl;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sad.utils.SADUtils;

import endpoint.write.file.WriteToFile;

public class WriteToFileImp  implements WriteToFile{

	Logger logger= LoggerFactory.getLogger("WriteToFilesImp");
	
	 String sourceFile="";
	 String fileLocation = System.getProperty("user.home")+File.separator+"Desktop"+File.separator+"endpoint_status.csv";
	 static BufferedReader br = null;
	
	 Set<String> endpSet = new HashSet<String>();
	 FileWriter writer;
	private int statusCodeNum;

	    
	
	
	
private final static String dataHubURL, dataHubURI, dataHubDataSetURL, dataHubDataSetURI, TAG_PREFIX;
    
static {
        dataHubURL = "http://datahub.io/api/2/";
        dataHubURI = "search/resource?format=api/sparql&all_fields=1&limit=1000";
        dataHubDataSetURL = "rest/dataset/";
        dataHubDataSetURI = "search/resource";
        TAG_PREFIX = "http://datahub.io/dataset?tags=";
    }
    
    public void setSourceFile(String sourceFileName){
    	
    	sourceFile=System.getProperty("user.home")+File.separator+"Desktop"+File.separator+sourceFileName;
    }
	public Set<String> provider() throws ParseException {

		createHeaderInCSVFile(fileLocation);// first time clear the file and
											// write the header
		String line = "";
		int count = 0;
		try {
			br = new BufferedReader(new FileReader(sourceFile));
			while ((line = br.readLine()) != null) {
				endpSet.add(line);
			}

			queryDATAHUBEndpoints();
			

		} catch (FileNotFoundException e) {
			logger.error("Source file for endpoints not found {}",e.getMessage());
			logger.info("querying endpoints directly from DATAHUB service ");
			try {
				queryDATAHUBEndpoints();
			} catch (IOException e1) {
				logger.error("IO Exception {}", e1);
			}
;		} catch (IOException e) {
			logger.error("IO EXCEPTION {}",e.getMessage());
		}

		return checkStatus(endpSet);
	}

	private void queryDATAHUBEndpoints() throws IOException{
		
		URL dHubURL = new URL(dataHubURL + dataHubURI);
		URLConnection conn = dHubURL.openConnection();

		InputStream inStream = conn.getInputStream();

		JsonObject json = JSON.parse(inStream);
		JsonValue jsonValue = json.get("results");
		Iterator<JsonValue> itr = jsonValue.getAsArray().iterator();
		while (itr.hasNext()) {
			JsonValue endpAdd = itr.next();

			endpSet.add(endpAdd.getAsObject().get("url").toString());
		}
	
	}
	private Set<String> checkStatus(Set<String> endpSet2) throws ParseException{
		
		
		
		  for (Object endp : endpSet2) {
	             
			  String endPoint=endp.toString().replace("\"", "");
		        try {
		        	if (endPoint.startsWith("http://")) {
		           	  HTTP_Response(endPoint);
		                    if (this.statusCodeNum == 200) {
		                    	logger.info("{} Up and running",endPoint);
		                       writeToCSVFile(fileLocation,endPoint,"SERVER UP AND RUNNING","\'"+SADUtils.getCurrentTime()+"\'");
		                                           
		                    } else {
		                            if (statusCodeNum == 400 || statusCodeNum == 404
		        							|| statusCodeNum == 500
		        							|| statusCodeNum == 502
		        							|| statusCodeNum == 503) {
		        						
		        						logger.error("down and status code: {} is for '{}' ",statusCodeNum,endPoint);
		        						writeToCSVFile(fileLocation,endPoint,"SERVER DOWN","\'"+SADUtils.getCurrentTime()+"\'");
		        					}
		                            }
		                     } else {
		                       logger.warn("EndPoint not found ");
		                       }//do nothing
		        } catch (UnknownHostException e) {
		           logger.error(" Unkonwn Host {}",endPoint);
		            writeToCSVFile(fileLocation,endPoint,"SERVER DOWN","\'"+SADUtils.getCurrentTime()+"\'");
		           
		        } catch (SocketTimeoutException e) {
		        	logger.error(" Time Out for {}",endPoint);
		            writeToCSVFile(fileLocation,endPoint,"SERVER DOWN","\'"+SADUtils.getCurrentTime()+"\'");
		           
		        } catch (IOException e) {
		        	logger.error("{} is down and not responding at the moment",endPoint);
		            writeToCSVFile(fileLocation,endPoint,"SERVER DOWN","\'"+SADUtils.getCurrentTime()+"\'");
		            
		        }
	        }
		
		return null;
	}
	

    public void HTTP_Response(String endPoint) throws IOException {
    	statusCodeNum=0;
    	List<String> statusCode = null;

    	URL obj = new URL(endPoint);
        HttpURLConnection http = (HttpURLConnection) obj.openConnection();
        http.setConnectTimeout(15 * 2000);
        http.setReadTimeout(15 * 2000);
        http.setRequestMethod("GET");
        statusCodeNum = http.getResponseCode();
        Map<String, List<String>> map= http.getHeaderFields();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
    		
        	if(entry.getKey()==null){
        		statusCode=entry.getValue();
        	}
        	
    	}
/*        String server = http.getHeaderField("Server");

        httpResp = "StatusCode="+statusCode+ " & Server=[" + server + "]";*/

    }
	
	public void createHeaderInCSVFile(String fileName) {
		try {
            writer = new FileWriter(fileName, false); // clear the file old data before writing
            writer = new FileWriter(fileName, true);

            writer.append("ENDPOINT");
            writer.append(',');
            writer.append("STATUS");
            writer.append(',');
            writer.append("TIME");
            writer.append('\n');
            writer.flush();
            writer.close();
        } catch (IOException e) {
        }
		
	}

	public void writeToCSVFile(String fileName,String endPoint, String status, String timeStamp) {
	      
		
		try {
	            writer = new FileWriter(fileName, true);
	            writer.append(endPoint);
	            writer.append(',');
	            writer.append(status);
	            writer.append(',');
	            
	            writer.append(timeStamp);
	            writer.append(',');
	            
	            writer.append("\n");
	            writer.flush();
	            writer.close();
	        } catch (IOException e) {
	        }
		
	}

}
