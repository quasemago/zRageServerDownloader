package br.com.zrage.serverdownloader.gui;

import br.com.zrage.serverdownloader.core.AssetManager;
import br.com.zrage.serverdownloader.core.DownloadManager;
import br.com.zrage.serverdownloader.core.MapManager;
import br.com.zrage.serverdownloader.core.models.GameAsset;
import br.com.zrage.serverdownloader.core.models.GameMap;
import br.com.zrage.serverdownloader.core.models.GameServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;

public class SwingServerSelectedAssetsFrame extends JDialog implements PropertyChangeListener {
    private GameServer serverContext;
    private AssetManager assetManager;
    private List<GameAsset> assetsToDownload;
    private TaskSwing task;
    private boolean replaceExistingAssets;

    class TaskSwing extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            int progress = 0;
            setProgress(0);

            double selectedCount = assetsToDownload.size();
            while (!assetsToDownload.isEmpty() && !isCancelled()) {
                // Get first asset from list and remove.
                GameAsset asset = assetsToDownload.remove(0);

                // Skip asset if already exists, if enabled.
                if (!replaceExistingAssets && assetManager.assetExists(asset)) {
                    //DownloadManager.appendToLogger(asset.getFilePath() + " já existe, skipando!");

                    // Update progress.
                    progress++;
                    setProgress((int) Math.round(((double) progress / selectedCount) * 100));
                    continue;
                }

                // Test download asset.
                DownloadManager.appendToLogger("Baixando: " + asset.getRemoteFileName());
                if (!assetManager.downloadAsset(asset)) {
                    DownloadManager.appendToLogger("*ERROR* ao tentar baixar: " + asset.getRemoteFileName());
                    continue;
                }

                // Test decompress asset.
                DownloadManager.appendToLogger("Extraindo: " + asset.getFilePath());
                assetManager.decompressAsset(asset);

                // Move to asset directory.
                assetManager.moveToGameFolder(asset);

                // Update progress.
                progress++;
                setProgress((int) Math.round(((double) progress / selectedCount) * 100));
            }
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            cancelDownloadButton.setEnabled(false);
            cancelDownloadButton.setVisible(false);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Toolkit.getDefaultToolkit().beep();
            downloadAssetsButton.setEnabled(true);
            downloadAssetsButton.setVisible(true);
            replaceExistingAssetsCheckBox.setEnabled(true);
            setCursor(null); //turn off the wait cursor

            // Reset download list.
            assetsToDownload = serverContext.getGameAssetsList();
            DownloadManager.appendToLogger("Download finalizado!");
        }
    }

    public SwingServerSelectedAssetsFrame(Frame parent, boolean modal, GameServer server) {
        super(parent, modal);

        // Set server context.
        this.serverContext = server;

        // Init map manager.
        this.assetManager = new AssetManager(serverContext);

        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        this.setTitle("zRageServerDownloader: " + serverContext.getName());

        // Init swing components.
        mainPanel = new JPanel();
        jLabel1 = new JLabel();
        assetsDirTextField = new javax.swing.JTextField();
        fileChooseButton = new JButton();
        replaceExistingAssetsCheckBox = new JCheckBox();
        downloadAssetsButton = new JButton();
        cancelDownloadButton = new JButton();
        jScrollPane1 = new JScrollPane();
        progressTextArea = new JTextArea();
        progressBar = new JProgressBar();
        jMenuBar1 = new JMenuBar();
        aboutMenuItem = new JMenu();

        // Inicializa todos mapas selecionados de padrão.
        // TODO: select maps to download.
        assetsToDownload = serverContext.getGameAssetsList();

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        mainPanel.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        mainPanel.setBackground(new Color(209, 209, 209));

        jLabel1.setFont(new Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Game directory:");

        assetsDirTextField.setText(serverContext.getGameDirectoryPath());

        fileChooseButton.setText("...");
        fileChooseButton.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileChooseButtonActionPerformed();
            }
        });

        replaceExistingAssets = false;
        replaceExistingAssetsCheckBox.setFont(new Font("Segoe UI", 1, 14)); // NOI18N
        replaceExistingAssetsCheckBox.setText("Replace any existing asset");
        replaceExistingAssetsCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceExistingAssets = replaceExistingAssetsCheckBox.isSelected();
            }
        });

        downloadAssetsButton.setFont(new Font("Segoe UI", 1, 13)); // NOI18N
        downloadAssetsButton.setText("Download Assets ");
        downloadAssetsButton.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadAssetsButtonActionPerformed();
            }
        });
        downloadAssetsButton.setEnabled(true);

        cancelDownloadButton.setFont(new Font("Segoe UI", 1, 13)); // NOI18N
        cancelDownloadButton.setText("Cancel");
        cancelDownloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (task != null) {
                    task.cancel(true);
                }
            }
        });
        cancelDownloadButton.setEnabled(false);
        cancelDownloadButton.setVisible(false);

        progressTextArea.setEditable(false);
        progressTextArea.setColumns(20);
        progressTextArea.setRows(5);
        jScrollPane1.setViewportView(progressTextArea);
        DownloadManager.setLogTextArea(progressTextArea);

        progressBar.setForeground(new Color(0, 153, 0));
        progressBar.setValue(0);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 638, Short.MAX_VALUE)
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addComponent(assetsDirTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 607, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(fileChooseButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(assetsDirTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                                        .addComponent(fileChooseButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(replaceExistingAssetsCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                                        .addComponent(downloadAssetsButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(cancelDownloadButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        jMenuBar1.setPreferredSize(new Dimension(34, 29));

        aboutMenuItem.setText("About");
        aboutMenuItem.setFont(new Font("Segoe UI", 1, 14)); // NOI18N
        jMenuBar1.add(aboutMenuItem);

        setJMenuBar(jMenuBar1);

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

        pack();
    }

    private void fileChooseButtonActionPerformed() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.showOpenDialog(this);

        File file = fc.getSelectedFile();
        if (file == null) {
            return;
        }

        if (file.exists()) {
            String newAssetsDirPath = file.getPath();

            assetsDirTextField.setText(newAssetsDirPath);
            assetManager.setGameDirectoryPath(Paths.get(newAssetsDirPath));
        }
    }

    private void downloadAssetsButtonActionPerformed() {
        downloadAssetsButton.setEnabled(false);
        downloadAssetsButton.setVisible(false);
        replaceExistingAssetsCheckBox.setEnabled(false);
        cancelDownloadButton.setEnabled(true);
        cancelDownloadButton.setVisible(true);

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

    public void onCancel() {
        if (task != null) {
            task.cancel(true);
        }
        dispose();
    }

    // Start swing selected server frame.
    public static void StartSwingServerFrame(GameServer server) {
        SwingServerSelectedAssetsFrame ex = new SwingServerSelectedAssetsFrame(new JFrame(), true, server);
        ex.setLocationRelativeTo(null);
        ex.setVisible(true);
    }

    // Variables declaration - do not modify
    private JMenu aboutMenuItem;
    private JButton downloadAssetsButton;
    private JButton cancelDownloadButton;
    private JButton fileChooseButton;
    private JLabel jLabel1;
    private JMenuBar jMenuBar1;
    private JScrollPane jScrollPane1;
    private JPanel mainPanel;
    private JTextField assetsDirTextField;
    private JProgressBar progressBar;
    private JTextArea progressTextArea;
    private JCheckBox replaceExistingAssetsCheckBox;
    // End of variables declaration
}
