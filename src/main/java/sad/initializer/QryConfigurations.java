/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sad.initializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.configuration.ConfigurationException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author qaiser.mehmood@insight-centre.org
 */
public class QryConfigurations implements LoadConfigurations {

	static final Logger logger = LoggerFactory
			.getLogger(QryConfigurations.class);

	BufferedReader fileReader = null;
	JSONObject genreJsonObject = null;

	public JSONObject loadConfiguration() throws IOException,
			ConfigurationException {

		try {
			fileReader = new BufferedReader(new InputStreamReader(this
					.getClass().getResourceAsStream("/" + "queries.json")));
			genreJsonObject = (JSONObject) JSONValue
					.parseWithException(fileReader);

		} catch (ParseException e) {
			logger.error("json parsing exception {}", e);
		} catch (IOException ioe) {
			logger.error("File not loaded {}", ioe);
		}

		return genreJsonObject;

	}

}
