package br.com.zrage.serverdownloader.gui;

import br.com.zrage.serverdownloader.core.DownloadManager;
import br.com.zrage.serverdownloader.core.MapManager;
import br.com.zrage.serverdownloader.core.models.GameMap;
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

public class SwingServerSelectedMapsFrame extends JDialog implements PropertyChangeListener {
    private final GameServer serverContext;
    private final MapManager mapManager;
    private TaskSwing task;
    private boolean downloadFailed;
    private boolean downloadCanceled;

    private class TaskSwing extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() {
            // Download starting.
            setProgress(0);

            downloadFailed = false;
            downloadCanceled = false;

            // Get selected game dir and validate if exists.
            Path gameDirPath = Paths.get(mapsDirTextField.getText());
            if (!Files.exists(gameDirPath)) {
                DownloadManager.appendToSwingLogger("*Error*: Selected game maps directory not found!");
                downloadFailed = true;
                return null;
            }

            mapManager.setMapsDirectoryPath(gameDirPath);

            // Get available maps to download.
            List<GameMap> mapList = mapManager.getMapsToDownload(replaceExistingMapsCheckBox.isSelected());
            if (mapList.isEmpty()) {
                return null;
            }

            DownloadManager.appendToSwingLogger("Fetched " + mapList.size() + " pending maps to download from \"" + serverContext.getName() + "\" server!");

            int progress = 0;
            double selectedCount = mapList.size();

            for (GameMap map : mapList) {
                // Task swing canceled.
                if (isCancelled() || downloadCanceled) {
                    break;
                }

                // Download map.
                DownloadManager.appendToSwingLogger("Downloading map: " + map.getName());
                if (!mapManager.download(map)) {
                    DownloadManager.appendToSwingLogger("*Error Downloading*: " + map.getRemoteFileName());
                    continue;
                }

                // Decompress map.
                if (map.isCompressed()) {
                    DownloadManager.appendToSwingLogger("Extracting map: " + map.getName());
                    mapManager.decompress(map);
                }

                // Move to maps directory.
                mapManager.moveToMapsFolder(map);

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
            downloadMapsButton.setEnabled(true);
            downloadMapsButton.setVisible(true);
            replaceExistingMapsCheckBox.setEnabled(true);
            mapsDirChooseButton.setEnabled(true);
            mapsDirTextField.setEditable(true);

            // Download completed alert.
            Toolkit.getDefaultToolkit().beep();
            setCursor(null); //turn off the wait cursor

            if (downloadCanceled) {
                progressBar.setValue(0);
                DownloadManager.appendToSwingLogger("Download canceled!");
            } else {
                DownloadManager.appendToSwingLogger(downloadFailed ? "Download failed!" : "Download completed!");
            }
        }
    }

    public SwingServerSelectedMapsFrame(java.awt.Frame parent, boolean modal, GameServer server) {
        super(parent, modal);
        this.setIconImage(utils.getResourceImageIcon("zrageplayer.png"));
        this.setTitle("zRageServerDownloader: " + server.getName());

        this.serverContext = server;
        this.mapManager = new MapManager(serverContext);

        // Init swing components.
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        /* Main panel. */
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new java.awt.Color(209, 209, 209));

        // call onCancel() on ESCAPE.
        mainPanel.registerKeyboardAction(evt ->
                onCancelPanel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        /* Maps dir section. */
        JLabel mapsDirLabel = new JLabel();
        mapsDirLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        mapsDirLabel.setText("Game maps directory:");

        mapsDirTextField = new JTextField();
        mapsDirTextField.setText(serverContext.getMapsDirectoryPath());

        mapsDirChooseButton = new JButton();
        mapsDirChooseButton.setText("...");
        mapsDirChooseButton.setBackground(new java.awt.Color(255, 255, 255));
        mapsDirChooseButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        mapsDirChooseButton.addActionListener(evt -> mapsDirChooseButtonActionPerformed());

        /* Replace existing checkbox section. */
        replaceExistingMapsCheckBox = new JCheckBox();
        replaceExistingMapsCheckBox.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        replaceExistingMapsCheckBox.setText("Replace any existing maps");
        replaceExistingMapsCheckBox.setBackground(new java.awt.Color(209, 209, 209));

        /* Download/cancel buttons section */
        downloadMapsButton = new JButton();
        downloadMapsButton.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        downloadMapsButton.setText("Download Maps");
        downloadMapsButton.setBackground(new java.awt.Color(255, 255, 255));
        downloadMapsButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        downloadMapsButton.addActionListener(evt -> downloadMapsButtonActionPerformed());
        downloadMapsButton.setEnabled(true);

        cancelDownloadButton = new JButton();
        cancelDownloadButton.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        cancelDownloadButton.setText("Cancel Download");
        cancelDownloadButton.setBackground(new java.awt.Color(255, 255, 255));
        cancelDownloadButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cancelDownloadButton.addActionListener(evt -> {
            cancelDownloadButton.setEnabled(false);
            downloadCanceled = true;
            DownloadManager.appendToSwingLogger("Canceling download...");
        });
        cancelDownloadButton.setEnabled(false);
        cancelDownloadButton.setVisible(false);

        /* Download progress area section */
        JTextArea progressTextArea = new JTextArea();
        progressTextArea.setEditable(false);
        progressTextArea.setColumns(20);
        progressTextArea.setRows(5);

        progressBar = new JProgressBar();
        progressBar.setForeground(new java.awt.Color(0, 153, 0));
        progressBar.setValue(0);

        JScrollPane progressTextPanel = new JScrollPane();
        progressTextPanel.setViewportView(progressTextArea);
        new JSmartScroller(progressTextPanel);
        DownloadManager.setSwingLoggerTextArea(progressTextArea);

        // Generated code by Netbeans form editor.
        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(progressTextPanel)
                                        .addComponent(mapsDirLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 638, Short.MAX_VALUE)
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addComponent(mapsDirTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 607, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(mapsDirChooseButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addComponent(replaceExistingMapsCheckBox)
                                                .addGap(296, 296, 296)
                                                .addComponent(downloadMapsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(cancelDownloadButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(mapsDirLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(mapsDirTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                                        .addComponent(mapsDirChooseButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(downloadMapsButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                                        .addComponent(cancelDownloadButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                                        .addComponent(replaceExistingMapsCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(progressTextPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        // call onCancelPanel() when cross is clicked.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                onCancelPanel();
            }
        });

        pack();
    }

    private void mapsDirChooseButtonActionPerformed() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.showOpenDialog(this);

        File file = fc.getSelectedFile();
        if (file != null && file.exists()) {
            mapsDirTextField.setText(file.getPath());
        }
    }

    private void downloadMapsButtonActionPerformed() {
        // Disable/hide download button and others fields.
        downloadMapsButton.setEnabled(false);
        downloadMapsButton.setVisible(false);
        replaceExistingMapsCheckBox.setEnabled(false);
        mapsDirChooseButton.setEnabled(false);
        mapsDirTextField.setEditable(false);

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
    public static void StartSwingServerFrame(GameServer server) {
        SwingServerSelectedMapsFrame ex = new SwingServerSelectedMapsFrame(new javax.swing.JFrame(), true, server);
        ex.setLocationRelativeTo(null);
        ex.setVisible(true);
    }

    // Java swing vars declaration
    private JButton mapsDirChooseButton;
    private JButton downloadMapsButton;
    private JButton cancelDownloadButton;
    private JTextField mapsDirTextField;
    private JProgressBar progressBar;
    private JCheckBox replaceExistingMapsCheckBox;
}
