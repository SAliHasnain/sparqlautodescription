package sad.endpoints.provider.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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

public class DataHUB implements EndPointsProvider {

	Logger logger = LoggerFactory.getLogger("DataHUB");

	String sourceFile = System.getProperty("user.home") + File.separator
			+ "File-Containig-Endpoints.txt";// to run on server
	private static final String dataHubURL, dataHubURI;
	Set<String> endpSet = Collections.synchronizedSet(new HashSet<String>());
	Set<String> fileEndpointSet = Collections
			.synchronizedSet(new HashSet<String>());
	static BufferedReader br = null;

	static {
		dataHubURL = "http://datahub.io/api/2/";
		dataHubURI = "search/resource?format=api/sparql&all_fields=1&limit=1000";
	}

	public Set<String> provider() {

		fileEndpointSet = SADUtils.readEndpointsFromFile(sourceFile);

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
			URLConnection conn = dHubURL.openConnection();
			InputStream inStream = conn.getInputStream();

			JsonObject json = JSON.parse(inStream);
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
			logger.error("URL is not accessible at the moment");
		}
	}

}
