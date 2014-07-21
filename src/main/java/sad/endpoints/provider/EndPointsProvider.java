package sad.endpoints.provider;

import java.text.ParseException;
import java.util.Set;

/**
 * 
 * @author qaiser.mehmood@insight-centre.org
 */

public interface EndPointsProvider {

	public Set<String> provider() throws ParseException;
}
