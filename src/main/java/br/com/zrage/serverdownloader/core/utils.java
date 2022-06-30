package br.com.zrage.serverdownloader.core;

import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import java.io.IOException;
import java.io.InputStream;

public class utils {
    public static void setSwingImageIcon(JDialog swingFrame) {
        try {
            final InputStream input = new ClassPathResource("zrageplayer.png").getInputStream();
            swingFrame.setIconImage(ImageIO.read(input));
        } catch (IOException err) {
            err.printStackTrace();
        }
    }
}
