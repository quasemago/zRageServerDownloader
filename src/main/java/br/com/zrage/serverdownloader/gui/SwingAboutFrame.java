package br.com.zrage.serverdownloader.gui;

import br.com.zrage.serverdownloader.core.utils;
import br.com.zrage.serverdownloader.gui.swingutils.JLinkLabel;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.net.URI;
import java.net.URISyntaxException;

// TODO: Use IntelliJ swing form.
public class SwingAboutFrame extends JDialog {
    public SwingAboutFrame(JFrame parent, boolean modal) throws URISyntaxException {
        super(parent, modal);
        this.setIconImage(utils.getResourceImageIcon("zrageplayer.png"));
        this.setTitle("zRageServerDownloader: About");

        // Init swing components.
        initComponents();
    }

    private void initComponents() throws URISyntaxException {
        /* About panel. */
        JPanel aboutPanel = new JPanel();
        aboutPanel.setBackground(new java.awt.Color(225, 225, 225));

        /* ZRage Logo section. */
        JLabel zRageIconLabel = new JLabel();
        zRageIconLabel.setIcon(new ImageIcon(utils.getResourceImageIcon("zragelogo.png"))); // NOI18N
        zRageIconLabel.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 2, new java.awt.Color(200, 200, 200)));

        /* Information section. */
        JLabel infoTitleLabel = new JLabel();
        infoTitleLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        infoTitleLabel.setText("Information");
        infoTitleLabel.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));

        JLabel zRageLabel = new JLabel();
        zRageLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        zRageLabel.setText("ZRage server assets downloader");

        JLabel websiteLabel = new JLabel();
        websiteLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        websiteLabel.setText("Website:");
        JLinkLabel websiteLinkLabel = new JLinkLabel(new URI("https://zrage.com.br"), "https://zrage.com.br");
        initJLinkLabel(websiteLinkLabel);

        JLabel discordLabel = new JLabel();
        discordLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        discordLabel.setText("Discord:");
        JLinkLabel discordLinkLabel = new JLinkLabel(new URI("http://discord.zrage.com.br"), "http://discord.zrage.com.br");
        initJLinkLabel(discordLinkLabel);

        JLabel steamGroupLabel = new JLabel();
        steamGroupLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        steamGroupLabel.setText("Steam Group:");
        JLinkLabel steamGroupLinkLabel = new JLinkLabel(new URI("http://grupo.zrage.com.br"), "http://grupo.zrage.com.br");
        initJLinkLabel(steamGroupLinkLabel);

        /* App information section. */
        JLabel appInfoTitleLabel = new JLabel();
        appInfoTitleLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        appInfoTitleLabel.setText("App Information");
        appInfoTitleLabel.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));

        JLabel versionLabel = new JLabel();
        versionLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        versionLabel.setText(utils.VERSION);

        JLabel createdByLabel = new JLabel();
        createdByLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        createdByLabel.setText("Created by: Bruno \"quasemago\" Ronning");

        JLabel githubLabel = new JLabel();
        githubLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        githubLabel.setText("GitHub:");
        JLinkLabel githubLinkLabel = new JLinkLabel(new URI("https://github.com/quasemago"), "https://github.com/quasemago");
        initJLinkLabel(githubLinkLabel);

        // Generated code by Netbeans form editor.
        javax.swing.GroupLayout aboutPanelLayout = new javax.swing.GroupLayout(aboutPanel);
        aboutPanel.setLayout(aboutPanelLayout);
        aboutPanelLayout.setHorizontalGroup(
                aboutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(aboutPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(zRageIconLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(aboutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(aboutPanelLayout.createSequentialGroup()
                                                .addComponent(steamGroupLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(steamGroupLinkLabel))
                                        .addGroup(aboutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, aboutPanelLayout.createSequentialGroup()
                                                        .addComponent(discordLabel)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(discordLinkLabel))
                                                .addComponent(zRageLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, aboutPanelLayout.createSequentialGroup()
                                                        .addComponent(websiteLabel)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(websiteLinkLabel))
                                                .addComponent(infoTitleLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(aboutPanelLayout.createSequentialGroup()
                                                .addComponent(githubLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(githubLinkLabel))
                                        .addComponent(createdByLabel)
                                        .addComponent(versionLabel)
                                        .addComponent(appInfoTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        aboutPanelLayout.setVerticalGroup(
                aboutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(aboutPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(infoTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(zRageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(aboutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(websiteLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(websiteLinkLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(aboutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(discordLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(discordLinkLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(8, 8, 8)
                                .addGroup(aboutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(steamGroupLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(steamGroupLinkLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(appInfoTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(versionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(createdByLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(aboutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(githubLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(githubLinkLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(zRageIconLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(aboutPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 610, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(aboutPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
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

    private void initJLinkLabel(JLinkLabel jLinkLabel) {
        jLinkLabel.init();
        jLinkLabel.setFocusable(false);
        jLinkLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLinkLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLinkLabel.setBorder(null);
        jLinkLabel.setBackground(new java.awt.Color(225, 225, 225));
    }

    private void onCancelPanel() {
        dispose();
    }

    // Start swing about frame.
    public static void StartSwingAboutFrame(SwingMainFrame frameInstance) throws URISyntaxException {
        SwingAboutFrame ex = new SwingAboutFrame(frameInstance, true);
        ex.setLocationRelativeTo(null);
        ex.setVisible(true);
    }
}
