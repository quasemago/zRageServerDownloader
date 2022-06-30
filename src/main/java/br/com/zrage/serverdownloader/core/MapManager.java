package br.com.zrage.serverdownloader.core;

import br.com.zrage.serverdownloader.core.models.GameMap;
import br.com.zrage.serverdownloader.core.models.GameServer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MapManager extends DownloadManager {
    private Path mapsDirectoryPath;

    public MapManager(GameServer server) {
        super();

        // Persiste o path da pasta "maps" do jogo.
        this.mapsDirectoryPath = Paths.get(server.getMapsDirectoryPath());
    }

    public Path getMapsDirectoryPath() {
        return mapsDirectoryPath;
    }

    public void setMapsDirectoryPath(Path path) {
        this.mapsDirectoryPath = path;
    }

    public boolean downloadMap(GameMap map) {
        final Path tempFile = tempFolderPath.resolve(map.getFileName());
        return this.download(map.getRemoteFileName(), tempFile);
    }

    public void decompressMap(GameMap map) {
        final String tempFile = tempFolderPath.resolve(map.getFileName()).toString();
        final String targetTempFile = tempFolderPath.resolve(map.getLocalFileName()).toString();

        try {
            this.decompress(tempFile, targetTempFile);
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    public boolean mapExists(GameMap map) {
        final Path targetFile = mapsDirectoryPath.resolve(map.getLocalFileName());
        return Files.exists(targetFile);
    }

    public void moveToMapsFolder(GameMap map) {
        if (!Files.exists(mapsDirectoryPath)) {
            return;
        }

        final Path tempFile = tempFolderPath.resolve(map.getLocalFileName());
        final Path targetFile = mapsDirectoryPath.resolve(map.getLocalFileName());

        this.moveFile(tempFile, targetFile);
    }
}