package br.com.zrage.serverdownloader.core;

import br.com.zrage.serverdownloader.core.models.GameServer;
import br.com.zrage.serverdownloader.core.models.ServersJson;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class utils {
    private static final String SERVERS_CONTEXT_REMOTEFILE = "https://api.zrage.com.br/mapdownloader/getserverslist.php";

    public static String getServersContextRemoteFile() {
        return SERVERS_CONTEXT_REMOTEFILE;
    }

    public static ServersJson getServersJson() {
        ResponseEntity<ServersJson> responseEntity = new RestTemplate()
                .getForEntity(SERVERS_CONTEXT_REMOTEFILE, ServersJson.class);
        return responseEntity.getBody();
    }

    public static String getGameAppMapsDirectory(GameServer server) {
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
                    final Path acfFile = Paths.get(str + "\\steamapps\\appmanifest_" + server.getGameAppId() + ".acf");

                    // Check if acf file exists.
                    if (!Files.exists(acfFile)) {
                        continue;
                    }

                    final String acfFileContents = Files.readString(acfFile);
                    final String installDir = acfFileContents.split("\"installdir\"		\"")[1].split("\"")[0];

                    // Resolve game app maps directory.
                    return str + "\\steamapps\\common\\" + installDir + server.getMapsDirectory();
                }
            } catch (IOException err) {
                err.printStackTrace();
            }
        }
        return "";
    }

    public static String normalizeUrl(String url) {
        if (url.charAt(url.length() - 1) != '/') {
            url += '/';
        }
        return url;
    }
}
