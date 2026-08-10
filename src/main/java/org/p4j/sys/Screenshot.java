package org.p4j.sys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Screenshot {
    private static final Logger log = LoggerFactory.getLogger(Screenshot.class);

    public static void save(int[] pixelBuffer, int width, int height) {
        if (pixelBuffer == null || pixelBuffer.length < width * height) {
            log.error("Invalid pixel buffer size");
            return;
        }
        int[] bufferCopy = pixelBuffer.clone();

        new Thread(() -> {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            image.setRGB(0, 0, width, height, bufferCopy, 0, width);

            try {
                String userHome = System.getProperty("user.home");
                File dir = new File(userHome, ".p4j");

                if (!dir.exists() && dir.mkdirs()) {
                    log.info("Created output directory: {}", dir.getAbsolutePath());
                }

                File outputFile = new File(dir, System.currentTimeMillis() + ".png");
                ImageIO.write(image, "png", outputFile);

                log.info("Screenshot saved to {}", outputFile.getAbsolutePath());
            } catch (IOException e) {
                log.error("Failed to save screenshot: {}", e.getMessage(), e);
            }
        }).start();
    }
}