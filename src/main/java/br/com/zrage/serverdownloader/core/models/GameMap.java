package br.com.zrage.serverdownloader.core.models;

import java.nio.file.Files;
import java.nio.file.Path;

public class GameMap {
    private String name;
    private String fileName; // name.bsp{.bz2}
    private String localFileName; // name.bsp
    private String remoteFileName; // fastdlUrl + fileName
    private boolean isCompressed;

    public GameMap(String rawName, GameServer server) {
        String fileExt = ".bsp";
        this.isCompressed = rawName.charAt(0) != '$';

        if (isCompressed) {
            fileExt += ".bz2";
        }

        // Remove the $ prefix if exists
        this.name = rawName.replace("$", "");

        // Resolve names.
        this.fileName = name + fileExt;
        this.localFileName = name + ".bsp";
        this.remoteFileName = server.getFastDLUrl() + server.getMapsDirectory().replace("\\", "") + "/" + fileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLocalFileName() {
        return localFileName;
    }

    public void setLocalFileName(String localFileName) {
        this.localFileName = localFileName;
    }

    public boolean isCompressed() {
        return isCompressed;
    }

    public void setCompressed(boolean compressed) {
        isCompressed = compressed;
    }

    public String getRemoteFileName() {
        return remoteFileName;
    }

    public void setRemoteFileName(String remoteFileName) {
        this.remoteFileName = remoteFileName;
    }

    public boolean existsInFolder(Path folder) {
        final Path targetFile = folder.resolve(this.getLocalFileName());
        return Files.exists(targetFile);
    }
}
