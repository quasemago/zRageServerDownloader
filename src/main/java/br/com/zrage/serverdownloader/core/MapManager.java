package br.com.zrage.serverdownloader.core;

import br.com.zrage.serverdownloader.core.models.GameMap;
import br.com.zrage.serverdownloader.core.models.GameServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MapManager extends DownloadManager {
    private final GameServer serverContext;
    private Path mapsDirectoryPath;

    public MapManager(GameServer server) {
        super();
        this.serverContext = server;

        // Persiste o path da pasta "maps" do jogo.
        this.mapsDirectoryPath = Paths.get(server.getMapsDirectoryPath());
    }

    public Path getMapsDirectoryPath() {
        return mapsDirectoryPath;
    }

    public void setMapsDirectoryPath(Path path) {
        this.mapsDirectoryPath = path;
    }

    public boolean download(GameMap map) {
        final Path tempFile = tempFolderPath.resolve(map.getFileName());
        return this.download(map.getRemoteFileName(), tempFile);
    }

    public void decompress(GameMap map) {
        final Path tempFile = tempFolderPath.resolve(map.getFileName());
        final String targetTempFile = tempFolderPath.resolve(map.getLocalFileName()).toString();

        try {
            this.decompress(tempFile, targetTempFile);
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    public void moveToMapsFolder(GameMap map) {
        if (!Files.exists(mapsDirectoryPath)) {
            return;
        }

        final Path tempFile = tempFolderPath.resolve(map.getLocalFileName());
        final Path targetFile = mapsDirectoryPath.resolve(map.getLocalFileName());

        this.moveFile(tempFile, targetFile);
    }

    public List<GameMap> getMapsToDownload(boolean replaceIfExists) {
        final List<GameMap> serverMapList = serverContext.getGameMapList();
        List<GameMap> mapList = new ArrayList<>();

        for (GameMap map : serverMapList) {
            if (map.existsInFolder(mapsDirectoryPath) && !replaceIfExists) {
                continue;
            }
            mapList.add(map);
        }
        return mapList;
    }
}