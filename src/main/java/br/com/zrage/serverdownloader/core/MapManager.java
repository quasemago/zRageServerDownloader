package br.com.zrage.serverdownloader.core;

import br.com.zrage.serverdownloader.core.models.GameMap;
import br.com.zrage.serverdownloader.core.models.GameServer;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public class MapManager {
    private static final Path mainTempFolder = Paths.get(System.getProperty("java.io.tmpdir")).resolve("zrageTempMaps");
    private Path tempFolder;
    private String mapsDirectory;
    private GameServer serverContext;
    private boolean canceled;

    public MapManager(GameServer server) {
        this.tempFolder = mainTempFolder.resolve(UUID.randomUUID().toString().toUpperCase());
        new File(tempFolder.toString()).mkdirs();

        this.serverContext = server;
        this.canceled = false;
    }

    public void download(GameMap map) {
        // TODO:
    }

    public void decompress(GameMap map) {
        // TODO:
    }

    public void moveToMapsFolder(GameMap map) {
        // TODO:
    }

    public static void deleteAllTempFiles() {
        // TODO:
    }

    private static void tryDeleteFile(String file) {
        // TODO:
    }

    public static String getLastMapsFolder() {
        // TODO:
        return "";
    }

    public static void saveLastMapsFolder(String mapsFolder) {
        // TODO:
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}
