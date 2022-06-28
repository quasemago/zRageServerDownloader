package br.com.zrage.serverdownloader.gui;

import br.com.zrage.serverdownloader.core.DownloadManager;
import br.com.zrage.serverdownloader.core.MapManager;
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

public class SwingServerSelectedFrame extends JDialog implements PropertyChangeListener {
    private GameServer serverContext;
    private MapManager mapManager;
    // TODO: Implement select maps to download frame.
    private List<GameMap> selectedMapsToDownload;
    private TaskSwing task;
    private boolean replaceExistingMaps;

    class TaskSwing extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            int progress = 0;
            setProgress(0);

            double selectedCount = selectedMapsToDownload.size();
            while (!selectedMapsToDownload.isEmpty() && !isCancelled()) {
                // Get first map from list and remove.
                GameMap map = selectedMapsToDownload.remove(0);

                // Skip map if already exists, if enabled.
                if (!replaceExistingMaps && mapManager.mapExists(map)) {
                    DownloadManager.appendToLogger("Mapa " + map.getName() + " já existe, skipando!");

                    // Update progress.
                    progress++;
                    setProgress((int) Math.round(((double) progress / selectedCount) * 100));
                    continue;
                }

                // Test download map.
                DownloadManager.appendToLogger("Baixando mapa " + map.getName());
                System.out.println("Baixando mapa " + map.getName());
                mapManager.downloadMap(map);

                // Test decompress map.
                if (map.isCompressed()) {
                    DownloadManager.appendToLogger("Extraindo mapa " + map.getName());
                    mapManager.decompressMap(map);
                }

                // Move to maps directory.
                DownloadManager.appendToLogger("Movendo " + map.getName() + " mapa para a pasta do jogo");
                mapManager.moveToMapsFolder(map);

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
            downloadMapsButton.setEnabled(true);
            downloadMapsButton.setVisible(true);
            replaceExistingMapsCheckBox.setEnabled(true);
            setCursor(null); //turn off the wait cursor

            // Delete temp folder after download.
            progressBar.setValue(0);
            DownloadManager.appendToLogger("Download finalizado!");
            DownloadManager.deleteAllTempFiles();
        }
    }

    public SwingServerSelectedFrame(java.awt.Frame parent, boolean modal, GameServer server) {
        super(parent, modal);

        // Set server context.
        this.serverContext = server;

        // Init map manager.
        this.mapManager = new MapManager(serverContext);

        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        this.setTitle("zRageServerDownloader: " + serverContext.getName());

        // Init swing components.
        mainPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        mapsDirTextField = new javax.swing.JTextField();
        fileChooseButton = new javax.swing.JButton();
        replaceExistingMapsCheckBox = new javax.swing.JCheckBox();
        selectMapsButton = new javax.swing.JButton();
        selectedMapsLabel = new javax.swing.JLabel();
        downloadMapsButton = new javax.swing.JButton();
        cancelDownloadButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        progressTextArea = new javax.swing.JTextArea();
        progressBar = new javax.swing.JProgressBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        aboutMenuItem = new javax.swing.JMenu();

        // Inicializa todos mapas selecionados de padrão.
        // TODO: select maps to download.
        selectedMapsToDownload = serverContext.getGameMapList();
        int totalMaps = serverContext.getGameMapList().size();

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

        mainPanel.setBackground(new java.awt.Color(209, 209, 209));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Maps directory:");

        mapsDirTextField.setText(serverContext.getMapsDirectoryPath());

        fileChooseButton.setText("...");
        fileChooseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileChooseButtonActionPerformed();
            }
        });

        replaceExistingMaps = false;
        replaceExistingMapsCheckBox.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        replaceExistingMapsCheckBox.setText("Replace any existing maps");
        replaceExistingMapsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceExistingMaps = replaceExistingMapsCheckBox.isSelected();
            }
        });

        selectMapsButton.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        selectMapsButton.setText("Select Maps");
        selectMapsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectMapsButtonActionPerformed();
            }
        });

        selectedMapsLabel.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        selectedMapsLabel.setText(selectedMapsToDownload.size() + " / " + totalMaps + " maps");

        downloadMapsButton.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        downloadMapsButton.setText("Download Maps");
        downloadMapsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadMapsButtonActionPerformed();
            }
        });
        downloadMapsButton.setEnabled(true);

        cancelDownloadButton.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        cancelDownloadButton.setText("Cancel");
        cancelDownloadButton.addActionListener(new java.awt.event.ActionListener() {
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

        progressBar.setForeground(new java.awt.Color(0, 153, 0));
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
                                                .addComponent(mapsDirTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 607, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(fileChooseButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addComponent(replaceExistingMapsCheckBox)
                                                .addGap(80, 80, 80)
                                                .addComponent(selectMapsButton)
                                                .addGap(18, 18, 18)
                                                .addComponent(selectedMapsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(downloadMapsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                                        .addComponent(mapsDirTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                                        .addComponent(fileChooseButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(replaceExistingMapsCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                                        .addComponent(downloadMapsButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(cancelDownloadButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(selectMapsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(selectedMapsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        jMenuBar1.setPreferredSize(new java.awt.Dimension(34, 29));

        aboutMenuItem.setText("About");
        aboutMenuItem.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jMenuBar1.add(aboutMenuItem);

        setJMenuBar(jMenuBar1);

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
            String newMapsDirPath = file.getPath();

            mapsDirTextField.setText(newMapsDirPath);
            mapManager.setMapsDirectoryPath(Paths.get(newMapsDirPath));
        }
    }

    private void selectMapsButtonActionPerformed() {
        // TODO add your handling code here:
    }

    private void downloadMapsButtonActionPerformed() {
        downloadMapsButton.setEnabled(false);
        downloadMapsButton.setVisible(false);
        replaceExistingMapsCheckBox.setEnabled(false);
        cancelDownloadButton.setEnabled(true);
        cancelDownloadButton.setVisible(true);
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
        SwingServerSelectedFrame ex = new SwingServerSelectedFrame(new javax.swing.JFrame(), true, server);
        ex.setLocationRelativeTo(null);
        ex.setVisible(true);
    }

    // Variables declaration - do not modify
    private javax.swing.JMenu aboutMenuItem;
    private javax.swing.JButton downloadMapsButton;
    private javax.swing.JButton cancelDownloadButton;
    private javax.swing.JButton fileChooseButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField mapsDirTextField;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextArea progressTextArea;
    private javax.swing.JCheckBox replaceExistingMapsCheckBox;
    private javax.swing.JButton selectMapsButton;
    private javax.swing.JLabel selectedMapsLabel;
    // End of variables declaration
}
