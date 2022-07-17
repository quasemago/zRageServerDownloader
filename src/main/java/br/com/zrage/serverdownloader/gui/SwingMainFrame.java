package br.com.zrage.serverdownloader.gui;

import br.com.zrage.serverdownloader.core.DownloadManager;
import br.com.zrage.serverdownloader.core.models.GameServer;
import br.com.zrage.serverdownloader.core.utils;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Vector;

// TODO: Use IntelliJ swing form.
public class SwingMainFrame extends JFrame {
    private static SwingMainFrame frameInstance;
    private GameServer serverContext;

    public SwingMainFrame() {
        super();
        this.setIconImage(utils.getResourceImageIcon("zrageplayer.png"));
        this.setTitle("zRageServerDownloader");

        // Init swing components.
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        /* Main panel. */
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new java.awt.Color(225, 225, 225));

        // call onCancelPanel() on ESCAPE.
        mainPanel.registerKeyboardAction(evt ->
                onCancelPanel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        /* Available servers section. */
        JLabel availableServersLabel = new JLabel();
        availableServersLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        availableServersLabel.setText("Select available servers:");

        List<GameServer> serverList = DownloadManager.getAvailableServersList().getServers();
        availableServersList = new JList<>(new Vector<>(serverList));
        availableServersList.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        availableServersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        availableServersList.addListSelectionListener(evt -> {
            serverContext = (GameServer) availableServersList.getSelectedValue();
            fastDLUrlTextField.setText(serverContext.getFastDLUrl());
            startButton.setEnabled(true);
        });

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

        JScrollPane availableServersPanel = new JScrollPane();
        availableServersPanel.setViewportView(availableServersList);

        /* Download types checkbox section. */
        JLabel typesLabel = new JLabel();
        typesLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        typesLabel.setText("Type:");

        typeMapsCheckBox = new JCheckBox();
        typeMapsCheckBox.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        typeMapsCheckBox.setText("Maps");
        typeMapsCheckBox.setSelected(true);
        typeMapsCheckBox.setFocusable(false);
        typeMapsCheckBox.setBackground(new java.awt.Color(225, 225, 225));
        typeMapsCheckBox.addActionListener(evt -> {
            if (typeMapsCheckBox.isSelected()) {
                typeAssetsCheckBox.setSelected(false);
            }
        });

        typeAssetsCheckBox = new JCheckBox();
        typeAssetsCheckBox.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        typeAssetsCheckBox.setText("Skins/Sounds");
        typeAssetsCheckBox.setFocusable(false);
        typeAssetsCheckBox.setBackground(new java.awt.Color(225, 225, 225));
        typeAssetsCheckBox.addActionListener(evt -> {
            if (typeAssetsCheckBox.isSelected()) {
                typeMapsCheckBox.setSelected(false);
            }
        });

        /* Footer fastdl section. */
        JLabel fastDLUrlLabel = new JLabel();
        fastDLUrlLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        fastDLUrlLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fastDLUrlLabel.setText("FastDL Url:");
        fastDLUrlLabel.setPreferredSize(new java.awt.Dimension(37, 29));

        fastDLUrlTextField = new JTextField();
        fastDLUrlTextField.setEditable(false);
        fastDLUrlTextField.setText("");

        startButton = new JButton();
        startButton.setText("START");
        startButton.setEnabled(false);
        startButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        startButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        startButton.addActionListener(evt -> {
            if (typeAssetsCheckBox.isSelected()) {
                SwingServerSelectedAssetsFrame.StartSwingServerFrame(frameInstance, serverContext);
            } else {
                // Maps is default value.
                SwingServerSelectedMapsFrame.StartSwingServerFrame(frameInstance, serverContext);
            }
        });

        /* Head toolbar buttons section. */
        JToolBar jToolBar1 = new JToolBar();
        jToolBar1.setRollover(false);
        jToolBar1.setFloatable(false);

        JButton aboutButton = new JButton();
        initToolBarButton(aboutButton, "About");
        aboutButton.addActionListener(evt -> {
            try {
                SwingAboutFrame.StartSwingAboutFrame(frameInstance);
            } catch (URISyntaxException err) {
                err.printStackTrace();
            }
        });
        jToolBar1.add(aboutButton);

        JButton helpButton = new JButton();
        initToolBarButton(helpButton, "Help");
        helpButton.addActionListener(evt -> {
            try {
                Desktop.getDesktop().browse(new URI("https://zrage.com.br"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        jToolBar1.add(helpButton);

        // Generated code by Netbeans form editor.
        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                                .addComponent(fastDLUrlLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(fastDLUrlTextField)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addComponent(availableServersLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                                                .addGap(47, 47, 47)
                                                .addComponent(typesLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(typeMapsCheckBox)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(typeAssetsCheckBox)
                                                .addGap(6, 6, 6))
                                        .addComponent(availableServersPanel))
                                .addContainerGap())
                        .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(availableServersLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(typesLabel)
                                        .addComponent(typeAssetsCheckBox)
                                        .addComponent(typeMapsCheckBox))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(availableServersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(startButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(fastDLUrlTextField)
                                        .addComponent(fastDLUrlLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE))
                                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
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

    private void initToolBarButton(JButton button, String text) {
        button.setText(text);
        button.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        //aboutButton.setBackground(new java.awt.Color(225, 225, 225));
        button.setFocusable(false);
        //aboutButton.setBorderPainted(false);
        //aboutButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    }

    private static void onCancelPanel() {
        DownloadManager.deleteAllTempFiles();
        System.exit(0);
    }

    // Start swing main frame window.
    public static void StartSwingMainFrame() {
        // Make sure delete main temp folder.
        DownloadManager.deleteAllTempFiles();

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception err) {
            err.printStackTrace();
        }

        frameInstance = new SwingMainFrame();
        frameInstance.setLocationRelativeTo(null);
        frameInstance.setVisible(true);
    }

    // Java swing vars declaration.
    private JList availableServersList;
    private JTextField fastDLUrlTextField;
    private JButton startButton;
    private JCheckBox typeMapsCheckBox;
    private JCheckBox typeAssetsCheckBox;
}
