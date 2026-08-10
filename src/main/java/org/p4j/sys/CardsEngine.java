package org.p4j.sys;

import org.p4j.core.K;
import org.p4j.core.World;
import org.p4j.data.ElementCard;
import org.p4j.data.ElementID;
import org.p4j.input.MouseController;

import java.awt.*;

public class CardsEngine {
    private final World world;
    private int lastGridX = -1;
    private int lastGridY = -1;
    private long hoverStartTime = 0L;
    private boolean isHoveringValidPixel = false;

    public CardsEngine(World world) {
        this.world = world;
    }

    public void update(MouseController mouseController) {
        int gx = mouseController.getMouseX() / K.DEFAULT_SCALE;
        int gy = mouseController.getMouseY() / K.DEFAULT_SCALE;

        if (!world.isInBounds(gx, gy)) {
            resetHover();
            return;
        }

        ElementID currentElem = world.getElementAt(gx, gy);

        if (currentElem == ElementID.EMPTY) {
            resetHover();
            return;
        }

        if (gx != lastGridX || gy != lastGridY) {
            lastGridX = gx;
            lastGridY = gy;
            hoverStartTime = System.currentTimeMillis();
            isHoveringValidPixel = true;
        }
    }

    public void render(Graphics2D g2d, MouseController mouseController,
                       int screenWidth, int screenHeight) {
        if (!isHoveringValidPixel) return;

        long elapsedTime = System.currentTimeMillis() - hoverStartTime;
        if (elapsedTime < K.HOVER_DELAY_MS) return;

        ElementID elem = world.getElementAt(lastGridX, lastGridY);
        if (elem == ElementID.EMPTY) return;

        float liveTemp = world.getTemperatureAt(lastGridX, lastGridY);
        ElementCard card = createCard(elem, liveTemp);

        int px = lastGridX * K.DEFAULT_SCALE;
        int py = lastGridY * K.DEFAULT_SCALE;
        g2d.setColor(K.HIGHLIGHT_COLOR);
        g2d.drawRect(px - 1, py - 1,
                K.DEFAULT_SCALE + 1,
                K.DEFAULT_SCALE + 1);

        int mx = mouseController.getMouseX();
        int my = mouseController.getMouseY();

        int cardX = mx + K.MOUSE_OFFSET_X;
        int cardY = my + K.MOUSE_OFFSET_Y;

        if (cardX + K.CARD_WIDTH > screenWidth) {
            cardX = mx - K.CARD_WIDTH - K.MOUSE_OFFSET_X;
        }
        if (cardY + K.CARD_HEIGHT > screenHeight) {
            cardY = my - K.CARD_HEIGHT - K.MOUSE_OFFSET_Y;
        }

        g2d.setColor(K.MENU_BACKGROUND_COLOR);
        g2d.fillRoundRect(cardX, cardY, K.CARD_WIDTH, K.CARD_HEIGHT,
                          K.HUD_SLIDER_CORNER_RADIUS, K.HUD_SLIDER_CORNER_RADIUS);
        g2d.setColor(K.HIGHLIGHT_COLOR);
        g2d.drawRoundRect(cardX, cardY, K.CARD_WIDTH, K.CARD_HEIGHT,
                          K.HUD_SLIDER_CORNER_RADIUS, K.HUD_SLIDER_CORNER_RADIUS);

        g2d.setFont(new Font(K.HUD_FONT_FAMILY, Font.BOLD, K.HUD_FONT_SIZE));
        g2d.setColor(K.TEXT_COLOR);
        int lineY = cardY + K.CARD_PADDING + K.CARD_OFFSET;

        String header = card.name() + " (" + card.symbol() + ")";
        g2d.drawString(header, cardX + K.CARD_PADDING, lineY);

        g2d.setFont(new Font(K.HUD_FONT_FAMILY, Font.PLAIN, K.HUD_FONT_SIZE));
        g2d.setColor(K.TEXT_COLOR);
        lineY += K.CARD_LINEHEIGHT;
        g2d.drawString("T: " + format(
                card.liveTemp()), cardX + K.CARD_PADDING, lineY);

        lineY += K.CARD_LINEHEIGHT;
        g2d.drawString("BP: " + format(
                card.boilingPoint()), cardX + K.CARD_PADDING, lineY);

        lineY += K.CARD_LINEHEIGHT;
        g2d.drawString("MP: " + format(
                card.meltingPoint()), cardX + K.CARD_PADDING, lineY);
    }

    private void resetHover() {
        lastGridX = -1;
        lastGridY = -1;
        hoverStartTime = 0L;
        isHoveringValidPixel = false;
    }

    private String format(float temp) {
        if (temp >= Float.MAX_VALUE ||
            Float.isInfinite(temp)) {
            return K.NANINF;
        }
        if (Math.abs(temp) >= 1_000_000f) {
            return String.format("%.2e°C", temp);
        }
        return String.format("%.1f°C", temp);
    }

    private ElementCard createCard(ElementID e, float liveTemp) {
        return new ElementCard(
                e,
                e.getId(),
                e.getName(),
                e.getSymbol(),
                liveTemp,
                e.getDefaultTemp(),
                e.getBoilingPoint(),
                e.getMeltingPoint()
        );
    }
}