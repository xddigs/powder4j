package org.p4j;

import org.p4j.core.K;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.p4j.core.SimulationLoop;
import org.p4j.core.World;
import org.p4j.data.ElementID;
import org.p4j.input.Brush;
import org.p4j.input.KeyboardController;
import org.p4j.input.MouseController;
import org.p4j.render.Render;
import org.p4j.render.Palette;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * The primary entry point for the Powder4J application.
 * This class orchestrates the initialization of the graphical user interface,
 * the simulation environment, and the rendering pipeline.
 */
public class App extends JFrame {
    private static final Logger log = LoggerFactory.getLogger(App.class);
    private final Render render;
    private final Palette palette;
    private final World world;
    private final SimulationLoop loop;
    private final Brush brush;
    private final MouseController mouseController;
    private final KeyboardController keyController;
    private int lastWindowX = Integer.MIN_VALUE;
    private int lastWindowY = Integer.MIN_VALUE;

    public App(String title, int simulationWidth, int simulationHeight, int scale) {
        log.info("Initializing Powder4J: {}x{} (scale: {})",
                simulationWidth, simulationHeight, scale);
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setFocusTraversalKeysEnabled(false);

        this.world = new World(simulationWidth, simulationHeight);
        this.brush = new Brush(ElementID.SODIUM, K.DEFAULT_BRUSH_RADIUS, world);
        this.palette = new Palette();
        this.render = new Render(simulationWidth, simulationHeight, world, scale);

        this.keyController = new KeyboardController(brush, world, render);
        this.mouseController = new MouseController(
                world, brush, keyController, scale);
        this.loop = new SimulationLoop(world, render, palette,
                keyController, mouseController, brush);

        add(render);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        render.addMouseListener(mouseController);
        render.addMouseMotionListener(mouseController);
        render.addMouseWheelListener(mouseController);
        render.addKeyListener(keyController);
        render.setFocusTraversalKeysEnabled(false);
        render.requestFocusInWindow();
        addKeyListener(keyController);
        addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                int currentX = getLocationOnScreen().x;
                int currentY = getLocationOnScreen().y;

                if (lastWindowX == Integer.MIN_VALUE) {
                    lastWindowX = currentX;
                    lastWindowY = currentY;
                    return;
                }

                int deltaX = currentX - lastWindowX;
                int deltaY = currentY - lastWindowY;

                if (Math.abs(deltaX) > K.WINDOW_SHAKING_THRESHOLD ||
                        Math.abs(deltaY) > K.WINDOW_SHAKING_THRESHOLD) {
                    world.applyInertia(-deltaX, -deltaY);
                }

                lastWindowX = currentX;
                lastWindowY = currentY;
            }
        });

        loop.start();
        log.info("Application started successfully.");
    }

    static void main() {
        log.info("Starting main entry point...");
        SwingUtilities.invokeLater(() -> new App(
                K.APP_TITLE + K.VERSION,
                K.DEFAULT_SIM_WIDTH, K.DEFAULT_SIM_HEIGHT,
                K.DEFAULT_SCALE));
    }
}
