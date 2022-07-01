package br.com.zrage.serverdownloader.core;

import br.com.zrage.serverdownloader.core.models.GameAsset;
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

import javax.swing.JTextArea;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

// TODO:
public class DownloadManager {
    private static final String SERVERS_CONTEXT_REMOTEFILE = "https://api.zrage.com.br/mapdownloader/getServersList.php";
    protected static final Path mainTempFolderPath = Paths.get(System.getProperty("java.io.tmpdir")).resolve("zrageTempDownloader");
    protected final Path tempFolderPath;
    protected static JTextArea swingLoggerTextArea;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public DownloadManager() {
        this.tempFolderPath = mainTempFolderPath.resolve(UUID.randomUUID().toString().toUpperCase());
        new File(tempFolderPath.toString()).mkdirs();
    }

    public static ServersList getAvailableServersList() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Application");
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        return restTemplate.exchange(SERVERS_CONTEXT_REMOTEFILE, HttpMethod.GET, entity, ServersList.class).getBody();
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

    protected void decompress(Path filePath, String targetPath) throws IOException {
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

    public static void setSwingLoggerTextArea(JTextArea textArea) {
        swingLoggerTextArea = textArea;
    }

    public static void appendToSwingLogger(String str) {
        try {
            swingLoggerTextArea.append(str + System.getProperty("line.separator"));
        } catch (NullPointerException err) {
            err.printStackTrace();
        }
    }
}
