package example.com;

import com.micex.client.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AstsClientConfiguration {

	@Bean
	public Client astsClient() {
		return new Client();
	}
}
