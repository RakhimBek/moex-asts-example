package example.com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class AstsService {

	@Autowired
	private AstsConnectionProperties astsConnectionProperties;

	@PostConstruct
	private void show() {
		System.out.println(astsConnectionProperties.getParams());
	}
}
