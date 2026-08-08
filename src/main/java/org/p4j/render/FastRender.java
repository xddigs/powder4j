package org.p4j.render;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.p4j.core.Constants;
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
    private static final Logger log =
            LoggerFactory.getLogger(FastRender.class);
    private final BufferedImage canvasImage;
    private final int[] pixelBuffer;
    private final int scale;

    private boolean shockwaveActive = false;
    private float shockwaveRadius = 0f;
    private float maxShockwaveRadius = 0f;
    private float shockwaveAlpha = Constants.SHOCKWAVE_ALPHA;
    private int shockwaveCenterX;
    private int shockwaveCenterY;

    private int currentCategoryIndex = 0;
    private final String[] categories = {
            "Gases", "Liquids", "Powders",
            "Metals & Solids", "Compounds & Misc"
    };

    public FastRender(int simWidth, int simHeight, int scale) {
        log.debug("Initializing renderer: {}x{} at scale {}",
                simWidth, simHeight, scale);
        this.scale = scale;
        Dimension size = new Dimension(
                simWidth * scale, simHeight * scale);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        this.canvasImage = new BufferedImage(
                simWidth, simHeight, BufferedImage.TYPE_INT_ARGB);
        this.pixelBuffer = ((DataBufferInt) canvasImage
                .getRaster().getDataBuffer()).getData();
    }

    public void updatePixels(short[] grid, int width) {
        int length = Math.min(grid.length, pixelBuffer.length);
        for (int i = 0; i < length; i++) {
            int x = i % width;
            int y = i / width;
            pixelBuffer[i] = getParticleColor(
                    x, y, ElementID.fromId(grid[i]));
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
            elementMenu(g, keyController, mouseController);
        }

        if (keyController.wasAltPressed()) {
            shaper(g, keyController, mouseController);
        }

        if (keyController.wasEPressed()) {
            shockwaveActive = true;
            shockwaveRadius = Constants.SHOCKWAVE_RADIUS_MAX;
            maxShockwaveRadius = (float) Math.hypot(
                    getWidth(), getHeight());
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
            shockwaveAlpha = Constants.SHOCKWAVE_ALPHA -
                    (progress * progress);

            short[] grid = world.getGrid();
            int simWidth = world.getWidth();
            int simHeight = world.getHeight();

            int centerSimX = shockwaveCenterX / scale;
            int centerSimY = shockwaveCenterY / scale;
            float currentSimRadius = shockwaveRadius / scale;
            float ringThicknessSim = Constants.SHOCKWAVE_WIDTH / scale +
                    Constants.SHOCKWAVE_RING_EXTRA_THICKNESS;

            int minX = Math.max(0, (int) (centerSimX -
                    currentSimRadius - ringThicknessSim));
            int maxX = Math.min(simWidth - 1, (int) (centerSimX +
                    currentSimRadius + ringThicknessSim));
            int minY = Math.max(0, (int) (centerSimY -
                    currentSimRadius - ringThicknessSim));
            int maxY = Math.min(simHeight - 1, (int) (centerSimY +
                    currentSimRadius + ringThicknessSim));

            for (int sy = minY; sy <= maxY; sy++) {
                for (int sx = minX; sx <= maxX; sx++) {
                    float dist = (float) Math.hypot(
                            sx - centerSimX, sy - centerSimY);
                    if (dist >= currentSimRadius - ringThicknessSim &&
                            dist <= currentSimRadius) {
                        int idx = sy * simWidth + sx;
                        grid[idx] = ElementID.EMPTY.getId();
                    }
                }
            }

            if (shockwaveAlpha <= Constants.SHOCKWAVE_MIN_ALPHA ||
                    shockwaveRadius >= maxShockwaveRadius) {
                shockwaveActive = false;
            } else {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER,
                        Math.clamp(shockwaveAlpha,
                                Constants.SHOCKWAVE_MIN_ALPHA,
                                Constants.SHOCKWAVE_MAX_ALPHA)));

                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(
                        Constants.SHOCKWAVE_WIDTH,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND));

                int diameter = (int) (shockwaveRadius * 2);
                g2d.drawOval(
                        shockwaveCenterX - (int) shockwaveRadius,
                        shockwaveCenterY - (int) shockwaveRadius,
                        diameter, diameter);
                g2d.dispose();
            }
        }

        g.dispose();
        bs.show();
    }

    private void elementMenu(Graphics2D g,
                             KeyboardController keyController,
                             MouseController mouseController) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int panelWidth = Constants.MENU_PANEL_WIDTH;
        int panelHeight = Constants.MENU_PANEL_HEIGHT;
        int panelX = (getWidth() - panelWidth) / 2;
        int panelY = (getHeight() - panelHeight) / 2;

        g.setColor(Constants.MENU_BACKGROUND_COLOR);
        g.fillRoundRect(panelX, panelY, panelWidth, panelHeight,
                Constants.MENU_CORNER_RADIUS,
                Constants.MENU_CORNER_RADIUS);

        g.setStroke(new BasicStroke(Constants.MENU_BORDER_STROKE));
        g.setColor(Constants.MENU_BORDER_COLOR);
        g.drawRoundRect(panelX, panelY, panelWidth, panelHeight,
                Constants.MENU_CORNER_RADIUS,
                Constants.MENU_CORNER_RADIUS);

        List<ElementID> allElements =
                keyController.getSelectableElements();
        List<ElementID> filteredElements =
                filterBy(allElements, currentCategoryIndex);

        int tabStartX = panelX + Constants.MENU_TAB_START_X_OFFSET;
        int tabY = panelY + Constants.MENU_TAB_START_Y_OFFSET;
        int tabHeight = Constants.MENU_TAB_HEIGHT;
        int tabSpacing = Constants.MENU_TAB_SPACING;

        g.setFont(new Font(Constants.HUD_FONT_FAMILY, Font.BOLD,
                Constants.MENU_TAB_FONT_SIZE));

        for (int i = 0; i < categories.length; i++) {
            FontMetrics fm = g.getFontMetrics();
            int tabWidth = fm.stringWidth(categories[i]) +
                    Constants.MENU_TAB_PADDING_X;

            int currentTabX = tabStartX;
            for (int j = 0; j < i; j++) {
                currentTabX += g.getFontMetrics().stringWidth(
                        categories[j]) +
                        Constants.MENU_TAB_PADDING_X + tabSpacing;
            }

            boolean isCategoryHovered =
                    mouseController.getMouseX() >= currentTabX &&
                            mouseController.getMouseX() <= currentTabX + tabWidth &&
                            mouseController.getMouseY() >= tabY &&
                            mouseController.getMouseY() <= tabY + tabHeight;

            if (i == currentCategoryIndex) {
                g.setColor(Constants.MENU_TAB_SELECTED_COLOR);
                g.fillRoundRect(currentTabX, tabY, tabWidth, tabHeight,
                        Constants.MENU_TAB_CORNER_RADIUS,
                        Constants.MENU_TAB_CORNER_RADIUS);
                g.setColor(Color.WHITE);
            } else if (isCategoryHovered) {
                g.setColor(Constants.MENU_TAB_HOVER_COLOR);
                g.fillRoundRect(currentTabX, tabY, tabWidth, tabHeight,
                        Constants.MENU_TAB_CORNER_RADIUS,
                        Constants.MENU_TAB_CORNER_RADIUS);
                g.setColor(Constants.MENU_TAB_HOVER_TEXT_COLOR);
            } else {
                g.setColor(Constants.MENU_TAB_INACTIVE_COLOR);
                g.fillRoundRect(currentTabX, tabY, tabWidth, tabHeight,
                        Constants.MENU_TAB_CORNER_RADIUS,
                        Constants.MENU_TAB_CORNER_RADIUS);
                g.setColor(Constants.MENU_TAB_INACTIVE_TEXT_COLOR);
            }

            g.drawString(categories[i], currentTabX + 12,
                    tabY + Constants.MENU_TAB_TEXT_Y_OFFSET);

            if (mouseController.isLeftPressed()) {
                if (isCategoryHovered) {
                    currentCategoryIndex = i;
                }
            }
        }

        int gridStartX = panelX + Constants.MENU_GRID_START_X_OFFSET;
        int gridStartY = panelY + Constants.MENU_GRID_START_Y_OFFSET;
        int cellSize = Constants.MENU_CELL_SIZE;
        int cellPadding = Constants.MENU_CELL_PADDING;
        int cols = Constants.MENU_GRID_COLS;

        int mx = mouseController.getMouseX();
        int my = mouseController.getMouseY();
        ElementID hoveredElement = null;

        for (int i = 0; i < filteredElements.size(); i++) {
            ElementID el = filteredElements.get(i);
            int col = i % cols;
            int row = i / cols;
            int cellX = gridStartX + col * (cellSize + cellPadding);
            int cellY = gridStartY + row * (cellSize + cellPadding);

            if (cellY + cellSize > panelY + panelHeight -
                    Constants.MENU_GRID_BOTTOM_MARGIN) {
                break;
            }

            boolean isHovered = mx >= cellX &&
                    mx <= cellX + cellSize &&
                    my >= cellY &&
                    my <= cellY + cellSize;

            if (isHovered) {
                g.setColor(Constants.MENU_CELL_HOVER_COLOR);
                hoveredElement = el;
                if (mouseController.isLeftPressed()) {
                    int globalIndex = allElements.indexOf(el);
                    if (globalIndex != -1) {
                        keyController.setSelectedIndex(globalIndex);
                    }
                }
            } else {
                g.setColor(Constants.MENU_CELL_DEFAULT_COLOR);
            }
            g.fillRoundRect(cellX, cellY, cellSize, cellSize,
                    Constants.MENU_TAB_CORNER_RADIUS,
                    Constants.MENU_TAB_CORNER_RADIUS);

            g.setColor(new Color(el.getColorArgb(), true));
            g.fillRoundRect(
                    cellX + Constants.MENU_CELL_INNER_OFFSET,
                    cellY + Constants.MENU_CELL_INNER_OFFSET,
                    cellSize - (Constants.MENU_CELL_INNER_OFFSET * 2),
                    cellSize - (Constants.MENU_CELL_INNER_OFFSET * 2),
                    Constants.MENU_CELL_INNER_CORNER_RADIUS,
                    Constants.MENU_CELL_INNER_CORNER_RADIUS);

            g.setFont(new Font(Constants.HUD_FONT_FAMILY, Font.BOLD,
                    Constants.MENU_SYMBOL_FONT_SIZE));
            g.setColor(Color.WHITE);
            FontMetrics fm = g.getFontMetrics();
            String symbol = el.getSymbol();

            g.setColor(new Color(0, 0, 0,
                    Constants.MENU_SYMBOL_SHADOW_ALPHA));
            g.drawString(symbol,
                    cellX + (cellSize - fm.stringWidth(symbol)) / 2 +
                            Constants.MENU_SYMBOL_SHADOW_X_OFFSET,
                    cellY + cellSize / 2 +
                            Constants.MENU_SYMBOL_SHADOW_Y_OFFSET);
            g.setColor(Color.WHITE);
            g.drawString(symbol,
                    cellX + (cellSize - fm.stringWidth(symbol)) / 2,
                    cellY + cellSize / 2 +
                            Constants.MENU_SYMBOL_Y_OFFSET);
        }

        int selectedIdx = keyController.getSelectedIndex();
        ElementID activeElement = hoveredElement != null ? hoveredElement :
                (selectedIdx >= 0 && selectedIdx < allElements.size() ?
                        allElements.get(selectedIdx) : null);

        if (activeElement != null) {
            g.setColor(Constants.MENU_FOOTER_BG_COLOR);
            g.fillRoundRect(
                    panelX + 20,
                    panelY + panelHeight -
                            Constants.MENU_FOOTER_BOTTOM_OFFSET,
                    panelWidth - 40,
                    Constants.MENU_FOOTER_HEIGHT,
                    Constants.MENU_FOOTER_CORNER_RADIUS,
                    Constants.MENU_FOOTER_CORNER_RADIUS);

            String footerText = activeElement.getName() + ", " +
                    activeElement.getSymbol();
            g.setFont(new Font(Constants.HUD_FONT_FAMILY, Font.BOLD,
                    Constants.MENU_FOOTER_FONT_SIZE));
            g.setColor(Constants.MENU_FOOTER_TEXT_COLOR);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(footerText,
                    panelX + (panelWidth - fm.stringWidth(footerText)) / 2,
                    panelY + panelHeight -
                            Constants.MENU_FOOTER_TEXT_Y_OFFSET);
        }
    }

    private List<ElementID> filterBy(List<ElementID> elements,
                                     int categoryIndex) {
        return elements.stream().filter(e -> switch (categoryIndex) {
            case 0 -> e.isGas();
            case 1 -> e.isLiquid();
            case 2 -> e.isPowder();
            case 3 -> e.isSolid() && !e.isPowder() &&
                    !e.isGas() && !e.isLiquid();
            case 4 -> !e.isGas() && !e.isLiquid() &&
                    !e.isPowder() && !e.isSolid();
            default -> true;
        }).toList();
    }

    private void shaper(Graphics2D g, KeyboardController keyController,
                        MouseController mouseController) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);

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

        if (distSq > innerRadius * innerRadius &&
                distSq < outerRadius * outerRadius) {
            int hoveredIdx = getHoveredIdx(dy, dx, angleStep);
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
            double startAngle = i * angleStep +
                    Constants.HUD_START_OFFSET_DEG;

            Arc2D.Double outerPie = new Arc2D.Double(
                    centerX - outerRadius, centerY - outerRadius,
                    outerRadius * 2, outerRadius * 2,
                    -startAngle, -angleStep, Arc2D.PIE
            );

            Area sliceArea = new Area(outerPie);
            sliceArea.subtract(holeArea);

            if (i == selectedIdx) {
                selectedSliceArea = sliceArea;
                g.setColor(Constants.HUD_SELECTED_SLICE_COLOR);
            } else {
                g.setColor(Constants.HUD_BACKGROUND_COLOR);
            }

            g.fill(sliceArea);

            g.setStroke(new BasicStroke(
                    Constants.HUD_BORDER_STROKE_WIDTH));
            g.setColor(Constants.HUD_BORDER_COLOR);
            g.draw(sliceArea);

            double midAngleRad = Math.toRadians(
                    startAngle + angleStep / 2);
            double iconRadius = (innerRadius + outerRadius) / 2.0;
            int iconX = (int) (centerX +
                    iconRadius * Math.cos(midAngleRad));
            int iconY = (int) (centerY +
                    iconRadius * Math.sin(midAngleRad));

            g.setFont(new Font(Constants.HUD_FONT_FAMILY, Font.BOLD,
                    Constants.SHAPER_ICON_FONT_SIZE));
            g.setColor(Color.WHITE);
            FontMetrics iconFm = g.getFontMetrics();
            g.drawString(shape.getSymbol(),
                    iconX - iconFm.stringWidth(shape.getSymbol()) / 2,
                    iconY + iconFm.getAscent() / 2 -
                            Constants.SHAPER_ICON_Y_OFFSET);
        }

        if (selectedSliceArea != null) {
            g.setStroke(new BasicStroke(
                    Constants.HUD_SELECTED_STROKE_WIDTH));
            g.setColor(Color.WHITE);
            g.draw(selectedSliceArea);
        }

        g.setColor(Constants.HUD_CENTER_COLOR);
        g.fill(innerHole);
        g.setStroke(new BasicStroke(
                Constants.HUD_CENTER_STROKE_WIDTH));
        g.setColor(Constants.HUD_TEXT_UNSELECTED);
        g.draw(innerHole);

        BrushShape selectedShape = shapes.get(selectedIdx);
        String shapeText = selectedShape.getName();
        g.setFont(new Font(Constants.HUD_FONT_FAMILY, Font.BOLD,
                Constants.HUD_FONT_SIZE));
        FontMetrics fm = g.getFontMetrics();
        int textX = centerX - fm.stringWidth(shapeText) / 2;
        int textY = centerY + (int) outerRadius +
                Constants.HUD_TEXT_Y_OFFSET + fm.getAscent();

        g.setColor(Color.WHITE);
        g.drawString(shapeText, textX, textY);
    }

    private static int getHoveredIdx(double dy, double dx, double angleStep) {
        double angle = Math.toDegrees(Math.atan2(dy, dx));
        if (angle < 0) {
            angle += Constants.RENDERING_FULL_CIRCLE_DEGREES;
        }

        double adjustedAngle = (angle -
                Constants.HUD_START_OFFSET_DEG) %
                Constants.RENDERING_FULL_CIRCLE_DEGREES;
        if (adjustedAngle < 0) {
            adjustedAngle +=
                    Constants.RENDERING_FULL_CIRCLE_DEGREES;
        }

        return (int) (adjustedAngle / angleStep);
    }

    private void slider(Graphics2D g, Brush brush) {
        long timeSinceLastChange = System.currentTimeMillis() -
                brush.getLastRadiusChangeTime();
        if (timeSinceLastChange > Constants.HUD_SLIDER_VISIBLE_MS) {
            return;
        }

        float opacity = getOpacity(timeSinceLastChange);

        g.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, opacity));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int sliderWidth = Constants.HUD_SLIDER_WIDTH;
        int sliderHeight = getHeight() -
                (Constants.HUD_SLIDER_Y_PADDING * 2);
        int sliderX = Constants.HUD_SLIDER_X_PADDING;
        int sliderY = Constants.HUD_SLIDER_Y_PADDING;

        g.setColor(new Color(
                Constants.COLOR_CHANNEL_MAX,
                Constants.COLOR_CHANNEL_MAX,
                Constants.COLOR_CHANNEL_MAX,
                Constants.HUD_SLIDER_TRACK_ALPHA));
        g.fillRoundRect(sliderX, sliderY, sliderWidth, sliderHeight,
                Constants.HUD_SLIDER_CORNER_RADIUS,
                Constants.HUD_SLIDER_CORNER_RADIUS);

        int minR = Constants.MIN_BRUSH_RADIUS;
        int maxR = Constants.MAX_BRUSH_RADIUS;
        int currentR = brush.getRadius();

        float ratio = (float) (currentR - minR) / (maxR - minR);
        int knobHeight = (int) (ratio * sliderHeight);
        int knobY = sliderY + sliderHeight - knobHeight;

        g.setColor(Constants.HUD_SLIDER_COLOR);
        g.fillRoundRect(sliderX, knobY, sliderWidth, knobHeight,
                Constants.HUD_SLIDER_CORNER_RADIUS,
                Constants.HUD_SLIDER_CORNER_RADIUS);

        g.setFont(new Font(Constants.HUD_FONT_FAMILY, Font.BOLD,
                Constants.HUD_SLIDER_LABEL_FONT_SIZE));
        FontMetrics fm = g.getFontMetrics();

        String plus = "+";
        g.drawString(plus,
                sliderX + (sliderWidth / 2) -
                        (fm.stringWidth(plus) / 2),
                sliderY - Constants.HUD_SLIDER_SYMBOL_OFFSET);

        String minus = "-";
        g.drawString(minus,
                sliderX + (sliderWidth / 2) -
                        (fm.stringWidth(minus) / 2),
                sliderY + sliderHeight + fm.getAscent());

        g.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER,
                Constants.HUD_SLIDER_MAX_OPACITY));
    }

    private static float getOpacity(long timeSinceLastChange) {
        float opacity = Constants.HUD_SLIDER_MAX_OPACITY;
        long fadeStartTime = Constants.HUD_SLIDER_VISIBLE_MS -
                Constants.HUD_SLIDER_FADE_DURATION_MS;
        if (timeSinceLastChange > fadeStartTime) {
            opacity = Constants.HUD_SLIDER_MAX_OPACITY -
                    (float) (timeSinceLastChange - fadeStartTime) /
                            (float) Constants.HUD_SLIDER_FADE_DURATION_MS;
        }
        opacity = Math.clamp(opacity,
                Constants.HUD_SLIDER_MIN_OPACITY,
                Constants.HUD_SLIDER_MAX_OPACITY);
        return opacity;
    }

    public int getParticleColor(int x, int y, ElementID element) {
        int baseColor = element.getColorArgb();
        if (element == ElementID.EMPTY) {
            return baseColor;
        }

        if (element == ElementID.MERCURY) {
            int shift = (int) ((Math.sin(x *
                    Constants.MERCURY_COLOR_WAVE_FREQUENCY +
                    y * Constants.MERCURY_COLOR_WAVE_FREQUENCY) + 1) *
                    Constants.MERCURY_COLOR_SHIFT_MULTIPLIER);
            return adjustBrightness(baseColor, shift);
        }

        if (element == ElementID.FIRE || element == ElementID.LAVA) {
            int noise = (int) (Math.random() *
                    Constants.FIRE_LAVA_COLOR_NOISE_RANGE -
                    Constants.FIRE_LAVA_COLOR_NOISE_OFFSET);
            return adjustBrightness(baseColor, noise);
        }

        int grain = ((x * Constants.PARTICLE_GRAIN_X_MULTIPLIER +
                y * Constants.PARTICLE_GRAIN_Y_MULTIPLIER) %
                Constants.PARTICLE_GRAIN_MODULO) -
                Constants.PARTICLE_GRAIN_OFFSET;
        return adjustBrightness(baseColor, grain);
    }

    private static int adjustBrightness(int argb, int delta) {
        int a = (argb >> 24) & Constants.COLOR_CHANNEL_MAX;
        int r = (argb >> 16) & Constants.COLOR_CHANNEL_MAX;
        int g = (argb >> 8) & Constants.COLOR_CHANNEL_MAX;
        int b = argb & Constants.COLOR_CHANNEL_MAX;

        r = Math.clamp(r + delta, 0, Constants.COLOR_CHANNEL_MAX);
        g = Math.clamp(g + delta, 0, Constants.COLOR_CHANNEL_MAX);
        b = Math.clamp(b + delta, 0, Constants.COLOR_CHANNEL_MAX);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}