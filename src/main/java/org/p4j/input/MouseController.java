package org.p4j.input;

import org.p4j.core.World;
import org.p4j.data.BrushType;
import org.p4j.data.ElementID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayDeque;
import java.util.Queue;

public class MouseController extends MouseAdapter implements
        MouseMotionListener, MouseWheelListener {
    private static final Logger log = LoggerFactory.getLogger(MouseController.class);
    private final KeyboardController keyboardController;
    private final World world;
    private final Brush brush;
    private final int scale;

    private boolean isPressed;
    private int lastGridX = -1;
    private int lastGridY = -1;

    private int mouseX;
    private int mouseY;

    public MouseController(World world, Brush brush,
                           KeyboardController keyboardController,
                           int scale) {
        this.world = world;
        this.brush = brush;
        this.keyboardController = keyboardController;
        this.scale = scale;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (keyboardController.wasTabPressed() ||
                keyboardController.wasAltPressed() ||
                keyboardController.wasShiftPressed()) return;

        int gridX = toGridX(e);
        int gridY = toGridY(e);

        if (brush.getType() == BrushType.DROPPER) {
            if (world.isInBounds(gridX, gridY)) {
                if (world.getElementAt(gridX, gridY) == ElementID.VOID) return;
                ElementID newborn = world.getElementAt(gridX, gridY);
                ElementID.unlock(newborn);
                brush.setElement(newborn);
                brush.setType(BrushType.BRUSH);
            }
            return;
        }

        log.trace("Mouse pressed at ({}, {})", e.getX(), e.getY());
        isPressed = true;

        if (brush.getType() == BrushType.FILLER) {
            floodFill(gridX, gridY);
        } else {
            paintLine(gridX, gridY, gridX, gridY);
        }

        lastGridX = gridX;
        lastGridY = gridY;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        log.trace("Mouse released");
        isPressed = false;
        lastGridX = -1;
        lastGridY = -1;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();

        if (!isPressed || keyboardController.wasTabPressed() ||
                keyboardController.wasAltPressed() ||
                keyboardController.wasShiftPressed()) {
            return;
        }

        int gridX = toGridX(e);
        int gridY = toGridY(e);

        if (brush.getType() == BrushType.FILLER) {
            if (gridX != lastGridX || gridY != lastGridY) {
                floodFill(gridX, gridY);
            }
        } else {
            if (lastGridX != -1 && lastGridY != -1) {
                paintLine(lastGridX, lastGridY, gridX, gridY);
            } else {
                paint(gridX, gridY);
            }
        }

        lastGridX = gridX;
        lastGridY = gridY;
    }

    private void floodFill(int startX, int startY) {
        if (!world.isInBounds(startX, startY)) return;

        ElementID targetElement = getActiveElement();
        ElementID startElement = world.getElementAt(startX, startY);

        if (startElement == targetElement) return;

        Queue<Point> queue = new ArrayDeque<>();
        queue.add(new Point(startX, startY));

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            int x = p.x;
            int y = p.y;

            if (!world.isInBounds(x, y)) continue;

            if (world.getElementAt(x, y) == startElement) {
                world.setCell(x, y, targetElement);
                queue.add(new Point(x + 1, y));
                queue.add(new Point(x - 1, y));
                queue.add(new Point(x, y + 1));
                queue.add(new Point(x, y - 1));
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int rotation = e.getWheelRotation();
        if (rotation < 0) {
            if (keyboardController.wasCtrlPressed()) {
                brush.changeTemperature(5);
                return;
            }
            brush.changeRadius(1);
        } else {
            if (keyboardController.wasCtrlPressed()) {
                brush.changeTemperature(-5);
                return;
            }
            brush.changeRadius(-1);
        }
    }

    private void paintLine(int x0, int y0, int x1, int y1) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            paint(x0, y0);

            if (x0 == x1 && y0 == y1) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }

    private void paint(int centerX, int centerY) {
        ElementID targetType = getActiveElement();
        int r = brush.getRadius();

        for (int dy = -r; dy <= r; dy++) {
            for (int dx = -r; dx <= r; dx++) {
                if (brush.contains(dx, dy)) {
                    int targetX = centerX + dx;
                    int targetY = centerY + dy;

                    if (world.isInBounds(targetX, targetY)) {
                        ElementID currentElement = world.getElementAt(targetX, targetY);
                        ElementID resultElement = getResultingElement(targetType, currentElement);

                        if (resultElement != null && resultElement != currentElement) {
                            world.setCell(targetX, targetY, resultElement);
                        }
                    }
                }
            }
        }
    }

    private ElementID getActiveElement() {
        if (brush.getType() == BrushType.ERASER) {
            return ElementID.VOID;
        }
        return brush.getElement();
    }

    private ElementID getResultingElement(ElementID brushElement,
                                          ElementID currentElement) {
        if (brushElement == ElementID.VOID) {
            return ElementID.VOID;
        }

        if (currentElement == ElementID.VOID) {
            return brushElement;
        }

        ElementID reactionProduct = world.getReaction()
                .produce(brushElement, currentElement);
        if (reactionProduct != null) {
            return reactionProduct;
        }

        if (brushElement.isSolid() && !currentElement.isSolid()) {
            return brushElement;
        }

        if (brushElement.isLiquid() && currentElement.isGas()) {
            return brushElement;
        }

        return currentElement;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    private int toGridX(MouseEvent e) {
        double scaleX = (double) e.getComponent().getWidth() / world.getWidth();
        return Math.clamp((int) (e.getX() / scaleX), 0, world.getWidth() - 1);
    }

    private int toGridY(MouseEvent e) {
        double scaleY = (double) e.getComponent().getHeight() / world.getHeight();
        return Math.clamp((int) (e.getY() / scaleY), 0, world.getHeight() - 1);
    }
}