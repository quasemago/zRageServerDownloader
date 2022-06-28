package br.com.zrage.serverdownloader.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServersList {
    @JsonProperty("servers")
    private List<GameServer> servers;

    public List<GameServer> getServers() {
        return servers;
    }

    public void setServers(List<GameServer> list) {
        this.servers = list;
    }
}
