package sad.endpoints.provider;

import java.text.ParseException;
import java.util.Set;

public interface EndPointsProvider {

	public Set <String> provider() throws ParseException;
}
