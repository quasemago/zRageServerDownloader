package br.com.zrage.serverdownloader.core.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameServer {
    @JsonProperty("name")
    private String name;
    @JsonProperty("mapList")
    private String csvMapListUrl;
    // TODO:
    @JsonIgnore
    private String assetsListUrl;
    @JsonProperty("fastDL")
    private String fastDLUrl;
    @JsonProperty("appID")
    private int steamAppId;
    @JsonProperty("mapsDirectory")
    private String mapsDirectory;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMapListUrl() {
        return csvMapListUrl;
    }

    public void setMapListUrl(String mapListUrl) {
        this.csvMapListUrl = mapListUrl;
    }

    public String getAssetsListUrl() {
        return assetsListUrl;
    }

    public void setAssetsListUrl(String assetsListUrl) {
        this.assetsListUrl = assetsListUrl;
    }

    public String getFastDLUrl() {
        return fastDLUrl;
    }

    public void setFastDLUrl(String fastDLUrl) {
        this.fastDLUrl = fastDLUrl;
    }

    public int getSteamAppId() {
        return steamAppId;
    }

    public void setSteamAppId(int steamAppId) {
        this.steamAppId = steamAppId;
    }

    public String getMapsDirectoryPath() {
        final String registryValue = Advapi32Util.registryGetStringValue(
                WinReg.HKEY_CURRENT_USER, "Software\\Valve\\Steam", "SteamPath");

        if (!registryValue.isEmpty()) {
            // Resolve libraryfolders.vdf path.
            final Path libraryFoldersVdf = Paths.get(registryValue.replace("/", "\\") + "\\steamapps\\libraryfolders.vdf");

            // Check if libraryfolders.vdf exists.
            if (!Files.exists(libraryFoldersVdf)) {
                return "";
            }

            try {
                // Read libraryfolders.vdf content.
                final String libraryInfoFileContents = Files.readString(libraryFoldersVdf);

                // Search for steam library folders paths.
                Pattern pattern = Pattern.compile("\"(\\d+?)\"[\\r\\n]{1,2}\\s*?\\{[\\r\\n]{1,2}\\s*?\"path\"\\s*?\"([^\"]*?)\"");
                Matcher matcher = pattern.matcher(libraryInfoFileContents);

                List<String> libraryFolders = new ArrayList<>();
                while (matcher.find()) {
                    libraryFolders.add(matcher.group(2));
                }

                // Search for appId game.
                for (String str : libraryFolders) {
                    str = str.replace("program files", "Program Files");

                    // Resolve acf file path.
                    final Path acfFile = Paths.get(str + "\\steamapps\\appmanifest_" + steamAppId + ".acf");

                    // Check if acf file exists.
                    if (!Files.exists(acfFile)) {
                        continue;
                    }

                    final String acfFileContents = Files.readString(acfFile);
                    final String installDir = acfFileContents.split("\"installdir\"		\"")[1].split("\"")[0];

                    // Resolve game app maps directory.
                    return (str + "\\steamapps\\common\\" + installDir + mapsDirectory).replace("\\\\", "\\");
                }
            } catch (IOException err) {
                err.printStackTrace();
            }
        }
        return "";
    }

    public List<GameMap> getGameMapList() {
        WebClient webClient = WebClient.create();
        String responseCSV = webClient.get()
                .uri(csvMapListUrl)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        List<String> mapList = Arrays.stream(responseCSV.replaceAll("(\r\n|\n\r|\r|\n)", "").split(",")).toList();
        List<GameMap> gameMapList = new ArrayList<>();
        for (String str : mapList) {
            gameMapList.add(new GameMap(str, this));
        }
        return gameMapList;
    }
}
