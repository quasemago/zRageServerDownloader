package br.com.zrage.serverdownloader;

import br.com.zrage.serverdownloader.core.MapManager;
import br.com.zrage.serverdownloader.core.models.GameMap;
import br.com.zrage.serverdownloader.core.models.GameServer;
import br.com.zrage.serverdownloader.core.models.ServersJson;
import br.com.zrage.serverdownloader.core.utils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

@SpringBootTest
class ZRageServerDownloaderApplicationTests {

    @Test
    void contextLoads() throws IOException {
        // Get ze server from json.
        ServersJson serversJson = utils.getServersJson();
        GameServer zeServer = serversJson.getServerList().get(0);

        // Test download map.
        GameMap map = new GameMap("ze_Pirates_Port_Royal_v5_6", zeServer);
        MapManager mapManager = new MapManager(zeServer);
        mapManager.download(map);

        // Test decompress map.
        mapManager.decompress(map);

        // Move to maps directory.
        mapManager.moveToMapsFolder(map);

        // Delete temp folder after download.
        MapManager.deleteAllTempFiles();
    }
}