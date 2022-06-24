package br.com.zrage.serverdownloader.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServersJson {
    @JsonProperty("servers")
    private List<GameServer> serverList;

    public ServersJson(List<GameServer> serverList) {
        this.serverList = serverList;
    }

    public List<GameServer> getServerList() {
        return serverList;
    }

    public void setServerList(List<GameServer> serverList) {
        this.serverList = serverList;
    }
}
