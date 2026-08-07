package org.xdg.p4j.render;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdg.p4j.core.Constants;
import org.xdg.p4j.data.ElementID;
import org.xdg.p4j.input.KeyboardController;
import org.xdg.p4j.input.MouseController;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.List;

/**
 * Responsible for visual representation of the simulation state.
 * Using a buffered approach, this class efficiently renders the grid
 * to the screen by mapping element identifiers to their respective colors.
 */
public class FastRender extends Canvas {
    private static final Logger log = LoggerFactory.getLogger(FastRender.class);
    private final BufferedImage canvasImage;
    private final int[] pixelBuffer;

    public FastRender(int simWidth, int simHeight, int scale) {
        log.debug("Initializing renderer: {}x{} at scale {}", simWidth, simHeight, scale);
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

        if (!Constants.IS_RUNNING) {
            g.setColor(Color.WHITE);
            g.setFont(new Font(Constants.HUD_FONT_FAMILY, Font.BOLD,
                    Constants.PAUSE_FONT_SIZE));
            FontMetrics fm = g.getFontMetrics();
            String pause = "| PAUSED |";
            int textX = getWidth() / 2 - fm.stringWidth(pause) / 2;
            int textY = getHeight() / 2 + fm.getAscent();
            g.drawString(pause, textX, textY);
        }

        g.dispose();
        bs.show();
    }

    private void renderHUD(Graphics2D g, KeyboardController keyController, 
                           MouseController mouseController) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        List<ElementID> elements = keyController.getSelectableElements();
        int totalElements = elements.size();

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        double outerRadius = Constants.HUD_OUTER_RADIUS;
        double innerRadius = Constants.HUD_INNER_RADIUS;
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
        Ellipse2D.Double innerHole = new Ellipse2D.Double(
                centerX - innerRadius, centerY - innerRadius,
                innerRadius * 2, innerRadius * 2
        );

        Area holeArea = new Area(innerHole);
        Area selectedSliceArea = null;

        for (int i = 0; i < totalElements; i++) {
            ElementID el = elements.get(i);
            double startAngle = i * angleStep + Constants.HUD_START_OFFSET_DEG;

            Arc2D.Double outerPie = new Arc2D.Double(
                    centerX - outerRadius, centerY - outerRadius,
                    outerRadius * 2, outerRadius * 2,
                    -startAngle, -angleStep, Arc2D.PIE
            );

            Area sliceArea = new Area(outerPie);
            sliceArea.subtract(holeArea);

            if (i == selectedIdx) {
                selectedSliceArea = sliceArea;
                g.setColor(new Color(el.getColorArgb()));
            } else {
                g.setColor(Constants.HUD_BACKGROUND_COLOR);
            }

            g.fill(sliceArea);

            g.setStroke(new BasicStroke(1.0f));
            g.setColor(Constants.HUD_BORDER_COLOR);
            g.draw(sliceArea);
        }

        if (selectedSliceArea != null) {
            g.setStroke(new BasicStroke(2.5f));
            g.setColor(Color.WHITE);
            g.draw(selectedSliceArea);
        }

        g.setColor(Constants.HUD_CENTER_COLOR);
        g.fill(innerHole);
        g.setStroke(new BasicStroke(1.5f));
        g.setColor(Constants.HUD_TEXT_UNSELECTED);
        g.draw(innerHole);

        ElementID selectedElement = elements.get(selectedIdx);
        String elementName = selectedElement.getName();
        elementName += " (" + selectedElement.getSymbol() + ")";
        g.setFont(new Font(Constants.HUD_FONT_FAMILY, Font.BOLD, Constants.HUD_FONT_SIZE));
        FontMetrics fm = g.getFontMetrics();
        int textX = centerX - fm.stringWidth(elementName) / 2;
        int textY = centerY + (int) outerRadius + Constants.HUD_TEXT_Y_OFFSET + fm.getAscent();

        g.setColor(Color.WHITE);
        g.drawString(elementName, textX, textY);
    }
}