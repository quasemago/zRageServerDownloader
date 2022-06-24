package br.com.zrage.serverdownloader;

import br.com.zrage.serverdownloader.core.models.GameServer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

@SpringBootTest
class ZRageServerDownloaderApplicationTests {
	private static final String SERVERS_CONTEXT_JSON = "https://raw.githubusercontent.com/ZombieRage/public/master/MapDownloaderApp/servers.json";
	private static final Logger log = LoggerFactory.getLogger(ZRageServerDownloaderApplication.class);

	@Test
	void contextLoads() {
		// Get Servers json.

		ResponseEntity<List<GameServer>> responseEntity =
				restTemplate.exchange(
						SERVERS_CONTEXT_JSON,
						HttpMethod.GET,
						null,
						new ParameterizedTypeReference<List<GameServer>>() {}
				);
		List<GameServer> servers = responseEntity.getBody();

	}

}
