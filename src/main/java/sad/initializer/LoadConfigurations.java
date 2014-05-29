/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sad.initializer;

import java.io.IOException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.json.simple.JSONObject;

/**
 *
 * @author qaimeh
 */
public interface LoadConfigurations {
 
    public JSONObject loadConfiguration() throws  IOException, ConfigurationException;
    
}
