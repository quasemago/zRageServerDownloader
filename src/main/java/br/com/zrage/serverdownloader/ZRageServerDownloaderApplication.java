package br.com.zrage.serverdownloader;

import br.com.zrage.serverdownloader.gui.SwingMainFrame;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.awt.*;

@SpringBootApplication
public class ZRageServerDownloaderApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(ZRageServerDownloaderApplication.class)
                .headless(false).run(args);

        EventQueue.invokeLater(() -> {
            SwingMainFrame ex = new SwingMainFrame(new javax.swing.JFrame(), true);
            ex.setSize(600, 450);
            ex.setLocationRelativeTo(null);
            ex.setVisible(true);
        });
    }
}
