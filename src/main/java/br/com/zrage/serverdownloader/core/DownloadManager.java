package br.com.zrage.serverdownloader.core;

import br.com.zrage.serverdownloader.core.models.GameAsset;
import br.com.zrage.serverdownloader.core.models.GameMap;
import br.com.zrage.serverdownloader.core.models.GameServer;
import br.com.zrage.serverdownloader.core.models.ServersList;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

// TODO:
public class DownloadManager {
    private static final String SERVERS_CONTEXT_REMOTEFILE = "https://api.zrage.com.br/mapdownloader/getServersList.php";
    private static final Path mainTempFolderPath = Paths.get(System.getProperty("java.io.tmpdir")).resolve("zrageTempDownloader");
    private final Path tempFolderPath;
    private Path gameDirectoryPath;
    private Path mapsDirectoryPath;
    private final GameServer serverContext;
    private JTextArea swingLoggerTextArea;
    private boolean downloadFailed;
    private AtomicBoolean downloadCanceled = new AtomicBoolean();
    private boolean parallelDownload;
    private int progress;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public DownloadManager(GameServer server) {
        this.serverContext = server;
        this.parallelDownload = false;

        this.tempFolderPath = mainTempFolderPath.resolve(UUID.randomUUID().toString().toUpperCase());
        new File(tempFolderPath.toString()).mkdirs();

        this.gameDirectoryPath = Paths.get(serverContext.getGameDirectoryPath());
        this.mapsDirectoryPath = Paths.get(serverContext.getMapsDirectoryPath());
    }

    public Path getGameDirectoryPath() {
        return gameDirectoryPath;
    }

    public void setGameDirectoryPath(Path path) {
        this.gameDirectoryPath = path;
    }

    public Path getMapsDirectoryPath() {
        return mapsDirectoryPath;
    }

    public void setMapsDirectoryPath(Path path) {
        this.mapsDirectoryPath = path;
    }

    public static ServersList getAvailableServersList() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Application");
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        return restTemplate.exchange(SERVERS_CONTEXT_REMOTEFILE, HttpMethod.GET, entity, ServersList.class).getBody();
    }

    public List<GameMap> getMapsToDownload(boolean replaceIfExists) {
        final List<GameMap> serverMapList = serverContext.getGameMapList();
        List<GameMap> mapList = new ArrayList<>();

        for (GameMap map : serverMapList) {
            if (map.existsInFolder(mapsDirectoryPath) && !replaceIfExists) {
                continue;
            }
            mapList.add(map);
        }
        return mapList;
    }

    public List<GameAsset> getAssetsToDownload(boolean replaceIfExists) {
        final List<GameAsset> serverAssetsList = serverContext.getGameAssetsList();
        List<GameAsset> assetsList = new ArrayList<>();

        for (GameAsset asset : serverAssetsList) {
            if (asset.existsInFolder(gameDirectoryPath) && !replaceIfExists) {
                continue;
            }
            assetsList.add(asset);
        }
        return assetsList;
    }

    public boolean download(GameMap map) {
        final Path tempFile = tempFolderPath.resolve(map.getFileName());
        return this.download(map.getRemoteFileName(), tempFile);
    }

    public boolean download(GameAsset asset) {
        final Path tempFile = tempFolderPath.resolve(asset.getFilePath());

        // Create parent subdirs.
        final File file = new File(tempFile.toString());
        final File parentFile = file.getParentFile();
        parentFile.mkdirs();

        return this.download(asset.getRemoteFilePath(), tempFile);
    }

    private boolean download(String url, Path targetPath) {
        WebClient client = WebClient.builder()
                .baseUrl(url)
                .build();
        Flux<DataBuffer> dataBufferFlux = client.get()
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getRawStatusCode() == 404) {
                        return Flux.empty();
                    } else {
                        return Mono.error(ex);
                    }
                });
        DataBufferUtils.write(dataBufferFlux, targetPath, StandardOpenOption.CREATE).block();

        // Sleep in parallel download.
        if (parallelDownload) {
            try {
                Thread.sleep(35);
            } catch (InterruptedException err) {
                err.printStackTrace();
            }
        }

        try {
            return Files.exists(targetPath) && Files.size(targetPath) > 0;
        } catch (IOException err) {
            err.printStackTrace();
        }

        return false;
    }

    public void decompress(GameMap map) {
        final Path tempFile = tempFolderPath.resolve(map.getFileName());
        final String targetTempFile = tempFolderPath.resolve(map.getLocalFileName()).toString();

        try {
            this.decompress(tempFile, targetTempFile);
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    public void decompress(GameAsset asset) {
        final Path tempFile = tempFolderPath.resolve(asset.getFilePath());
        final String targetTempFile = tempFolderPath.resolve(asset.getLocalFilePath()).toString();

        try {
            this.decompress(tempFile, targetTempFile);
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    private void decompress(Path filePath, String targetPath) throws IOException {
        BZip2CompressorInputStream input = new BZip2CompressorInputStream(new BufferedInputStream(Files.newInputStream(filePath)));
        FileOutputStream output = new FileOutputStream(targetPath);

        try {
            IOUtils.copy(input, output);
        } catch (IOException err) {
            err.printStackTrace();
        }

        input.close();
        output.close();

        // Delete temp input file.
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    public void moveToGameFolder(GameMap map) {
        if (!Files.exists(mapsDirectoryPath)) {
            return;
        }

        final Path tempFile = tempFolderPath.resolve(map.getLocalFileName());
        final Path targetFile = mapsDirectoryPath.resolve(map.getLocalFileName());

        this.moveFile(tempFile, targetFile);
    }

    public void moveToGameFolder(GameAsset asset) {
        if (!Files.exists(gameDirectoryPath)) {
            return;
        }

        final Path tempFile = tempFolderPath.resolve(asset.getLocalFilePath());
        final Path targetFile = gameDirectoryPath.resolve(asset.getLocalFilePath());

        // Create parent subdirs.
        final File file = new File(targetFile.toString());
        final File parentFile = file.getParentFile();
        parentFile.mkdirs();

        this.moveFile(tempFile, targetFile);
    }

    private void moveFile(Path filePath, Path targetPath) {
        try {
            Files.deleteIfExists(targetPath);
        } catch (IOException err) {
            err.printStackTrace();
        }

        try {
            Files.move(filePath, targetPath);
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    public static void deleteAllTempFiles() {
        try {
            FileUtils.deleteDirectory(mainTempFolderPath.toFile());
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    public void setSwingLoggerTextArea(JTextArea textArea) {
        swingLoggerTextArea = textArea;
    }

    public void appendToSwingLogger(String str) {
        try {
            swingLoggerTextArea.append(str + System.getProperty("line.separator"));
        } catch (NullPointerException err) {
            err.printStackTrace();
        }
    }

    public boolean isDownloadFailed() {
        return downloadFailed;
    }

    public void setDownloadFailed(boolean downloadFailed) {
        this.downloadFailed = downloadFailed;
    }

    public boolean isDownloadCanceled() {
        return downloadCanceled.get();
    }

    public void setDownloadCanceled(boolean downloadCanceled) {
        this.downloadCanceled.set(downloadCanceled);
    }

    public void setParallelDownload(boolean parallel) {
        this.parallelDownload = parallel;
    }

    public boolean isParallelDownload() {
        return parallelDownload;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void increaseProgress() {
        this.progress++;
    }
}
