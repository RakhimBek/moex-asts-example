package example.com;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties("asts")
@Configuration
public class AstsConnectionProperties {
	private final Map<String, String> params = new HashMap<>();

	public Map<String, String> getParams() {
		return params;
	}
}
