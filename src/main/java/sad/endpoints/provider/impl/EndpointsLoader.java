package sad.endpoints.provider.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sad.endpoints.provider.EndPointsProvider;
import sad.utils.SADUtils;

/**
 * 
 * @author qaiser.mehmood@insight-centre.org
 */

public class EndpointsLoader implements EndPointsProvider {

	Logger logger = LoggerFactory.getLogger("EndpointsLoader");
	
	private Set<String> endpSet = Collections.synchronizedSet(new HashSet<String>());
	private Set<String> fileEndpointSet = Collections.synchronizedSet(new HashSet<String>());
	
	// get the list of endpoints from a local file
	private String sourceFile = System.getProperty("user.home") + File.separator
			+ "liveEndpoints.txt";// to run on server
	 
	private static final String dataHubURL, dataHubURI;
	static {
		dataHubURL = "https://datahub.io/api/2/";
		dataHubURI = "search/resource?format=api/sparql&all_fields=1&limit=1000";
	}

	
	public Set<String> provider() {

		// read endpoints from file
		fileEndpointSet = SADUtils.readEndpointsFromFile(sourceFile);

		// fetch endpoints from datahub
		checkendPointsFromDATAHUB();

		endpSet.addAll(fileEndpointSet);

		for (String endps : endpSet) {
			logger.info(endps);
		}

		return endpSet;

	}

	private void checkendPointsFromDATAHUB() {

		try {
			URL dHubURL = new URL(dataHubURL + dataHubURI);
								 
			HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

			// Set verifier     
			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
			HttpsURLConnection conn = (HttpsURLConnection)dHubURL.openConnection();
			InputStream inStream = conn.getInputStream();
			
			JsonObject json = (JsonObject) JSON.parseAny(inStream);
			JsonValue jsonValue = json.get("results");
			Iterator<JsonValue> itr = jsonValue.getAsArray().iterator();
			
			while (itr.hasNext()) {
				JsonValue endpAdd = itr.next();
				if (!endpAdd.getAsObject().get("url").toString().equals(""))
					endpSet.add(endpAdd.getAsObject().get("url").toString()
							.replace("\"", ""));
			}

			logger.info("endpoints from datahub are {}", endpSet.size());
		} catch (MalformedURLException me) {
			logger.error("URL is malformed ");
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("URL is not accessible at the moment");
		}catch (Exception e){
			e.printStackTrace();
			logger.error("exception raised: {}",e.getMessage());
		}
		
	}

}
