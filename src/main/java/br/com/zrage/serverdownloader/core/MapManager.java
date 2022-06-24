package br.com.zrage.serverdownloader.core;

import br.com.zrage.serverdownloader.core.models.GameMap;
import br.com.zrage.serverdownloader.core.models.GameServer;
import org.asynchttpclient.*;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

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
        String tempFile = tempFolder.resolve(map.getDownloadableFileName()).toString();
        WebClient client = WebClient.builder()
                .baseUrl(map.getRemoteFileName())
                .build();
        Flux<DataBuffer> dataBufferFlux = client.get().retrieve().bodyToFlux(DataBuffer.class);
        DataBufferUtils.write(dataBufferFlux, Paths.get(tempFile), StandardOpenOption.CREATE).block();
    }

    /*
    public void download(GameMap map) throws IOException, ExecutionException, InterruptedException {
        String tempFile = tempFolder.resolve(map.getDownloadableFileName()).toString();

        FileOutputStream stream = new FileOutputStream(tempFile);
        AsyncHttpClient client = Dsl.asyncHttpClient();

        client.prepareGet(map.getRemoteFileName())
                .execute(new AsyncCompletionHandler<FileOutputStream>() {

                    @Override
                    public State onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
                        stream.getChannel()
                                .write(bodyPart.getBodyByteBuffer());
                        return State.CONTINUE;
                    }

                    @Override
                    public FileOutputStream onCompleted(Response response) throws Exception {
                        return stream;
                    }
                })
                .get();

        stream.getChannel().close();
        client.close();
    }
     */

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
