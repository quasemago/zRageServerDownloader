package br.com.zrage.serverdownloader.core.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameServer {
    @JsonProperty("name")
    private String name;
    @JsonProperty("mapList")
    private String mapListUrl;
    @JsonProperty("fastDL")
    private String fastDlUrl;
    @JsonProperty("appID")
    private int gameAppId;
    @JsonProperty("mapsDirectory")
    private String mapsDirectory;

    public GameServer() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMapListUrl() {
        return mapListUrl;
    }

    public void setMapListUrl(String mapListUrl) {
        this.mapListUrl = mapListUrl;
    }

    public String getFastDlUrl() {
        return fastDlUrl;
    }

    public void setFastDlUrl(String fastDlUrl) {
        this.fastDlUrl = fastDlUrl;
    }

    public int getGameAppId() {
        return gameAppId;
    }

    public void setGameAppId(int gameAppId) {
        this.gameAppId = gameAppId;
    }

    public String getMapsDirectory() {
        return mapsDirectory;
    }

    public void setMapsDirectory(String mapsDirectory) {
        this.mapsDirectory = mapsDirectory;
    }
}
