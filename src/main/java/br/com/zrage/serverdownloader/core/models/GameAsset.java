package br.com.zrage.serverdownloader.core.models;

import java.nio.file.Files;
import java.nio.file.Path;

public class GameAsset {
    private String localFilePath;
    private String filePath;
    private String remoteFilePath;

    public GameAsset(String rawFileName, GameServer server) {
        // (models/to/folder/model_example.mdl).
        this.localFilePath = rawFileName;

        // localFilePath + ext (models/to/folder/model_example.mdl.bz2).
        this.filePath = localFilePath + ".bz2";

        // fastdlUrl + filePath (https://fastdl.com/models/to/folder/model_example.mdl.bz2).
        this.remoteFilePath = (server.getFastDLUrl() + filePath).replaceAll("\\\\", "/");
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getRemoteFilePath() {
        return remoteFilePath;
    }

    public void setRemoteFilePath(String remoteFilePath) {
        this.remoteFilePath = remoteFilePath;
    }

    public boolean existsInFolder(Path folder) {
        final Path targetFile = folder.resolve(localFilePath);
        return Files.exists(targetFile);
    }
}