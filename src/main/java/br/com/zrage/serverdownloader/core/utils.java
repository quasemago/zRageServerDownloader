package br.com.zrage.serverdownloader.core;

import br.com.zrage.serverdownloader.core.models.GameServer;
import br.com.zrage.serverdownloader.core.models.ServersJson;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class utils {
    private static final String SERVERS_CONTEXT_REMOTEFILE  = "https://api.zrage.com.br/mapdownloader/getserverslist.php";

    public static String getServersContextRemoteFile() {
        return SERVERS_CONTEXT_REMOTEFILE;
    }

    public static ServersJson getServersJson() {
        ResponseEntity<ServersJson> responseEntity = new RestTemplate()
                .getForEntity(SERVERS_CONTEXT_REMOTEFILE, ServersJson.class);
        return responseEntity.getBody();
    }

    public static String normalizeUrl(String url) {
        if (url.charAt(url.length() - 1) != '/') {
            url += '/';
        }
        return url;
    }
}
