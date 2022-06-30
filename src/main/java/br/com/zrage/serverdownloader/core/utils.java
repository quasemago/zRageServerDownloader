package br.com.zrage.serverdownloader.core;

import br.com.zrage.serverdownloader.core.models.ServersList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class utils {
    private static final String SERVERS_CONTEXT_REMOTEFILE = "https://api.zrage.com.br/mapdownloader/getServersList.php";

    public static String getServersContextRemoteFile() {
        return SERVERS_CONTEXT_REMOTEFILE;
    }

    public static ServersList getAvailableServersList() {
        ResponseEntity<ServersList> responseEntity = new RestTemplate()
                .getForEntity(SERVERS_CONTEXT_REMOTEFILE, ServersList.class);
        return responseEntity.getBody();
    }

    public static String normalizeUrl(String url) {
        if (url.charAt(url.length() - 1) != '/') {
            url += '/';
        }
        return url;
    }
}
