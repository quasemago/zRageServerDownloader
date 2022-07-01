package br.com.zrage.serverdownloader.core;

import br.com.zrage.serverdownloader.core.models.GameAsset;
import br.com.zrage.serverdownloader.core.models.GameServer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AssetManager extends DownloadManager {
    private final GameServer serverContext;
    private Path gameDirectoryPath;

    public AssetManager(GameServer server) {
        super();
        this.serverContext = server;

        // Persiste o path da pasta do jogo.
        this.gameDirectoryPath = Paths.get(server.getGameDirectoryPath());
    }

    public Path getGameDirectoryPath() {
        return gameDirectoryPath;
    }

    public void setGameDirectoryPath(Path path) {
        this.gameDirectoryPath = path;
    }

    public boolean download(GameAsset asset) {
        final Path tempFile = tempFolderPath.resolve(asset.getFileName());
        return this.download(asset.getRemoteFileName(), tempFile);
    }

    public void decompress(GameAsset asset) {
        final Path tempFile = tempFolderPath.resolve(asset.getFileName());
        final String targetTempFile = tempFolderPath.resolve(asset.getLocalFileName()).toString();

        try {
            this.decompress(tempFile, targetTempFile);
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    public void moveToGameFolder(GameAsset asset) {
        if (!Files.exists(gameDirectoryPath)) {
            return;
        }

        final Path tempFile = tempFolderPath.resolve(asset.getLocalFileName());
        final Path targetFile = gameDirectoryPath.resolve(asset.getFilePath());

        // TODO: Uma melhor maneira de fazer isso?
        // Cria os subdiretorios necessários.
        File file = new File(targetFile.toString());
        File parentFile = file.getParentFile();
        parentFile.mkdirs();

        this.moveFile(tempFile, targetFile);
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
}