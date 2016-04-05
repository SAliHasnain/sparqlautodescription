package sad.initializer;

import java.io.IOException;
import org.apache.commons.configuration.ConfigurationException;
import org.json.simple.JSONObject;

/**
 * 
 * @author qaiser.mehmood@insight-centre.org
 */
public interface LoadConfigurations {

	public JSONObject loadConfiguration() throws IOException,
			ConfigurationException;

}
