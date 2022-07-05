package br.com.zrage.serverdownloader.gui;

import br.com.zrage.serverdownloader.core.DownloadManager;
import br.com.zrage.serverdownloader.core.models.GameAsset;
import br.com.zrage.serverdownloader.core.models.GameServer;
import br.com.zrage.serverdownloader.core.utils;
import br.com.zrage.serverdownloader.gui.swingutils.JSmartScroller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SwingServerSelectedAssetsFrame extends JDialog implements PropertyChangeListener {
    private final GameServer serverContext;
    private final DownloadManager downloadManager;
    private TaskSwing task;

    private class TaskSwing extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() {
            // Download starting.
            setProgress(0);

            downloadManager.setDownloadFailed(false);
            downloadManager.setDownloadCanceled(false);

            // Get selected game dir and validate if exists.
            Path gameDirPath = Paths.get(gameDirTextField.getText());
            if (!Files.exists(gameDirPath)) {
                downloadManager.appendToSwingLogger("*Error*: Selected game directory not found!");
                downloadManager.setDownloadFailed(true);
                return null;
            }

            downloadManager.setGameDirectoryPath(gameDirPath);

            // Get available assets to download.
            downloadManager.appendToSwingLogger("Fetching pending assets to download from \"" + serverContext.getName() + "\" server!");

            List<GameAsset> assetsList = downloadManager.getAssetsToDownload(replaceExistingAssetsCheckBox.isSelected());
            if (assetsList.isEmpty()) {
                // Update progress bar.
                setProgress(100);
                return null;
            }

            downloadManager.appendToSwingLogger("Fetched " + assetsList.size() + " pending assets to download from \"" + serverContext.getName() + "\" server!");

            int progress = 0;
            double selectedCount = assetsList.size();

            for (GameAsset asset : assetsList) {
                // Task swing canceled.
                if (isCancelled() || downloadManager.isDownloadCanceled()) {
                    break;
                }

                // Download asset.
                downloadManager.appendToSwingLogger("Downloading: " + asset.getFilePath());
                if (!downloadManager.download(asset)) {
                    downloadManager.appendToSwingLogger("*Error Downloading*: " + asset.getRemoteFileName());
                    continue;
                }

                // Decompress asset.
                downloadManager.appendToSwingLogger("Extracting: " + asset.getFilePath());
                downloadManager.decompress(asset);

                // Move to game directory.
                downloadManager.moveToGameFolder(asset);

                // Update progress.
                progress++;
                setProgress((int) Math.round(((double) progress / selectedCount) * 100));
            }
            return null;
        }

        @Override
        public void done() {
            // Disable/hide cancel button.
            cancelDownloadButton.setEnabled(false);
            cancelDownloadButton.setVisible(false);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Enable download button again and others fields.
            downloadAssetsButton.setEnabled(true);
            downloadAssetsButton.setVisible(true);
            replaceExistingAssetsCheckBox.setEnabled(true);
            gameDirChooseButton.setEnabled(true);
            gameDirTextField.setEditable(true);

            // Download completed alert.
            Toolkit.getDefaultToolkit().beep();
            setCursor(null); //turn off the wait cursor

            if (downloadManager.isDownloadCanceled()) {
                progressBar.setValue(0);
                downloadManager.appendToSwingLogger("Download canceled!");
            } else {
                downloadManager.appendToSwingLogger(downloadManager.isDownloadFailed() ? "Download failed!" : "Download completed!");
            }
        }
    }

    public SwingServerSelectedAssetsFrame(Frame parent, boolean modal, GameServer server) {
        super(parent, modal);
        this.setIconImage(utils.getResourceImageIcon("zrageplayer.png"));
        this.setTitle("zRageServerDownloader: " + server.getName());

        this.serverContext = server;
        this.downloadManager = new DownloadManager(serverContext);

        // Init swing components.
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        /* Main panel. */
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new java.awt.Color(225, 225, 225));

        // call onCancel() on ESCAPE.
        mainPanel.registerKeyboardAction(evt ->
                onCancelPanel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        /* Game dir section. */
        JLabel gameDirLabel = new JLabel();
        gameDirLabel.setFont(new Font("Segoe UI", 1, 14)); // NOI18N
        gameDirLabel.setText("Game directory:");

        gameDirTextField = new JTextField();
        gameDirTextField.setText(serverContext.getGameDirectoryPath());

        gameDirChooseButton = new JButton();
        gameDirChooseButton.setText("...");
        gameDirChooseButton.addActionListener(evt -> gameDirChooseButtonActionPerformed());
        gameDirChooseButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        /* Replace existing checkbox section. */
        replaceExistingAssetsCheckBox = new JCheckBox();
        replaceExistingAssetsCheckBox.setFont(new Font("Segoe UI", 1, 14)); // NOI18N
        replaceExistingAssetsCheckBox.setText("Replace any existing asset");
        replaceExistingAssetsCheckBox.setBackground(new java.awt.Color(225, 225, 225));

        /* Download/cancel buttons section */
        downloadAssetsButton = new JButton();
        downloadAssetsButton.setFont(new Font("Segoe UI", 1, 13)); // NOI18N
        downloadAssetsButton.setText("Download Assets ");
        downloadAssetsButton.addActionListener(evt -> downloadAssetsButtonActionPerformed());
        downloadAssetsButton.setEnabled(true);
        downloadAssetsButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        cancelDownloadButton = new JButton();
        cancelDownloadButton.setFont(new Font("Segoe UI", 1, 13)); // NOI18N
        cancelDownloadButton.setText("Cancel Download");
        cancelDownloadButton.addActionListener(evt -> {
            cancelDownloadButton.setEnabled(false);
            downloadManager.setDownloadCanceled(true);
            downloadManager.appendToSwingLogger("Canceling download. Waiting for end of the current process...");
        });
        cancelDownloadButton.setEnabled(false);
        cancelDownloadButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cancelDownloadButton.setVisible(false);

        /* Download progress area section */
        JTextArea progressTextArea = new JTextArea();
        progressTextArea.setEditable(false);
        progressTextArea.setColumns(20);

        progressBar = new JProgressBar();
        progressBar.setForeground(new Color(0, 153, 0));
        progressBar.setValue(0);

        JScrollPane progressTextPanel = new JScrollPane();
        progressTextPanel.setViewportView(progressTextArea);
        new JSmartScroller(progressTextPanel);
        downloadManager.setSwingLoggerTextArea(progressTextArea);

        // Generated code by Netbeans form editor.
        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(progressTextPanel)
                                        .addComponent(gameDirLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 638, Short.MAX_VALUE)
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addComponent(gameDirTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 607, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(gameDirChooseButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addComponent(replaceExistingAssetsCheckBox)
                                                .addGap(296, 296, 296)
                                                .addComponent(downloadAssetsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(cancelDownloadButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(gameDirLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(gameDirTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                                        .addComponent(gameDirChooseButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(replaceExistingAssetsCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                                        .addComponent(downloadAssetsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(cancelDownloadButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(progressTextPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        // call onCancel() when cross is clicked.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                onCancelPanel();
            }
        });

        pack();
    }

    private void gameDirChooseButtonActionPerformed() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.showOpenDialog(this);

        File file = fc.getSelectedFile();
        if (file != null && file.exists()) {
            gameDirTextField.setText(file.getPath());
        }
    }

    private void downloadAssetsButtonActionPerformed() {
        // Disable/hide download button and others fields.
        downloadAssetsButton.setEnabled(false);
        downloadAssetsButton.setVisible(false);
        replaceExistingAssetsCheckBox.setEnabled(false);
        gameDirChooseButton.setEnabled(false);
        gameDirTextField.setEditable(false);

        // Enable/show cancel button.
        cancelDownloadButton.setEnabled(true);
        cancelDownloadButton.setVisible(true);

        // Reset progressbar and set wait cursor.
        progressBar.setValue(0);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // Create download task.
        task = new TaskSwing();
        task.addPropertyChangeListener(this);
        task.execute();
    }

    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("progress")) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
        }
    }

    public void onCancelPanel() {
        if (task != null) {
            task.cancel(true);
        }
        dispose();
    }

    // Start swing selected server frame.
    public static void StartSwingServerFrame(SwingMainFrame frameInstance, GameServer server) {
        SwingServerSelectedAssetsFrame ex = new SwingServerSelectedAssetsFrame(frameInstance, true, server);
        ex.setLocationRelativeTo(null);
        ex.setVisible(true);
    }

    // Java swing vars declaration
    private JButton gameDirChooseButton;
    private JButton downloadAssetsButton;
    private JButton cancelDownloadButton;
    private JTextField gameDirTextField;
    private JProgressBar progressBar;
    private JCheckBox replaceExistingAssetsCheckBox;
}
