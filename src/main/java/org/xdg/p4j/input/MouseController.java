package org.xdg.p4j.input;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdg.p4j.core.Constants;
import org.xdg.p4j.core.World;
import org.xdg.p4j.data.ElementID;

import java.awt.event.*;

/**
 * Facilitates user interaction with the simulation through mouse input.
 * This controller translates mouse gestures and clicks into world
 * modifications, enabling the placement or removal of elements.
 */
public class MouseController extends MouseAdapter implements 
        MouseMotionListener, MouseWheelListener {
    private static final Logger log = LoggerFactory.getLogger(MouseController.class);
    private final World world;
    private final Brush brush;
    private final int scale;

    private boolean isPressed;
    private boolean isRightClick;
    
    private int lastGridX = -1;
    private int lastGridY = -1;

    public MouseController(World world, Brush brush, int scale) {
        this.world = world;
        this.brush = brush;
        this.scale = scale;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        log.trace("Mouse pressed at ({}, {})", e.getX(), e.getY());
        isPressed = true;
        isRightClick = (e.getButton() == Constants.MOUSE_BUTTON_RIGHT);

        int gridX = e.getX() / scale;
        int gridY = e.getY() / scale;

        paintLine(gridX, gridY, gridX, gridY);
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
        if (!isPressed) return;

        int gridX = e.getX() / scale;
        int gridY = e.getY() / scale;

        if (lastGridX != -1 && lastGridY != -1) {
            paintLine(lastGridX, lastGridY, gridX, gridY);
        } else {
            paint(gridX, gridY);
        }

        lastGridX = gridX;
        lastGridY = gridY;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int rotation = e.getWheelRotation();
        if (rotation < 0) {
            brush.changeRadius(1);
        } else {
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
        ElementID targetType = isRightClick ? ElementID.EMPTY : brush.getCurrentElement();
        int r = brush.getRadius();
        int rSquared = r * r;

        for (int dy = -r; dy <= r; dy++) {
            for (int dx = -r; dx <= r; dx++) {
                if (dx * dx + dy * dy <= rSquared) {
                    world.setCell(centerX + dx, centerY + dy, targetType);
                }
            }
        }
    }
}