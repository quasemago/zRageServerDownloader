package br.com.zrage.serverdownloader;

import br.com.zrage.serverdownloader.core.AssetManager;
import br.com.zrage.serverdownloader.core.DownloadManager;
import br.com.zrage.serverdownloader.core.MapManager;
import br.com.zrage.serverdownloader.core.models.GameAsset;
import br.com.zrage.serverdownloader.core.models.GameMap;
import br.com.zrage.serverdownloader.core.models.GameServer;
import br.com.zrage.serverdownloader.core.models.ServersList;
import br.com.zrage.serverdownloader.core.utils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class ZRageServerDownloaderApplicationTests {

    @Test
    void contextLoads() throws IOException {
        /*
        // Get server from json.
        ServersList servers = utils.getAvailableServersList();
        GameServer selectedServer = servers.getServers().get(0); // ze
        List<GameAsset> assetsList = selectedServer.getGameAssetsList();

        GameAsset asset = assetsList.get(35);
        System.out.println("fileName=" + asset.getFileName());
        System.out.println("localFileName=" + asset.getLocalFileName());
        System.out.println("filePath=" + asset.getFilePath());
        System.out.println("remoteFileName=" + asset.getRemoteFileName());

        AssetManager assetManager = new AssetManager(selectedServer);
        assetManager.setGameDirectoryPath(Paths.get("C:\\Users\\bruno\\Desktop\\testdownload"));

        boolean status = assetManager.downloadAsset(asset);
        System.out.println("status=" + status);

        assetManager.decompressAsset(asset);
        assetManager.moveToGameFolder(asset);



        // Get map list from selected server.
        List<GameMap> mapList = selectedServer.getGameMapList();

        // Initialize map manager.
        MapManager mapManager = new MapManager(selectedServer);

        while(!mapList.isEmpty()) {
            // Get first map from list and remove.
            GameMap map = mapList.remove(0);

            // Test download map.
            System.out.println("Baixando mapa " + map.getName());
            mapManager.downloadMap(map);

            // Test decompress map.
            System.out.println("Extraindo mapa " + map.getName());
            mapManager.decompressMap(map);

            // Move to maps directory.
            System.out.println("Movendo mapa para a pasta do jogo = " + map.getName());
            mapManager.moveToMapsFolder(map);
        }

        // Delete temp folder after download.
        System.out.println("Apagando pasta temporária.");
        DownloadManager.deleteAllTempFiles();
         */
    }
}