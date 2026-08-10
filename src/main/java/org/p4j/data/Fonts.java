package org.p4j.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class Fonts {
    public static final Font NERD_FONT;
    private static final Logger log = LoggerFactory.getLogger(Fonts.class);

    static {
        Font font;
        try (InputStream is = Fonts.class.getResourceAsStream("ArimoNerdFont-Bold.ttf")) {
            if (is == null) {
                throw new IOException("Font not found");
            }

            font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(16f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);

        } catch (FontFormatException | IOException e) {
            log.error(e.getMessage());
            font = new Font("Arial", Font.PLAIN, 16);
        }
        NERD_FONT = font;
    }

    public static Font get(float size, int style) {
        return NERD_FONT.deriveFont(style, size);
    }
}