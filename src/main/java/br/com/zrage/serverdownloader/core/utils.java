package br.com.zrage.serverdownloader.core;

import br.com.zrage.serverdownloader.core.models.ServersList;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;

public class utils {
    private static final String SERVERS_CONTEXT_REMOTEFILE = "https://api.zrage.com.br/mapdownloader/getServersList.php";

    public static String getServersContextRemoteFile() {
        return SERVERS_CONTEXT_REMOTEFILE;
    }

    public static ServersList getAvailableServersList() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Application");
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        return restTemplate.exchange(SERVERS_CONTEXT_REMOTEFILE, HttpMethod.GET, entity, ServersList.class).getBody();
    }

    public static String normalizeUrl(String url) {
        if (url.charAt(url.length() - 1) != '/') {
            url += '/';
        }
        return url;
    }
}
