package org.p4j.render;

import org.p4j.core.K;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.p4j.core.World;
import org.p4j.data.BrushShape;
import org.p4j.data.ElementID;
import org.p4j.input.Brush;
import org.p4j.input.KeyboardController;
import org.p4j.input.MouseController;

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
    private float shockwaveAlpha = K.SHOCKWAVE_ALPHA;
    private int shockwaveCenterX;
    private int shockwaveCenterY;

    public FastRender(int simWidth, int simHeight, int scale) {
        log.debug("Initializing renderer: {}x{} at scale {}",
                simWidth, simHeight, scale);
        this.scale = scale;
        Dimension size = new Dimension(
                simWidth * scale,
                simHeight * scale);

        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        this.canvasImage = new BufferedImage(
                simWidth, simHeight,
                BufferedImage.TYPE_INT_ARGB);
        this.pixelBuffer = ((DataBufferInt)
                canvasImage.getRaster()
                        .getDataBuffer()).getData();
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
            createBufferStrategy(K.BUFFER_STRATEGY_COUNT);
            return;
        }

        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        Graphics2D g2d = (Graphics2D) g.create();
        g.drawImage(canvasImage, 0, 0, getWidth(), getHeight(), null);

        if (keyController.wasTabPressed()) {
            wheel(g, keyController, mouseController);
        }

        if (keyController.wasAltPressed()) {
            shaper(g, keyController, mouseController);
        }

        if (keyController.wasEPressed()) {
            shockwaveActive = true;
            shockwaveRadius = K.SHOCKWAVE_RADIUS_MAX;
            maxShockwaveRadius = (float) Math.hypot(getWidth(), getHeight());
            shockwaveAlpha = K.SHOCKWAVE_ALPHA;
            shockwaveCenterX = mouseController.getMouseX();
            shockwaveCenterY = mouseController.getMouseY();
        }

        slider(g, brush);

        if (!K.IS_RUNNING) {
            g.setColor(K.PAUSE_TEXT_COLOR);
            g.setFont(new Font(K.HUD_FONT_FAMILY, Font.BOLD,
                    K.PAUSE_FONT_SIZE));
            FontMetrics fm = g.getFontMetrics();
            String pause = K.PAUSE_TEXT;
            int textX = getWidth() / 2 - fm.stringWidth(pause) / 2;
            int textY = getHeight() / 2 + fm.getAscent();
            g.drawString(pause, textX, textY);
        }

        if (shockwaveActive) {
            shockwaveRadius += K.SHOCKWAVE_INCREMENT;

            float progress = shockwaveRadius / maxShockwaveRadius;
            shockwaveAlpha = K.SHOCKWAVE_ALPHA - (progress * progress);

            byte[] grid = world.getGrid();
            int simWidth = world.getWidth();
            int simHeight = world.getHeight();

            int centerSimX = shockwaveCenterX / scale;
            int centerSimY = shockwaveCenterY / scale;
            float currentSimRadius = shockwaveRadius / scale;
            float ringThicknessSim = K.SHOCKWAVE_WIDTH / scale + K.SHOCKWAVE_RING_EXTRA_THICKNESS;

            int minX = Math.max(0, (int) (centerSimX - currentSimRadius - ringThicknessSim));
            int maxX = Math.min(simWidth - 1, (int) (centerSimX + currentSimRadius + ringThicknessSim));
            int minY = Math.max(0, (int) (centerSimY - currentSimRadius - ringThicknessSim));
            int maxY = Math.min(simHeight - 1, (int) (centerSimY + currentSimRadius + ringThicknessSim));

            for (int sy = minY; sy <= maxY; sy++) {
                for (int sx = minX; sx <= maxX; sx++) {
                    float dist = (float) Math.hypot(sx - centerSimX, sy - centerSimY);
                    if (dist >= currentSimRadius - ringThicknessSim && dist <= currentSimRadius) {
                        int idx = sy * simWidth + sx;
                        grid[idx] = ElementID.EMPTY.getId();
                    }
                }
            }

            if (shockwaveAlpha <= K.SHOCKWAVE_MIN_ALPHA ||
                    shockwaveRadius >= maxShockwaveRadius) {
                shockwaveActive = false;
            } else {
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER,
                        Math.clamp(shockwaveAlpha,
                                K.SHOCKWAVE_MIN_ALPHA,
                                K.SHOCKWAVE_MAX_ALPHA)));

                g2d.setColor(K.SHOCKWAVE_COLOR);
                g2d.setStroke(new BasicStroke(K.SHOCKWAVE_WIDTH,
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int diameter = (int) (shockwaveRadius * 2);
                g2d.drawOval(shockwaveCenterX - (int) shockwaveRadius,
                        shockwaveCenterY - (int) shockwaveRadius, diameter, diameter);
                g2d.dispose();
            }
        }

        world.getCards().render(g2d,
                mouseController,
                getWidth(),
                getHeight());

        g.dispose();
        bs.show();
    }

    private void wheel(Graphics2D g, KeyboardController keyController,
                       MouseController mouseController) {
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g.setRenderingHint(
                RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);

        List<ElementID> elements = keyController.getSelectableElements();
        int totalElements = elements.size();

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        double outerRadius = K.HUD_OUTER_RADIUS;
        double innerRadius = K.HUD_INNER_RADIUS;
        double angleStep = K.HUD_FULL_CIRCLE / totalElements;

        int mx = mouseController.getMouseX();
        int my = mouseController.getMouseY();
        double dx = mx - centerX;
        double dy = my - centerY;
        double distSq = dx * dx + dy * dy;

        if (distSq > innerRadius * innerRadius && distSq < outerRadius * outerRadius) {
            double angle = Math.toDegrees(Math.atan2(dy, dx));
            if (angle < 0) angle += K.RENDERING_FULL_CIRCLE_DEGREES;

            double adjustedAngle = (angle - K.HUD_START_OFFSET_DEG)
                    % K.RENDERING_FULL_CIRCLE_DEGREES;

            if (adjustedAngle < 0) {
                adjustedAngle += K.RENDERING_FULL_CIRCLE_DEGREES;
            }

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
            double startAngle = i * angleStep + K.HUD_START_OFFSET_DEG;

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
                g.setColor(K.HUD_BACKGROUND_COLOR);
            }

            g.fill(sliceArea);

            g.setStroke(new BasicStroke(K.HUD_BORDER_STROKE_WIDTH));
            g.setColor(K.HUD_BORDER_COLOR);
            g.draw(sliceArea);
        }

        if (selectedSliceArea != null) {
            g.setStroke(new BasicStroke(K.HUD_SELECTED_STROKE_WIDTH));
            g.setColor(Color.WHITE);
            g.draw(selectedSliceArea);
        }

        g.setColor(K.HUD_CENTER_COLOR);
        g.fill(innerHole);
        g.setStroke(new BasicStroke(K.HUD_CENTER_STROKE_WIDTH));
        g.setColor(K.HUD_TEXT_UNSELECTED);
        g.draw(innerHole);

        ElementID selectedElement = elements.get(selectedIdx);
        String elementName = selectedElement.getName();
        elementName += " (" + selectedElement.getSymbol() + ")";
        g.setFont(new Font(K.HUD_FONT_FAMILY, Font.BOLD, K.HUD_FONT_SIZE));
        FontMetrics fm = g.getFontMetrics();
        int textX = centerX - fm.stringWidth(elementName) / 2;
        int textY = centerY + (int) outerRadius + K.HUD_TEXT_Y_OFFSET + fm.getAscent();

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
        double outerRadius = K.HUD_OUTER_RADIUS;
        double innerRadius = K.HUD_INNER_RADIUS;
        double angleStep = K.HUD_FULL_CIRCLE / totalShapes;

        int mx = mouseController.getMouseX();
        int my = mouseController.getMouseY();
        double dx = mx - centerX;
        double dy = my - centerY;
        double distSq = dx * dx + dy * dy;

        if (distSq > innerRadius * innerRadius && distSq < outerRadius * outerRadius) {
            double angle = Math.toDegrees(Math.atan2(dy, dx));
            if (angle < 0) angle += K.RENDERING_FULL_CIRCLE_DEGREES;

            double adjustedAngle = (angle - K.HUD_START_OFFSET_DEG)
                    % K.RENDERING_FULL_CIRCLE_DEGREES;

            if (adjustedAngle < 0) {
                adjustedAngle += K.RENDERING_FULL_CIRCLE_DEGREES;
            }

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
            double startAngle = i * angleStep + K.HUD_START_OFFSET_DEG;

            Arc2D.Double outerPie = new Arc2D.Double(
                    centerX - outerRadius, centerY - outerRadius,
                    outerRadius * 2, outerRadius * 2,
                    -startAngle, -angleStep, Arc2D.PIE
            );

            Area sliceArea = new Area(outerPie);
            sliceArea.subtract(holeArea);

            if (i == selectedIdx) {
                selectedSliceArea = sliceArea;
                g.setColor(K.SHAPER_SELECTED_COLOR);
            } else {
                g.setColor(K.HUD_BACKGROUND_COLOR);
            }

            g.fill(sliceArea);

            g.setStroke(new BasicStroke(K.HUD_BORDER_STROKE_WIDTH));
            g.setColor(K.HUD_BORDER_COLOR);
            g.draw(sliceArea);

            double midAngleRad = Math.toRadians(startAngle + angleStep / 2);
            double iconRadius = (innerRadius + outerRadius) / 2.0;
            int iconX = (int) (centerX + iconRadius * Math.cos(midAngleRad));
            int iconY = (int) (centerY + iconRadius * Math.sin(midAngleRad));

            g.setFont(new Font(K.HUD_FONT_FAMILY, Font.BOLD, K.SHAPER_ICON_FONT_SIZE));
            g.setColor(Color.WHITE);
            FontMetrics iconFm = g.getFontMetrics();
            g.drawString(shape.getSymbol(),
                    iconX - iconFm.stringWidth(shape.getSymbol()) / 2,
                    iconY + iconFm.getAscent() / 2 - K.SHAPER_ICON_Y_OFFSET);
        }

        if (selectedSliceArea != null) {
            g.setStroke(new BasicStroke(K.HUD_SELECTED_STROKE_WIDTH));
            g.setColor(Color.WHITE);
            g.draw(selectedSliceArea);
        }

        g.setColor(K.HUD_CENTER_COLOR);
        g.fill(innerHole);
        g.setStroke(new BasicStroke(K.HUD_CENTER_STROKE_WIDTH));
        g.setColor(K.HUD_TEXT_UNSELECTED);
        g.draw(innerHole);

        BrushShape selectedShape = shapes.get(selectedIdx);
        String shapeText = selectedShape.getName();
        g.setFont(new Font(K.HUD_FONT_FAMILY, Font.BOLD, K.HUD_FONT_SIZE));
        FontMetrics fm = g.getFontMetrics();
        int textX = centerX - fm.stringWidth(shapeText) / 2;
        int textY = centerY + (int) outerRadius + K.HUD_TEXT_Y_OFFSET + fm.getAscent();

        g.setColor(Color.WHITE);
        g.drawString(shapeText, textX, textY);
    }

    private void slider(Graphics2D g, Brush brush) {
        long timeSinceLastChange = System.currentTimeMillis() -
                brush.getLastRadiusChangeTime();
        if (timeSinceLastChange > K.HUD_SLIDER_VISIBLE_MS) {
            return;
        }

        float opacity = K.HUD_SLIDER_MAX_OPACITY;
        long fadeStartTime = K.HUD_SLIDER_VISIBLE_MS - K.HUD_SLIDER_FADE_DURATION_MS;
        if (timeSinceLastChange > fadeStartTime) {
            opacity = K.HUD_SLIDER_MAX_OPACITY - (float)
                    (timeSinceLastChange - fadeStartTime) / (float)
                    K.HUD_SLIDER_FADE_DURATION_MS;
        }
        opacity = Math.clamp(opacity, K.HUD_SLIDER_MIN_OPACITY,
                K.HUD_SLIDER_MAX_OPACITY);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int sliderWidth = K.HUD_SLIDER_WIDTH;
        int sliderHeight = getHeight() - (K.HUD_SLIDER_Y_PADDING * 2);
        int sliderX = K.HUD_SLIDER_X_PADDING;
        int sliderY = K.HUD_SLIDER_Y_PADDING;

        g.setColor(new Color(K.COLOR_CHANNEL_MAX, K.COLOR_CHANNEL_MAX,
                K.COLOR_CHANNEL_MAX, K.HUD_SLIDER_TRACK_ALPHA));

        g.fillRoundRect(sliderX, sliderY, sliderWidth, sliderHeight,
                K.HUD_SLIDER_CORNER_RADIUS, K.HUD_SLIDER_CORNER_RADIUS);

        int minR = K.MIN_BRUSH_RADIUS;
        int maxR = K.MAX_BRUSH_RADIUS;
        int currentR = brush.getRadius();

        float ratio = (float) (currentR - minR) / (maxR - minR);
        int knobHeight = (int) (ratio * sliderHeight);
        int knobY = sliderY + sliderHeight - knobHeight;

        g.setColor(K.HUD_SLIDER_COLOR);
        g.fillRoundRect(sliderX, knobY, sliderWidth, knobHeight,
                K.HUD_SLIDER_CORNER_RADIUS,
                K.HUD_SLIDER_CORNER_RADIUS);

        g.setFont(new Font(K.HUD_FONT_FAMILY, Font.BOLD,
                K.HUD_SLIDER_LABEL_FONT_SIZE));
        FontMetrics fm = g.getFontMetrics();

        String plus = K.HUD_SLIDER_PLUS_SYMBOL;
        g.drawString(plus, sliderX + (sliderWidth / 2) - (fm.stringWidth(plus) / 2),
                sliderY - K.HUD_SLIDER_SYMBOL_OFFSET);

        String minus = K.HUD_SLIDER_MINUS_SYMBOL;
        g.drawString(minus, sliderX + (sliderWidth / 2) - (fm.stringWidth(minus) / 2),
                sliderY + sliderHeight + fm.getAscent());

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                K.HUD_SLIDER_MAX_OPACITY));
    }

    public int getParticleColor(int x, int y, ElementID element) {
        int baseColor = element.getColorArgb();
        if (element == ElementID.EMPTY) {
            return baseColor;
        }

        double v = (Math.sin(x * K.MERCURY_COLOR_WAVE_FREQUENCY + y *
                K.MERCURY_COLOR_WAVE_FREQUENCY) + 1) * K.MERCURY_COLOR_SHIFT_MULTIPLIER;

        if (element == ElementID.MERCURY) {
            int shift = (int) v;
            return adjustBrightness(baseColor, shift);
        }

        if (element == ElementID.IRON) {
            int shift = (int) v;
            return adjustBrightness(baseColor, shift);
        }

        if (element == ElementID.FIRE || element == ElementID.LAVA) {
            int noise = (int) (Math.random() *
                    K.FIRE_LAVA_COLOR_NOISE_RANGE -
                    K.FIRE_LAVA_COLOR_NOISE_OFFSET);
            return adjustBrightness(baseColor, noise);
        }

        int grain = ((x * K.PARTICLE_GRAIN_X_MULTIPLIER + y *
                K.PARTICLE_GRAIN_Y_MULTIPLIER) %
                K.PARTICLE_GRAIN_MODULO) - K.PARTICLE_GRAIN_OFFSET;
        return adjustBrightness(baseColor, grain);
    }

    private static int adjustBrightness(int argb, int delta) {
        int a = (argb >> K.COLOR_ALPHA_SHIFT) & K.COLOR_CHANNEL_MAX;
        int r = (argb >> K.COLOR_RED_SHIFT) & K.COLOR_CHANNEL_MAX;
        int g = (argb >> K.COLOR_GREEN_SHIFT) & K.COLOR_CHANNEL_MAX;
        int b = argb & K.COLOR_CHANNEL_MAX;

        r = Math.clamp(r + delta, 0, K.COLOR_CHANNEL_MAX);
        g = Math.clamp(g + delta, 0, K.COLOR_CHANNEL_MAX);
        b = Math.clamp(b + delta, 0, K.COLOR_CHANNEL_MAX);

        return (a << K.COLOR_ALPHA_SHIFT) |
                (r << K.COLOR_RED_SHIFT) |
                (g << K.COLOR_GREEN_SHIFT) | b;
    }
}