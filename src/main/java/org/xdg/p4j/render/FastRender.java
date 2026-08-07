package org.xdg.p4j.render;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdg.p4j.core.Constants;
import org.xdg.p4j.core.World;
import org.xdg.p4j.data.BrushShape;
import org.xdg.p4j.data.ElementID;
import org.xdg.p4j.input.Brush;
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
    private final int scale;

    private boolean shockwaveActive = false;
    private float shockwaveRadius = 0f;
    private float maxShockwaveRadius = 0f;
    private float shockwaveAlpha = 1.0f;
    private int shockwaveCenterX;
    private int shockwaveCenterY;

    public FastRender(int simWidth, int simHeight, int scale) {
        log.debug("Initializing renderer: {}x{} at scale {}", simWidth, simHeight, scale);
        this.scale = scale;
        Dimension size = new Dimension(simWidth * scale, simHeight * scale);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        this.canvasImage = new BufferedImage(simWidth, simHeight, BufferedImage.TYPE_INT_ARGB);
        this.pixelBuffer = ((DataBufferInt) canvasImage.getRaster().getDataBuffer()).getData();
    }

    public void updatePixels(byte[] grid, int width) {
        int length = Math.min(grid.length, pixelBuffer.length);
        for (int i = 0; i < length; i++) {
            int x = i % width;
            int y = i / width;
            pixelBuffer[i] = getParticleColor(x, y, ElementID.fromId(grid[i]));
        }
    }

    public void render(World world, KeyboardController keyController,
                       MouseController mouseController, Brush brush) {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(Constants.BUFFER_STRATEGY_COUNT);
            return;
        }

        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.drawImage(canvasImage, 0, 0, getWidth(), getHeight(), null);

        if (keyController.wasTabPressed()) {
            wheel(g, keyController, mouseController);
        }

        if (keyController.wasAltPressed()) {
            shaper(g, keyController, mouseController);
        }

        if (keyController.wasEPressed()) {
            shockwaveActive = true;
            shockwaveRadius = Constants.SHOCKWAVE_RADIUS_MAX;
            maxShockwaveRadius = (float) Math.hypot(getWidth(), getHeight());
            shockwaveAlpha = Constants.SHOCKWAVE_ALPHA;
            shockwaveCenterX = mouseController.getMouseX();
            shockwaveCenterY = mouseController.getMouseY();
        }

        slider(g, brush);

        if (!Constants.IS_RUNNING) {
            g.setColor(Color.WHITE);
            g.setFont(new Font(Constants.HUD_FONT_FAMILY, Font.BOLD,
                    Constants.PAUSE_FONT_SIZE));
            FontMetrics fm = g.getFontMetrics();
            String pause = "PAUSED";
            int textX = getWidth() / 2 - fm.stringWidth(pause) / 2;
            int textY = getHeight() / 2 + fm.getAscent();
            g.drawString(pause, textX, textY);
        }

        if (shockwaveActive) {
            shockwaveRadius += Constants.SHOCKWAVE_INCREMENT;

            float progress = shockwaveRadius / maxShockwaveRadius;
            shockwaveAlpha = Constants.SHOCKWAVE_ALPHA - (progress * progress);

            byte[] grid = world.getGrid();
            int simWidth = world.getWidth();
            int simHeight = world.getHeight();

            int centerSimX = shockwaveCenterX / scale;
            int centerSimY = shockwaveCenterY / scale;
            float currentSimRadius = shockwaveRadius / scale;
            float ringThicknessSim = Constants.SHOCKWAVE_WIDTH / scale + 2f;

            int minX = Math.max(0, (int)(centerSimX - currentSimRadius - ringThicknessSim));
            int maxX = Math.min(simWidth - 1, (int)(centerSimX + currentSimRadius + ringThicknessSim));
            int minY = Math.max(0, (int)(centerSimY - currentSimRadius - ringThicknessSim));
            int maxY = Math.min(simHeight - 1, (int)(centerSimY + currentSimRadius + ringThicknessSim));

            for (int sy = minY; sy <= maxY; sy++) {
                for (int sx = minX; sx <= maxX; sx++) {
                    float dist = (float) Math.hypot(sx - centerSimX, sy - centerSimY);
                    if (dist >= currentSimRadius - ringThicknessSim && dist <= currentSimRadius) {
                        int idx = sy * simWidth + sx;
                        grid[idx] = ElementID.EMPTY.getId();
                    }
                }
            }

            if (shockwaveAlpha <= 0f || shockwaveRadius >= maxShockwaveRadius) {
                shockwaveActive = false;
            } else {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER,
                        Math.clamp(shockwaveAlpha, 0f, 1f)));

                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(Constants.SHOCKWAVE_WIDTH,
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int diameter = (int) (shockwaveRadius * 2);
                g2d.drawOval(shockwaveCenterX - (int)shockwaveRadius,
                        shockwaveCenterY - (int)shockwaveRadius, diameter, diameter);
                g2d.dispose();
            }
        }

        g.dispose();
        bs.show();
    }

    private void wheel(Graphics2D g, KeyboardController keyController,
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

    private void shaper(Graphics2D g, KeyboardController keyController,
                        MouseController mouseController) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        List<BrushShape> shapes = keyController.getSelectableShapes();
        int totalShapes = shapes.size();

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        double outerRadius = Constants.HUD_OUTER_RADIUS;
        double innerRadius = Constants.HUD_INNER_RADIUS;
        double angleStep = Constants.HUD_FULL_CIRCLE / totalShapes;

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
            if (hoveredIdx >= 0 && hoveredIdx < totalShapes) {
                keyController.setSelectedShapeIndex(hoveredIdx);
            }
        }

        int selectedIdx = keyController.getSelectedShapeIndex();
        Ellipse2D.Double innerHole = new Ellipse2D.Double(
                centerX - innerRadius, centerY - innerRadius,
                innerRadius * 2, innerRadius * 2
        );

        Area holeArea = new Area(innerHole);
        Area selectedSliceArea = null;

        for (int i = 0; i < totalShapes; i++) {
            BrushShape shape = shapes.get(i);
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
                g.setColor(new Color(60, 120, 210, 200));
            } else {
                g.setColor(Constants.HUD_BACKGROUND_COLOR);
            }

            g.fill(sliceArea);

            g.setStroke(new BasicStroke(1.0f));
            g.setColor(Constants.HUD_BORDER_COLOR);
            g.draw(sliceArea);

            double midAngleRad = Math.toRadians(startAngle + angleStep / 2);
            double iconRadius = (innerRadius + outerRadius) / 2.0;
            int iconX = (int) (centerX + iconRadius * Math.cos(midAngleRad));
            int iconY = (int) (centerY + iconRadius * Math.sin(midAngleRad));

            g.setFont(new Font(Constants.HUD_FONT_FAMILY, Font.BOLD, 22));
            g.setColor(Color.WHITE);
            FontMetrics iconFm = g.getFontMetrics();
            g.drawString(shape.getSymbol(),
                    iconX - iconFm.stringWidth(shape.getSymbol()) / 2,
                    iconY + iconFm.getAscent() / 2 - 2);
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

        BrushShape selectedShape = shapes.get(selectedIdx);
        String shapeText = selectedShape.getName();
        g.setFont(new Font(Constants.HUD_FONT_FAMILY, Font.BOLD, Constants.HUD_FONT_SIZE));
        FontMetrics fm = g.getFontMetrics();
        int textX = centerX - fm.stringWidth(shapeText) / 2;
        int textY = centerY + (int) outerRadius + Constants.HUD_TEXT_Y_OFFSET + fm.getAscent();

        g.setColor(Color.WHITE);
        g.drawString(shapeText, textX, textY);
    }

    private void slider(Graphics2D g, Brush brush) {
        long timeSinceLastChange = System.currentTimeMillis() - brush.getLastRadiusChangeTime();
        if (timeSinceLastChange > Constants.HUD_SLIDER_VISIBLE_MS) {
            return;
        }

        float opacity = 1.0f;
        long fadeStartTime = Constants.HUD_SLIDER_VISIBLE_MS - 500;
        if (timeSinceLastChange > fadeStartTime) {
            opacity = 1.0f - (float)(timeSinceLastChange - fadeStartTime) / 500f;
        }
        opacity = Math.clamp(opacity, 0.0f, 1.0f);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int sliderWidth = Constants.HUD_SLIDER_WIDTH;
        int sliderHeight = getHeight() - (Constants.HUD_SLIDER_Y_PADDING * 2);
        int sliderX = Constants.HUD_SLIDER_X_PADDING;
        int sliderY = Constants.HUD_SLIDER_Y_PADDING;

        g.setColor(new Color(255, 255, 255, 60));
        g.fillRoundRect(sliderX, sliderY, sliderWidth, sliderHeight, 5, 5);

        int minR = Constants.MIN_BRUSH_RADIUS;
        int maxR = Constants.MAX_BRUSH_RADIUS;
        int currentR = brush.getRadius();

        float ratio = (float)(currentR - minR) / (maxR - minR);
        int knobHeight = (int)(ratio * sliderHeight);
        int knobY = sliderY + sliderHeight - knobHeight;

        g.setColor(Constants.HUD_SLIDER_COLOR);
        g.fillRoundRect(sliderX, knobY, sliderWidth, knobHeight, 5, 5);

        g.setFont(new Font(Constants.HUD_FONT_FAMILY, Font.BOLD, 20));
        FontMetrics fm = g.getFontMetrics();

        String plus = "+";
        g.drawString(plus, sliderX + (sliderWidth / 2) - (fm.stringWidth(plus) / 2),
                sliderY - Constants.HUD_SLIDER_SYMBOL_OFFSET);

        String minus = "-";
        g.drawString(minus, sliderX + (sliderWidth / 2) - (fm.stringWidth(minus) / 2),
                sliderY + sliderHeight + fm.getAscent());

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    public int getParticleColor(int x, int y, ElementID element) {
        int baseColor = element.getColorArgb();
        if (element == ElementID.EMPTY) {
            return baseColor;
        }

        if (element == ElementID.MERCURY) {
            int shift = (int) ((Math.sin(x * 0.5 + y * 0.5) + 1) * 15);
            return adjustBrightness(baseColor, shift);
        }

        if (element == ElementID.FIRE || element == ElementID.LAVA) {
            int noise = (int) (Math.random() * 20 - 10);
            return adjustBrightness(baseColor, noise);
        }

        int grain = ((x * 7 + y * 13) % 21) - 10;
        return adjustBrightness(baseColor, grain);
    }

    private static int adjustBrightness(int argb, int delta) {
        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;

        r = Math.clamp(r + delta, 0, 255);
        g = Math.clamp(g + delta, 0, 255);
        b = Math.clamp(b + delta, 0, 255);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}