package org.xdg.p4j.render;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * Responsible for visual representation of the simulation state.
 * Utilizing a buffered approach, this class efficiently renders the grid
 * to the screen by mapping element identifiers to their respective colors.
 */
public class FastRenderer extends Canvas {
    private static final Logger log = LoggerFactory.getLogger(FastRenderer.class);
    private final int simWidth;
    private final int simHeight;
    private final BufferedImage canvasImage;
    private final int[] pixelBuffer;

    public FastRenderer(int simWidth, int simHeight, int scale) {
        log.debug("Initializing renderer: {}x{} at scale {}", simWidth, simHeight, scale);
        this.simWidth = simWidth;
        this.simHeight = simHeight;

        Dimension size = new Dimension(simWidth * scale, simHeight * scale);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        this.canvasImage = new BufferedImage(simWidth, simHeight, BufferedImage.TYPE_INT_ARGB);
        this.pixelBuffer = ((DataBufferInt) canvasImage.getRaster().getDataBuffer()).getData();
    }

    public void updatePixels(byte[] grid, Palette palette) {
        int length = Math.min(grid.length, pixelBuffer.length);
        for (int i = 0; i < length; i++) {
            pixelBuffer[i] = palette.getColor(grid[i]);
        }
    }

    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.drawImage(canvasImage, 0, 0, getWidth(), getHeight(), null);
        g.dispose();
        bs.show();
    }

    public int getSimWidth() { return simWidth; }
    public int getSimHeight() { return simHeight; }
}