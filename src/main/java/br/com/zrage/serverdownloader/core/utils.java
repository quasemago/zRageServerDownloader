package br.com.zrage.serverdownloader.core;

import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class utils {
    public static BufferedImage getResourceImageIcon(String resource) {
        try {
            final InputStream input = new ClassPathResource(resource).getInputStream();
            return ImageIO.read(input);
        } catch (IOException err) {
            err.printStackTrace();
        }
        return null;
    }
}
