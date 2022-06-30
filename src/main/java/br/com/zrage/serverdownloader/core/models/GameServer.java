package br.com.zrage.serverdownloader.core.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameServer {
    @JsonProperty("name")
    private String name;
    @JsonProperty("mapListUrl")
    private String csvMapListUrl;
    @JsonProperty("assetsListUrl")
    private String assetsListUrl;
    @JsonProperty("fastDLUrl")
    private String fastDLUrl;
    @JsonProperty("steamAppId")
    private int steamAppId;
    @JsonProperty("gameDirectory")
    private String gameDirectory;
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

    public String getGameDirectory() {
        return gameDirectory;
    }

    public String getGameDirectoryPath() {
        final String registryValue = Advapi32Util.registryGetStringValue(
                WinReg.HKEY_CURRENT_USER, "Software\\Valve\\Steam", "SteamPath");

        if (!registryValue.isEmpty()) {
            // Resolve libraryfolders.vdf path.
            final Path libraryFoldersVdf = Paths.get(registryValue.replace("/", "\\") + "\\steamapps\\libraryfolders.vdf");

            // Check if libraryfolders.vdf exists.
            if (!Files.exists(libraryFoldersVdf)) {
                return "";
            }

            StringBuilder strBuilder = new StringBuilder();

            // Read libraryfolders.vdf content.
            try (Stream<String> stream = Files.lines(libraryFoldersVdf, StandardCharsets.UTF_8)) {
                //Read the content with Stream
                stream.forEach(s -> strBuilder.append(s).append("\n"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            final String libraryInfoFileContents = strBuilder.toString();

            // Search for steam library folders paths.
            Pattern pattern = Pattern.compile("\"(\\d+?)\"[\\r\\n]{1,2}\\s*?\\{[\\r\\n]{1,2}\\s*?\"path\"\\s*?\"([^\"]*?)\"");
            Matcher matcher = pattern.matcher(libraryInfoFileContents);

            List<String> libraryFolders = new ArrayList<>();
            while (matcher.find()) {
                libraryFolders.add(matcher.group(2));
            }

            // Search for appId game.
            strBuilder.setLength(0);
            for (String str : libraryFolders) {
                str = str.replace("program files", "Program Files");

                // Resolve acf file path.
                final Path acfFile = Paths.get(str + "\\steamapps\\appmanifest_" + steamAppId + ".acf");

                // Check if acf file exists.
                if (!Files.exists(acfFile)) {
                    continue;
                }

                try (Stream<String> stream = Files.lines(acfFile, StandardCharsets.UTF_8)) {
                    //Read the content with Stream
                    stream.forEach(s -> strBuilder.append(s).append("\n"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final String acfFileContents = strBuilder.toString();
                final String installDir = acfFileContents.split("\"installdir\"		\"")[1].split("\"")[0];

                // Resolve game app maps directory.
                return (str + "\\steamapps\\common\\" + installDir + gameDirectory).replace("\\\\", "\\");
            }
        }
        return "";
    }

    public String getMapsDirectory() {
        return mapsDirectory;
    }

    public String getMapsDirectoryPath() {
        return getGameDirectoryPath() + mapsDirectory;
    }

    public List<GameMap> getGameMapList() {
        WebClient webClient = WebClient.create();
        String responseCSV = webClient.get()
                .uri(csvMapListUrl)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        List<String> mapList = Arrays.stream(responseCSV.replaceAll("(\r\n|\n\r|\r|\n)", "").split(",")).collect(Collectors.toList());
        List<GameMap> gameMapList = new ArrayList<>();

        for (String str : mapList) {
            gameMapList.add(new GameMap(str, this));
        }

        return gameMapList;
    }

    public List<GameAsset> getGameAssetsList() {
        WebClient webClient = WebClient.create();
        String response = webClient.get()
                .uri(assetsListUrl)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        List<String> assetsList = Arrays.stream(response.replaceAll("(\r\n|\n\r|\r|\n)", "").split(";")).collect(Collectors.toList());
        List<GameAsset> gameAssetsList = new ArrayList<>();

        for (String str : assetsList) {
            gameAssetsList.add(new GameAsset(str, this));
        }

        return gameAssetsList;
    }
}
