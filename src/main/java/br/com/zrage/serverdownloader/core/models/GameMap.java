package br.com.zrage.serverdownloader.core.models;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GameMap {
    private GameServer serverContext;
    private String name;
    private String downloadableFileName;
    private String localFileName;
    private boolean isCompressed;
    private String remoteFileName;
    private boolean skipOnDownload;

    // Display porpuses
    private boolean visible;

    public GameMap(String rawName, GameServer server) {
        this.serverContext = server;

        String fileExt = ".bsp";
        this.isCompressed = rawName.charAt(0) != '$';

        if (isCompressed) {
            fileExt += ".bz2";
        }

        // Remove the $ prefix if exists
        this.name = rawName.replace("$", "");

        // Resolve names.
        this.downloadableFileName = name + fileExt;
        this.localFileName = name + ".bsp";
        this.remoteFileName = serverContext.getFastDlUrl() + downloadableFileName;
    }

    public boolean existsInMapsFolder() {
        // TODO: implement existsInMapsFolder;
        return false;
    }

    public GameServer getServerContext() {
        return serverContext;
    }

    public void setServerContext(GameServer serverContext) {
        this.serverContext = serverContext;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDownloadableFileName() {
        return downloadableFileName;
    }

    public void setDownloadableFileName(String downloadableFileName) {
        this.downloadableFileName = downloadableFileName;
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

    public boolean isSkipOnDownload() {
        return skipOnDownload;
    }

    public void setSkipOnDownload(boolean skipOnDownload) {
        this.skipOnDownload = skipOnDownload;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public String toString() {
        return name;
    }
}
