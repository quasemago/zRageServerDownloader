package br.com.zrage.serverdownloader.core.models;

public class GameAsset {
    private String localFileName;
    private String fileName;
    private String filePath;
    private String remoteFileName;
    private boolean failed;

    public GameAsset(String rawFileName, GameServer server) {
        // models/to/folder/model_example.mdl
        this.filePath = rawFileName;

        // model_example.mdl
        this.localFileName = rawFileName.substring(rawFileName.lastIndexOf("\\") + 1);

        // model_example.mdl.bz2
        this.fileName = localFileName + ".bz2";

        // fastdlUrl + fileName
        this.remoteFileName = (server.getFastDLUrl() + filePath + ".bz2").replaceAll("\\\\", "/");
    }

    public String getLocalFileName() {
        return localFileName;
    }

    public void setLocalFileName(String localFileName) {
        this.localFileName = localFileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getRemoteFileName() {
        return remoteFileName;
    }

    public void setRemoteFileName(String remoteFileName) {
        this.remoteFileName = remoteFileName;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public boolean isFailed() {
        return failed;
    }
}