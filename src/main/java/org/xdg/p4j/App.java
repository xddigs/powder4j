package org.xdg.p4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdg.p4j.core.Constants;
import org.xdg.p4j.core.SimulationLoop;
import org.xdg.p4j.core.World;
import org.xdg.p4j.data.ElementID;
import org.xdg.p4j.input.Brush;
import org.xdg.p4j.input.KeyboardController;
import org.xdg.p4j.input.MouseController;
import org.xdg.p4j.render.FastRender;
import org.xdg.p4j.render.Palette;

import javax.swing.*;

/**
 * The primary entry point for the Powder4J application.
 * This class orchestrates the initialization of the graphical user interface,
 * the simulation environment, and the rendering pipeline.
 */
public class App extends JFrame {
    private static final Logger log = LoggerFactory.getLogger(App.class);
    private final FastRender render;
    private final Palette palette;
    private final World world;
    private final SimulationLoop loop;
    private final Brush brush;
    private final MouseController mouseController;
    private final KeyboardController keyController;

    public App(String title, int simulationWidth, int simulationHeight, int scale) {
        log.info("Initializing Powder4J: {}x{} (scale: {})", simulationWidth, simulationHeight, scale);
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setFocusTraversalKeysEnabled(false);

        this.brush = new Brush(ElementID.SAND, Constants.DEFAULT_BRUSH_RADIUS);
        this.keyController = new KeyboardController(brush);

        this.palette = new Palette();
        this.world = new World(simulationWidth, simulationHeight);
        this.render = new FastRender(simulationWidth, simulationHeight, scale);
        this.mouseController = new MouseController(world, brush, keyController, scale);
        this.loop = new SimulationLoop(world, render, palette, keyController, mouseController, brush);

        add(render);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        render.addMouseListener(mouseController);
        render.addMouseMotionListener(mouseController);
        render.addMouseWheelListener(mouseController);
        render.addKeyListener(keyController);
        render.setFocusTraversalKeysEnabled(false);
        addKeyListener(keyController);

        loop.start();
        log.info("Application started successfully.");
    }

    static void main() {
        log.info("Starting main entry point...");
        SwingUtilities.invokeLater(() -> new App(
                Constants.APP_TITLE + Constants.VERSION,
                Constants.DEFAULT_SIM_WIDTH, Constants.DEFAULT_SIM_HEIGHT,
                Constants.DEFAULT_SCALE));
    }
}
