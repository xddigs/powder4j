package org.xdg.p4j.render;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdg.p4j.core.Constants;
import org.xdg.p4j.data.ElementID;
import org.xdg.p4j.input.KeyboardController;
import org.xdg.p4j.input.MouseController;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * Responsible for visual representation of the simulation state.
 * Using a buffered approach, this class efficiently renders the grid
 * to the screen by mapping element identifiers to their respective colors.
 */
public class FastRender extends Canvas {
    private static final Logger log = LoggerFactory.getLogger(FastRender.class);
    private final int simWidth;
    private final int simHeight;
    private final BufferedImage canvasImage;
    private final int[] pixelBuffer;

    public FastRender(int simWidth, int simHeight, int scale) {
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

    public void render(KeyboardController keyController, MouseController mouseController) {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(Constants.BUFFER_STRATEGY_COUNT);
            return;
        }

        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.drawImage(canvasImage, 0, 0, getWidth(), getHeight(), null);

        if (keyController.isTabPressed()) {
            renderHUD(g, keyController, mouseController);
        }

        g.dispose();
        bs.show();
    }

    private void renderHUD(Graphics2D g, KeyboardController keyController, MouseController mouseController) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        ElementID[] elements = ElementID.values();
        int totalElements = elements.length;

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int outerRadius = Constants.HUD_OUTER_RADIUS;
        int innerRadius = Constants.HUD_INNER_RADIUS;
        double angleStep = Constants.HUD_FULL_CIRCLE / totalElements;

        int mx = mouseController.getMouseX();
        int my = mouseController.getMouseY();
        double dx = mx - centerX;
        double dy = my - centerY;
        double distSq = dx * dx + dy * dy;

        if (distSq > innerRadius * innerRadius && distSq < outerRadius * outerRadius) {
            double angle = Math.toDegrees(Math.atan2(dy, dx));
            if (angle < 0) angle += 360;
            
            double adjustedAngle = (angle - Constants.HUD_START_OFFSET_DEG) % 360;
            if (adjustedAngle < 0) adjustedAngle += 360;
            
            int hoveredIdx = (int) (adjustedAngle / angleStep);
            if (hoveredIdx >= 0 && hoveredIdx < totalElements) {
                keyController.setSelectedIndex(hoveredIdx);
            }
        }

        int selectedIdx = keyController.getSelectedIndex();

        for (int i = 0; i < totalElements; i++) {
            ElementID el = elements[i];
            double startAngle = i * angleStep + Constants.HUD_START_OFFSET_DEG;

            if (i == selectedIdx) {
                g.setColor(new Color(el.getColorArgb()));
            } else {
                g.setColor(Constants.HUD_BACKGROUND_COLOR);
            }

            g.fillArc(centerX - outerRadius, centerY - outerRadius, outerRadius * 2, outerRadius * 2,
                    (int) -startAngle, (int) -angleStep);

            g.setColor(Constants.HUD_BORDER_COLOR);
            g.drawArc(centerX - outerRadius, centerY - outerRadius, outerRadius * 2, outerRadius * 2,
                    (int) -startAngle, (int) -angleStep);
        }

        g.setColor(Constants.HUD_CENTER_COLOR);
        g.fillOval(centerX - innerRadius, centerY - innerRadius, innerRadius * 2, innerRadius * 2);
        g.setColor(Constants.HUD_TEXT_UNSELECTED);
        g.drawOval(centerX - innerRadius, centerY - innerRadius, innerRadius * 2, innerRadius * 2);

        ElementID selectedElement = elements[selectedIdx];
        String elementName = selectedElement.getName();
        g.setFont(new Font(Constants.HUD_FONT_FAMILY, Font.BOLD, Constants.HUD_FONT_SIZE));
        FontMetrics fm = g.getFontMetrics();
        int textX = centerX - fm.stringWidth(elementName) / 2;
        int textY = centerY + outerRadius + Constants.HUD_TEXT_Y_OFFSET + fm.getAscent();
        
        g.setColor(Constants.HUD_TEXT_UNSELECTED);
        g.drawString(elementName, textX, textY);
    }

    public int getSimWidth() { return simWidth; }
    public int getSimHeight() { return simHeight; }
}