package br.com.zrage.serverdownloader.core;

import br.com.zrage.serverdownloader.core.models.GameServer;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

// TODO:
public class DownloadManager {
    protected static final Path mainTempFolderPath = Paths.get(System.getProperty("java.io.tmpdir")).resolve("zrageTempDownloader");
    protected final Path tempFolderPath;
    protected static JTextArea logTextArea;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public DownloadManager(GameServer server) {
        this.tempFolderPath = mainTempFolderPath.resolve(UUID.randomUUID().toString().toUpperCase());
        new File(tempFolderPath.toString()).mkdirs();
    }

    protected boolean download(String url, Path targetPath) {
        WebClient client = WebClient.builder()
                .baseUrl(url)
                .build();
        Flux<DataBuffer> dataBufferFlux = client.get()
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getRawStatusCode() == 404) {
                        return Flux.empty();
                    }
                    else {
                        return Mono.error(ex);
                    }
                });
        DataBufferUtils.write(dataBufferFlux, targetPath, StandardOpenOption.CREATE).block();

        try {
            return Files.size(targetPath) > 0;
        }
        catch (IOException err) {
            err.printStackTrace();
        }

        return false;
    }

    protected void decompress(String filePath, String targetPath) throws IOException {
        var input = new BZip2CompressorInputStream(new BufferedInputStream(new FileInputStream(filePath)));
        var output = new FileOutputStream(targetPath);
        try (input; output) {
            IOUtils.copy(input, output);
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    protected void moveFile(Path filePath, Path targetPath) {
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

    public static void setLogTextArea(JTextArea textArea) {
        logTextArea = textArea;
    }

    public static void appendToLogger(String str) {
        try {
            logTextArea.append(str + System.getProperty("line.separator"));
        } catch (NullPointerException err) {
            err.printStackTrace();
        }
    }
}
