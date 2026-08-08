package org.p4j.render;

import org.p4j.core.K;
import org.p4j.data.ElementID;
import org.p4j.input.MouseController;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

public class ElementMenu {
    private int selectedTabIndex = 0;
    private List<ElementID> currentElements;

    public ElementMenu() {
        load(0);
    }

    private void load(int index) {
        currentElements = new ArrayList<>();
        this.selectedTabIndex = index;

        for (ElementID e : ElementID.values()) {
            if (e == ElementID.EMPTY) continue;

            if (index == 0) {
                currentElements.add(e);
            } else if (index == 1 && e.isSolid()) {
                currentElements.add(e);
            } else if (index == 2 && e.isLiquid()) {
                currentElements.add(e);
            } else if (index == 3 && e.isGas()) {
                currentElements.add(e);
            } else if (index == 4 && e.isPowder()) {
                currentElements.add(e);
            }
        }
    }

    public ElementID getHoveredElement(int screenWidth, int screenHeight,
                                       MouseController mouse) {
        if (mouse == null || currentElements.isEmpty()) return null;

        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;
        int mouseX = mouse.getMouseX();
        int mouseY = mouse.getMouseY();

        double dx = mouseX - centerX;
        double dy = mouseY - centerY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        int outerRadius = K.WHEEL_OUTER_RADIUS;
        int innerRadius = K.WHEEL_INNER_RADIUS;

        if (distance < innerRadius || distance > outerRadius) {
            return null;
        }

        double angle = Math.toDegrees(Math.atan2(dy, dx)) + 90.0;
        if (angle < 0) angle += K.HUD_FULL_CIRCLE;

        int count = currentElements.size();
        double anglePerSlice = K.HUD_FULL_CIRCLE / count;
        int index = (int) (angle / anglePerSlice);

        if (index >= 0 && index < count) {
            return currentElements.get(index);
        }
        return null;
    }

    public void render(Graphics2D g, int width, int height,
                       MouseController mouse) {
        if (currentElements == null || currentElements.isEmpty()) return;
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = width / 2;
        int centerY = height / 2;
        int outerRadius = K.WHEEL_OUTER_RADIUS;
        int innerRadius = K.WHEEL_INNER_RADIUS;

        ElementID hoveredElement = getHoveredElement(width, height, mouse);
        g.setColor(K.WHEEL_BG_COLOR);
        g.fillOval(
                centerX - outerRadius - 4,
                centerY - outerRadius - 4,
                (outerRadius + 4) * 2,
                (outerRadius + 4) * 2);

        int count = currentElements.size();
        double anglePerSlice = K.HUD_FULL_CIRCLE / count;

        for (int i = 0; i < count; i++) {
            ElementID element = currentElements.get(i);
            double startAngle = 90.0 - (i + 1) * anglePerSlice;

            Arc2D.Double outerArc = new Arc2D.Double(
                    centerX - outerRadius, centerY - outerRadius,
                    outerRadius * 2, outerRadius * 2,
                    startAngle, anglePerSlice, Arc2D.PIE
            );

            Ellipse2D.Double innerCircle = new Ellipse2D.Double(
                    centerX - innerRadius, centerY - innerRadius,
                    innerRadius * 2, innerRadius * 2
            );

            Area sliceArea = new Area(outerArc);
            sliceArea.subtract(new Area(innerCircle));

            boolean isHovered = (element == hoveredElement);
            Color eColor = new Color(element.getColorArgb(), true);
            if (isHovered) {
                g.setColor(eColor.brighter());
            } else {
                g.setColor(eColor);
            }
            g.fill(sliceArea);

            g.setColor(K.WHEEL_BORDER_COLOR);
            g.setStroke(new BasicStroke(K.MENU_BORDER_STROKE));
            g.draw(sliceArea);
        }

        g.setColor(K.MENU_BACKGROUND_COLOR);
        g.fillOval(
                centerX - innerRadius,
                centerY - innerRadius,
                innerRadius * 2,
                innerRadius * 2
        );

        g.setColor(K.MENU_BORDER_COLOR);
        g.setStroke(new BasicStroke(K.MENU_BORDER_STROKE));
        g.drawOval(
                centerX - innerRadius,
                centerY - innerRadius,
                innerRadius * 2,
                innerRadius * 2
        );

        if (hoveredElement != null) {
            String eName = hoveredElement.getName();
            g.setFont(new Font(K.HUD_FONT_FAMILY, Font.BOLD,
                    K.MENU_SYMBOL_FONT_SIZE + 2));
            FontMetrics fmElem = g.getFontMetrics();

            int elemX = centerX - fmElem.stringWidth(eName) / 2;
            int elemY = centerY + outerRadius + 20 + fmElem.getAscent();

            Color eColor = new Color(hoveredElement.getColorArgb(), true);
            g.setColor(eColor);
            g.drawString(eName, elemX, elemY);
        }
    }
}