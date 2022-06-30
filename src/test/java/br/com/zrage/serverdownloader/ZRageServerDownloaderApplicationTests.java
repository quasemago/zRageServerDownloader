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
        assetManager.downloadAsset(asset);
        assetManager.decompressAsset(asset);
        assetManager.moveToGameFolder(asset);


        List<GameMap> mapList = selectedServer.getGameMapList();
        MapManager mapManager = new MapManager(selectedServer);
        GameMap map = mapList.remove(0);
        System.out.println("fileName=" + map.getFileName());
        System.out.println("localFileName=" + map.getLocalFileName());
        System.out.println("remoteFileName=" + map.getRemoteFileName());
        */
    }
}