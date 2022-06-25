package br.com.zrage.serverdownloader.core;

import br.com.zrage.serverdownloader.core.models.GameMap;
import br.com.zrage.serverdownloader.core.models.GameServer;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.*;
import java.lang.annotation.ElementType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public class MapManager {
    private static final Path mainTempFolder = Paths.get(System.getProperty("java.io.tmpdir")).resolve("zrageTempMaps");
    private final GameServer serverContext;
    private final Path tempFolder;
    private boolean canceled;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public MapManager(GameServer server) {
        this.tempFolder = mainTempFolder.resolve(UUID.randomUUID().toString().toUpperCase());
        new File(tempFolder.toString()).mkdirs();

        this.serverContext = server;
        this.canceled = false;
    }

    public void download(GameMap map) {
        String tempFile = tempFolder.resolve(map.getDownloadableFileName()).toString();
        WebClient client = WebClient.builder()
                .baseUrl(map.getRemoteFileName())
                .build();
        Flux<DataBuffer> dataBufferFlux = client.get().retrieve().bodyToFlux(DataBuffer.class);
        DataBufferUtils.write(dataBufferFlux, Paths.get(tempFile), StandardOpenOption.CREATE).block();
    }

    public void decompress(GameMap map) throws IOException {
        String tempFile = tempFolder.resolve(map.getDownloadableFileName()).toString();
        String decompressTempFile = tempFolder.resolve(map.getLocalFileName()).toString();

        var input = new BZip2CompressorInputStream(new BufferedInputStream(new FileInputStream(tempFile)));
        var output = new FileOutputStream(decompressTempFile);
        try (input; output) {
            IOUtils.copy(input, output);
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    public void moveToMapsFolder(GameMap map) {
        var mapsFolder = Paths.get(utils.getGameAppMapsDirectory(serverContext));
        if (!Files.exists(mapsFolder)) {
            return;
        }

        var tempFile = tempFolder.resolve(map.getLocalFileName());
        var finalFile = mapsFolder.resolve(map.getLocalFileName());

        try {
            Files.deleteIfExists(finalFile);
        } catch (IOException err) {
            err.printStackTrace();
        }

        try {
            Files.move(tempFile, finalFile);
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    public static void deleteAllTempFiles() {
        try {
            FileUtils.deleteDirectory(mainTempFolder.toFile());
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}
