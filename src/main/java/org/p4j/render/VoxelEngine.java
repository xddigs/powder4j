package org.p4j.render;

import org.p4j.core.Engine;
import org.p4j.core.K;
import org.p4j.core.World;
import org.p4j.input.Brush;
import org.p4j.input.KeyboardController;
import org.p4j.input.MouseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * Isometric (2.5D) voxel rendering engine for Powder4J.
 * Renders spatial projections directly into a pixel buffer for low-latency graphics.
 */
public class VoxelEngine extends Canvas implements Engine {
    private static final Logger log = LoggerFactory.getLogger(VoxelEngine.class);

    private final BufferedImage canvasImage;
    private final World world;
    private final int[] pixelBuffer;
    private final int scale;

    public VoxelEngine(int simWidth, int simHeight, World world, int scale) {
        log.debug("Initializing 2.5D Voxel Engine: {}x{} at scale {}", simWidth, simHeight, scale);
        this.scale = scale;
        this.world = world;

        Dimension size = new Dimension(simWidth * scale, simHeight * scale);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setBackground(K.GAME_BACKGROUND_COLOR);

        this.canvasImage = new BufferedImage(simWidth, simHeight, BufferedImage.TYPE_INT_ARGB);
        this.pixelBuffer = ((DataBufferInt) canvasImage.getRaster().getDataBuffer()).getData();
    }

    @Override
    public void updatePixels(World world) {

    }

    @Override
    public void render(World world, KeyboardController keyController,
                       MouseController mouseController, Brush brush) {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(K.BUFFER_STRATEGY_COUNT);
            return;
        }

        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.drawImage(canvasImage, 0, 0, getWidth(), getHeight(), null);

        if (!K.IS_RUNNING) {
            g.setColor(K.PAUSE_TEXT_COLOR);
            g.setFont(K.FONT_BIG);
            FontMetrics fm = g.getFontMetrics();
            String pause = K.PAUSE_TEXT;
            int textX = getWidth() / 2 - fm.stringWidth(pause) / 2;
            int textY = getHeight() / 2 + fm.getAscent();
            g.drawString(pause, textX, textY);
        }

        g.dispose();
        bs.show();
    }

    public int[] getPixelBuffer() {
        return pixelBuffer;
    }

    @Override
    public void dispose() {
        log.debug("Disposing VoxelEngine resources");
    }
}