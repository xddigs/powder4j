package org.p4j.render;

import org.p4j.core.K;
import org.p4j.core.World;
import org.p4j.data.BrushShape;
import org.p4j.data.BrushType;
import org.p4j.data.ElementID;
import org.p4j.input.Brush;
import org.p4j.input.KeyboardController;
import org.p4j.input.MouseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class RenderingEngine extends Canvas {
    private static final Logger log = LoggerFactory.getLogger(RenderingEngine.class);
    private final BufferedImage canvasImage;
    private final World world;
    private final int[] pixelBuffer;
    private final int scale;

    private boolean shockwaveActive = false;
    private float shockwaveRadius = 0f;
    private float maxShockwaveRadius = 0f;
    private float shockwaveAlpha = K.SHOCKWAVE_ALPHA;
    private int shockwaveCenterX;
    private int shockwaveCenterY;

    public RenderingEngine(int simWidth, int simHeight, World world, int scale) {
        log.debug("Initializing renderer: {}x{} at scale {}",
                simWidth, simHeight, scale);
        this.scale = scale;
        this.world = world;
        Dimension size = new Dimension(
                simWidth * scale,
                simHeight * scale);

        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setBackground(K.GAME_BACKGROUND_COLOR);

        this.canvasImage = new BufferedImage(
                simWidth, simHeight,
                BufferedImage.TYPE_INT_ARGB);
        this.pixelBuffer = ((DataBufferInt)
                canvasImage.getRaster()
                        .getDataBuffer()).getData();
    }

    public void updatePixels(World world) {
        byte[] grid = world.getGrid();
        int width = world.getWidth();
        int height = world.getHeight();
        HeatMap heatmap = world.getHeatMap();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int idx = world.getIndex(x, y);
                byte elemId = grid[idx];
                float temp = world.getTemperature(idx);
                int heatColor = heatmap.getColorForPixel(elemId, temp);
                if (heatColor != -1) {
                    pixelBuffer[idx] = heatColor;
                } else {
                    pixelBuffer[idx] = getParticleColor(x, y, ElementID.fromId(elemId));
                }
            }
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

        if (keyController.wasShiftPressed()) {
            typer(g, keyController, mouseController);
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
        cursor(g, brush, mouseController);
        regulate(g, brush);

        if (!K.IS_RUNNING) {
            g.setColor(K.PAUSE_TEXT_COLOR);
            g.setFont(K.FONT_BIG);
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
                        grid[idx] = ElementID.VOID.getId();
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

        info(g2d, brush.getElement(), keyController);
        world.getCards().render(g2d,
                mouseController,
                getWidth(),
                getHeight());

        g.dispose();
        bs.show();
    }

    private void cursor(Graphics g,
                        Brush brush,
                        MouseController mouseController) {
        Graphics2D g2 = (Graphics2D) g;
        if (brush.getElement() == null) return;
        long lastRadiusChange = System.currentTimeMillis() - brush.getLastRadiusChangeTime();
        if (lastRadiusChange > K.HUD_SLIDER_VISIBLE_MS) {
            return;
        }

        float opacity = getOpacity(lastRadiusChange);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);

        int mouseX = mouseController.getMouseX();
        int mouseY = mouseController.getMouseY();

        int scaledRadius = brush.getRadius() * scale;
        int diameter = scaledRadius * 2;

        int drawX = mouseX - scaledRadius;
        int drawY = mouseY - scaledRadius;

        g2.setStroke(new BasicStroke(K.HUD_SELECTED_STROKE_WIDTH));
        g2.setColor(K.TEXT_COLOR);
        g2.drawOval(drawX, drawY, diameter, diameter);

        g2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, K.HUD_SLIDER_MAX_OPACITY));
    }

    private void info(Graphics2D g2,
                      ElementID element,
                      KeyboardController keyController) {
        if (!keyController.wasVPressed()) return;
        if (element == null) return;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                            RenderingHints.VALUE_STROKE_PURE);

        g2.setFont(K.FONT_SMALL);
        FontMetrics fm = g2.getFontMetrics();

        String elementInfo = element.getName() + " (" + element.getSymbol() + ")";

        int textWidth = fm.stringWidth(elementInfo);
        int iconSize = K.HUD_INFO_ICON_SIZE;
        int spacing = 8;
        int paddingRight = K.HUD_INFO_WIDTH_OFFSET;
        int totalHUDWidth = iconSize + spacing + textWidth;
        int iconX = getWidth() - paddingRight - totalHUDWidth;
        int textX = iconX + iconSize + spacing;
        int textY = getHeight() - K.HUD_SLIDER_Y_PADDING;
        int iconY = textY - (fm.getAscent() / 2) - (iconSize / 2);

        int shadowOffset = 2;
        Color shadowColor = K.GAME_BACKGROUND_COLOR;

        g2.setColor(shadowColor);
        g2.fillOval(iconX + shadowOffset, iconY + shadowOffset, iconSize, iconSize);
        g2.drawString(elementInfo, textX + shadowOffset, textY + shadowOffset);

        g2.setColor(new Color(element.getColorArgb()));
        g2.fillOval(iconX, iconY, iconSize, iconSize);
        g2.setColor(K.TEXT_COLOR);
        g2.drawString(elementInfo, textX, textY);
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
                g.setColor(K.UI_BACKGROUND_COLOR);
            }

            g.fill(sliceArea);

            g.setStroke(new BasicStroke(K.HUD_BORDER_STROKE_WIDTH));
            g.setColor(K.UI_BACKGROUND_BORDER_COLOR);
            g.draw(sliceArea);
        }

        if (selectedSliceArea != null) {
            g.setStroke(new BasicStroke(K.HUD_SELECTED_STROKE_WIDTH));
            g.setColor(K.TEXT_COLOR);
            g.draw(selectedSliceArea);
        }

        g.setColor(K.UI_BACKGROUND_COLOR);
        g.fill(innerHole);
        g.setStroke(new BasicStroke(K.HUD_CENTER_STROKE_WIDTH));
        g.setColor(K.TEXT_COLOR_UNSELECTED);
        g.draw(innerHole);

        ElementID selectedElement = elements.get(selectedIdx);
        String elementName = selectedElement.getName();
        elementName += " (" + selectedElement.getSymbol() + ")";
        g.setFont(K.FONT_SMALL);
        FontMetrics fm = g.getFontMetrics();
        int textX = centerX - fm.stringWidth(elementName) / 2;
        int textY = centerY + (int) outerRadius + K.HUD_TEXT_Y_OFFSET + fm.getAscent();

        g.setColor(K.TEXT_COLOR);
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
                g.setColor(K.HIGHLIGHT_COLOR);
            } else {
                g.setColor(K.UI_BACKGROUND_COLOR);
            }

            g.fill(sliceArea);

            g.setStroke(new BasicStroke(K.HUD_BORDER_STROKE_WIDTH));
            g.setColor(K.UI_BACKGROUND_BORDER_COLOR);
            g.draw(sliceArea);

            double midAngleRad = Math.toRadians(startAngle + angleStep / 2);
            double iconRadius = (innerRadius + outerRadius) / 2.0;
            int iconX = (int) (centerX + iconRadius * Math.cos(midAngleRad));
            int iconY = (int) (centerY + iconRadius * Math.sin(midAngleRad));

            g.setFont(K.FONT_BIG);
            g.setColor(K.TEXT_COLOR);
            FontMetrics iconFm = g.getFontMetrics();
            g.drawString(shape.getSymbol(),
                    iconX - iconFm.stringWidth(shape.getSymbol()) / 2,
                    iconY + iconFm.getAscent() / 2 - K.SHAPER_ICON_Y_OFFSET);
        }

        if (selectedSliceArea != null) {
            g.setStroke(new BasicStroke(K.HUD_SELECTED_STROKE_WIDTH));
            g.setColor(K.TEXT_COLOR);
            g.draw(selectedSliceArea);
        }

        g.setColor(K.UI_BACKGROUND_COLOR);
        g.fill(innerHole);
        g.setStroke(new BasicStroke(K.HUD_CENTER_STROKE_WIDTH));
        g.setColor(K.TEXT_COLOR_UNSELECTED);
        g.draw(innerHole);

        BrushShape selectedShape = shapes.get(selectedIdx);
        String shapeText = selectedShape.getName();
        g.setFont(K.FONT_SMALL);
        FontMetrics fm = g.getFontMetrics();
        int textX = centerX - fm.stringWidth(shapeText) / 2;
        int textY = centerY + (int) outerRadius + K.HUD_TEXT_Y_OFFSET + fm.getAscent();

        g.setColor(K.TEXT_COLOR);
        g.drawString(shapeText, textX, textY);
    }

    private void typer(Graphics2D g, KeyboardController keyController,
                        MouseController mouseController) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        List<BrushType> types = keyController.getSelectableTypes();
        int totalTypes = types.size();

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        double outerRadius = K.HUD_OUTER_RADIUS;
        double innerRadius = K.HUD_INNER_RADIUS;
        double angleStep = K.HUD_FULL_CIRCLE / totalTypes;

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
            if (hoveredIdx >= 0 && hoveredIdx < totalTypes) {
                keyController.setSelectedTypeIndex(hoveredIdx);
            }
        }

        int selectedIdx = keyController.getSelectedTypeIndex();
        Ellipse2D.Double innerHole = new Ellipse2D.Double(
                centerX - innerRadius, centerY - innerRadius,
                innerRadius * 2, innerRadius * 2
        );

        Area holeArea = new Area(innerHole);
        Area selectedSliceArea = null;

        for (int i = 0; i < totalTypes; i++) {
            BrushType type = types.get(i);
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
                g.setColor(K.HIGHLIGHT_COLOR);
            } else {
                g.setColor(K.UI_BACKGROUND_COLOR);
            }

            g.fill(sliceArea);
            g.setStroke(new BasicStroke(K.HUD_BORDER_STROKE_WIDTH));
            g.setColor(K.UI_BACKGROUND_BORDER_COLOR);
            g.draw(sliceArea);

            double midAngleRad = Math.toRadians(startAngle + angleStep / 2);
            double iconRadius = (innerRadius + outerRadius) / 2.0;
            int iconX = (int) (centerX + iconRadius * Math.cos(midAngleRad));
            int iconY = (int) (centerY + iconRadius * Math.sin(midAngleRad));

            g.setFont(K.FONT_BIG);
            g.setColor(K.TEXT_COLOR);
            FontMetrics iconFm = g.getFontMetrics();
            g.drawString(type.getSymbol(),
                    iconX - iconFm.stringWidth(type.getSymbol()) / 2,
                    iconY + iconFm.getAscent() / 2 - K.SHAPER_ICON_Y_OFFSET);
        }

        if (selectedSliceArea != null) {
            g.setStroke(new BasicStroke(K.HUD_SELECTED_STROKE_WIDTH));
            g.setColor(K.TEXT_COLOR);
            g.draw(selectedSliceArea);
        }

        g.setColor(K.UI_BACKGROUND_COLOR);
        g.fill(innerHole);
        g.setStroke(new BasicStroke(K.HUD_CENTER_STROKE_WIDTH));
        g.setColor(K.TEXT_COLOR_UNSELECTED);
        g.draw(innerHole);

        BrushType selectedType = types.get(selectedIdx);
        String typeText = selectedType.getName();
        g.setFont(K.FONT_SMALL);
        FontMetrics fm = g.getFontMetrics();
        int textX = centerX - fm.stringWidth(typeText) / 2;
        int textY = centerY + (int) outerRadius + K.HUD_TEXT_Y_OFFSET + fm.getAscent();

        g.setColor(K.TEXT_COLOR);
        g.drawString(typeText, textX, textY);
    }

    private void drawSlider(Graphics2D g,
                                    int xPadding,
                                    float minVal, float maxVal, float currentVal,
                                    long lastChangeTime,
                                    Color fillColor,
                                    String topSymbol, String bottomSymbol) {

        long timeSinceLastChange = System.currentTimeMillis() - lastChangeTime;
        if (timeSinceLastChange > K.HUD_SLIDER_VISIBLE_MS) {
            return;
        }

        float opacity = getOpacity(timeSinceLastChange);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        int sliderWidth = K.HUD_SLIDER_WIDTH;
        int sliderHeight = getHeight() - (K.HUD_SLIDER_Y_PADDING * 2);
        int sliderX = xPadding;
        int sliderY = K.HUD_SLIDER_Y_PADDING;

        g.setColor(K.UI_BACKGROUND_COLOR);
        g.fillRoundRect(sliderX, sliderY, sliderWidth, sliderHeight,
                K.HUD_SLIDER_CORNER_RADIUS, K.HUD_SLIDER_CORNER_RADIUS);

        float ratio = (currentVal - minVal) / (maxVal - minVal);
        ratio = Math.clamp(ratio, 0.0f, 1.0f);

        int knobHeight = (int) (ratio * sliderHeight);
        int knobY = sliderY + sliderHeight - knobHeight;

        g.setColor(fillColor);
        g.fillRoundRect(sliderX, knobY, sliderWidth, knobHeight,
                K.HUD_SLIDER_CORNER_RADIUS, K.HUD_SLIDER_CORNER_RADIUS);

        g.setFont(K.FONT_SMALL);
        FontMetrics fm = g.getFontMetrics();

        if (topSymbol != null && !topSymbol.isEmpty()) {
            g.setColor(K.TEXT_COLOR);
            g.drawString(topSymbol, sliderX + (sliderWidth / 2) -
                        (fm.stringWidth(topSymbol) / 2),
                      sliderY - K.HUD_SLIDER_SYMBOL_OFFSET);
        }

        if (bottomSymbol != null && !bottomSymbol.isEmpty()) {
            g.setColor(K.TEXT_COLOR);
            g.drawString(bottomSymbol, sliderX + (sliderWidth / 2) -
                        (fm.stringWidth(bottomSymbol) / 2),
                      sliderY + sliderHeight + fm.getAscent());
        }

        g.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, K.HUD_SLIDER_MAX_OPACITY));
    }

    private static float getOpacity(long timeSinceLastChange) {
        float opacity = K.HUD_SLIDER_MAX_OPACITY;
        long fadeStartTime = K.HUD_SLIDER_VISIBLE_MS -
                K.HUD_SLIDER_FADE_DURATION_MS;

        if (timeSinceLastChange > fadeStartTime) {
            opacity = K.HUD_SLIDER_MAX_OPACITY - (float)
                    (timeSinceLastChange - fadeStartTime) / (float)
                    K.HUD_SLIDER_FADE_DURATION_MS;
        }
        opacity = Math.clamp(opacity, K.HUD_SLIDER_MIN_OPACITY,
                            K.HUD_SLIDER_MAX_OPACITY);
        return opacity;
    }

    private void slider(Graphics2D g, Brush brush) {
        drawSlider(g,
                K.HUD_SLIDER_X_PADDING,
                K.MIN_BRUSH_RADIUS,
                K.MAX_BRUSH_RADIUS,
                brush.getRadius(),
                brush.getLastRadiusChangeTime(),
                K.HUD_SLIDER_COLOR,
                K.HUD_SLIDER_PLUS_SYMBOL,
                K.HUD_SLIDER_MINUS_SYMBOL
        );
    }

    private void regulate(Graphics2D g, Brush brush) {
        int xPosition = K.HUD_SLIDER_WIDTH + K.HUD_SLIDER_X_PADDING + K.HUD_SLIDER_SYMBOL_OFFSET;
        drawSlider(g, xPosition,
                K.MIN_COLD_TEMP,
                K.MAX_HOT_TEMP,
                world.getThermo().getAmbientTemp(),
                brush.getLastTemperatureChangeTime(),
                K.HUD_SLIDER_COLOR,
                "\uF06D",
                "\uE645"
        );
    }

    public int getParticleColor(int x, int y, ElementID element) {
        int baseColor = element.getColorArgb();
        if (element == ElementID.VOID) {
            return baseColor;
        }

        double v = (Math.sin(x * K.MERCURY_COLOR_WAVE_FREQUENCY + y *
                K.MERCURY_COLOR_WAVE_FREQUENCY) + 1) * K.MERCURY_COLOR_SHIFT_MULTIPLIER;

        if (element.isMetal()) {
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

    public int[] getPixelBuffer() {
        return pixelBuffer;
    }
}