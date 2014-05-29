package sad.endpoints.provider.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sad.endpoints.provider.EndPointsProvider;
import sad.utils.SADUtils;


public class DataHUB implements EndPointsProvider {

    Logger logger = LoggerFactory.getLogger("DataHUB");
    
    String sourceFile=System.getProperty("user.home")+File.separator+"qry"+File.separator+"liveEndpoints.txt";// to run on server
    private static final String dataHubURL, dataHubURI;
    Set<String> endpSet = Collections.synchronizedSet(new HashSet<String>());
    Set<String> fileEndpointSet = Collections.synchronizedSet(new HashSet<String>());
    static BufferedReader br = null;
    
    static {
        dataHubURL = "http://datahub.io/api/2/";
        dataHubURI = "search/resource?format=api/sparql&all_fields=1&limit=1000";
    }

    public Set<String> provider() {


		// endpSet.add("http://semantic.eea.europa.eu/sparql");
		// endpSet.add("http://roma.rkbexplorer.com/sparql/"); // generates warnings long loop for PREFIX void: <http://rdfs.org/ns/void#> CONSTRUCT { <datasetUri> void:classPartition [ void:class ?c ; void:propertyPartition [ void:property ?p ] ] } WHERE { ?s a ?c ; ?p ?o .}'
		// endpSet.add("http://s4.semanticscience.org:14006/sparql");
		// endpSet.add("http://s4.semanticscience.org:14002/sparql");
		// endpSet.add("http://epsrc.rkbexplorer.com/sparql");
		// endpSet.add("http://s4.semanticscience.org:14008/sparql");
		// endpSet.add("http://lod.b3kat.de/sparql");
		// endpSet.add("http://acm.rkbexplorer.com/sparql/");
		// endpSet.add("http://data.uni-muenster.de/sparql");
		// endpSet.add("http://biomodels.bio2rdf.org/sparql");
    	
    	
    	fileEndpointSet=SADUtils.readEndpointsFromFile(sourceFile);
    	
    	checkendPointsFromDATAHUB();

    	endpSet.addAll(fileEndpointSet);
    	
        return endpSet;
        
    }
    
    
   private  void checkendPointsFromDATAHUB(){

		try {
			URL dHubURL = new URL(dataHubURL + dataHubURI);
			URLConnection conn = dHubURL.openConnection();
			InputStream inStream = conn.getInputStream();

			JsonObject json = JSON.parse(inStream);
			JsonValue jsonValue = json.get("results");
			Iterator<JsonValue> itr = jsonValue.getAsArray().iterator();
			// int count=0;
			while (itr.hasNext()) {
				// endpSet.add(itr.next());
				// count++;
				JsonValue endpAdd = itr.next();
				if (!endpAdd.getAsObject().get("url").toString().equals(""))
					endpSet.add(endpAdd.getAsObject().get("url").toString().replace("\"", ""));
				// if (count>100)break;
			}

			logger.info("endpoints from datahub are {}", endpSet.size());
		} catch (MalformedURLException me) {
			logger.error("URL is malformed ");
		} catch (IOException e) {
			logger.error("URL is not accessible at the moment");
		}
    }
   
}
