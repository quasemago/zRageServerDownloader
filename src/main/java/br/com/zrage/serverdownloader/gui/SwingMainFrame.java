package br.com.zrage.serverdownloader.gui;

import br.com.zrage.serverdownloader.core.DownloadManager;
import br.com.zrage.serverdownloader.core.models.GameServer;
import br.com.zrage.serverdownloader.core.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.util.List;
import java.util.Vector;

public class SwingMainFrame extends JDialog {
    private GameServer serverContext;

    public SwingMainFrame(JFrame parent, boolean modal) {
        super(parent, modal);
        utils.setSwingImageIcon(this);
        this.setTitle("zRageServerDownloader");
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        JPanel mainPanel = new JPanel();
        JScrollPane jScrollPane1 = new JScrollPane();
        JLabel jLabel1 = new JLabel();
        JLabel jLabel2 = new JLabel();
        JLabel fastdlUrlLabel = new JLabel();
        fastDLUrlTextField = new JTextField();
        startButton = new JButton();
        JToolBar jToolBar1 = new JToolBar();
        aboutButton = new JButton();
        typeMapsCheckBox = new JCheckBox();
        typeAssetsCheckBox = new JCheckBox();

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        mainPanel.registerKeyboardAction(evt ->
                onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        mainPanel.setBackground(new java.awt.Color(209, 209, 209));

        // Available Servers.
        List<GameServer> serverList = DownloadManager.getAvailableServersList().getServers();
        availableServersList = new javax.swing.JList<>(new Vector<>(serverList));
        availableServersList.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        availableServersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        availableServersList.addListSelectionListener(evt -> availableServersListValueChanged((GameServer) availableServersList.getSelectedValue()));

        // Snipped from: https://stackoverflow.com/questions/12478661/
        availableServersList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (renderer instanceof JLabel && value instanceof GameServer) {
                    ((JLabel) renderer).setText("- " + ((GameServer) value).getName());
                }
                return renderer;
            }
        });
        jScrollPane1.setViewportView(availableServersList);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Available Servers:");

        fastdlUrlLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        fastdlUrlLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fastdlUrlLabel.setText("FastDL Url:");
        fastdlUrlLabel.setPreferredSize(new java.awt.Dimension(37, 29));

        fastDLUrlTextField.setEditable(false);
        fastDLUrlTextField.setBackground(new java.awt.Color(255, 255, 255, 229));
        fastDLUrlTextField.setText("");

        startButton.setText("START");
        startButton.setEnabled(false);
        startButton.setBackground(new java.awt.Color(255, 255, 255));
        startButton.addActionListener(evt -> startButtonActionPerformed());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Type:");

        typeMapsCheckBox.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        typeMapsCheckBox.setText("Maps");
        typeMapsCheckBox.setSelected(true);
        typeMapsCheckBox.setFocusable(false);
        typeMapsCheckBox.setBackground(new java.awt.Color(209, 209, 209));
        typeMapsCheckBox.addActionListener(evt -> {
            if (typeMapsCheckBox.isSelected()) {
                typeAssetsCheckBox.setSelected(false);
            }
        });

        typeAssetsCheckBox.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        typeAssetsCheckBox.setText("Assets");
        typeAssetsCheckBox.setFocusable(false);
        typeAssetsCheckBox.setBackground(new java.awt.Color(209, 209, 209));
        typeAssetsCheckBox.addActionListener(evt -> {
            if (typeAssetsCheckBox.isSelected()) {
                typeMapsCheckBox.setSelected(false);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                                .addComponent(fastdlUrlLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(fastDLUrlTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGap(53, 53, 53)
                                                .addComponent(jLabel2)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(typeMapsCheckBox)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(typeAssetsCheckBox))
                                        .addComponent(jScrollPane1))
                                .addContainerGap())
                        .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2)
                                        .addComponent(typeAssetsCheckBox)
                                        .addComponent(typeMapsCheckBox))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(startButton, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                                        .addComponent(fastDLUrlTextField)
                                        .addComponent(fastdlUrlLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );

        jToolBar1.setRollover(false);
        jToolBar1.setFloatable(false);

        aboutButton.setText("About");
        aboutButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        aboutButton.setBackground(new java.awt.Color(255, 255, 255));
        aboutButton.setFocusable(false);
        aboutButton.setBorderPainted(false);
        aboutButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        aboutButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        aboutButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        aboutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                System.out.println("BLAH");
            }
        });

        jToolBar1.add(aboutButton);

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

    private void startButtonActionPerformed() {
        if (typeAssetsCheckBox.isSelected()) {
            SwingServerSelectedAssetsFrame.StartSwingServerFrame(serverContext);
        } else {
            // Maps is default value.
            SwingServerSelectedMapsFrame.StartSwingServerFrame(serverContext);
        }
    }

    private void availableServersListValueChanged(GameServer server) {
        serverContext = server;
        fastDLUrlTextField.setText(serverContext.getFastDLUrl());
        startButton.setEnabled(true);
    }

    private static void onCancel() {
        DownloadManager.deleteAllTempFiles();
        System.exit(0);
    }

    // Start swing main frame window.
    public static void StartSwingMainFrame() {
        // Make sure delete main temp folder.
        DownloadManager.deleteAllTempFiles();

        SwingMainFrame ex = new SwingMainFrame(new javax.swing.JFrame(), true);
        ex.setLocationRelativeTo(null);
        ex.setVisible(true);
    }

    // Variables declaration - do not modify
    private javax.swing.JList availableServersList;
    private JTextField fastDLUrlTextField;
    private JButton aboutButton;
    private JButton startButton;
    private JCheckBox typeMapsCheckBox;
    private JCheckBox typeAssetsCheckBox;
    // End of variables declaration
}
