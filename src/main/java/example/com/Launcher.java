package example.com;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;

@Slf4j
public class Launcher {
	public static void main(String[] args) {
		SpringApplication.run(Launcher.class, args);
		log.info("Application 'marketdata-loader-asts' has been launched.");
	}
}
