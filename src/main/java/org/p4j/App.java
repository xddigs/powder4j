package org.p4j;

import org.p4j.core.K;
import org.p4j.core.SimulationLoop;
import org.p4j.core.World;
import org.p4j.data.ElementID;
import org.p4j.gui.Menu;
import org.p4j.input.Brush;
import org.p4j.input.KeyboardController;
import org.p4j.input.MouseController;
import org.p4j.render.Palette;
import org.p4j.render.RenderingEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("unused")
public class App extends JFrame {
    private static final Logger log = LoggerFactory.getLogger(App.class);
    private final CardLayout cardLayout;
    private final JPanel mainContainer;
    private final SimulationLoop loop;
    private final World world;
    private final RenderingEngine render;
    private final Brush brush;
    private final Palette palette;
    private final KeyboardController keyController;
    private final MouseController mouseController;

    public App(String title, int simulationWidth,
               int simulationHeight, int scale) {
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        this.world = new World(simulationWidth, simulationHeight);
        this.brush = new Brush(ElementID.SODIUM, K.DEFAULT_BRUSH_RADIUS, world);
        this.palette = new Palette();
        this.render = new RenderingEngine(simulationWidth, simulationHeight, world, scale);

        this.keyController = new KeyboardController(brush, world, render);
        this.mouseController = new MouseController(world, brush, keyController, scale);
        this.loop = new SimulationLoop(world, render,
                keyController, mouseController, brush);

        this.cardLayout = new CardLayout();
        this.mainContainer = new JPanel(cardLayout);
        Menu menuPanel = new Menu(this::start);

        mainContainer.add(menuPanel, "MENU");
        mainContainer.add(render, "GAME");

        add(mainContainer);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        cardLayout.show(mainContainer, "MENU");
    }

    private void start() {
        cardLayout.show(mainContainer, "GAME");
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        render.setFocusTraversalKeysEnabled(false);
        render.requestFocusInWindow();
        render.addMouseListener(mouseController);
        render.addMouseMotionListener(mouseController);
        render.addMouseWheelListener(mouseController);
        render.addKeyListener(keyController);
        addKeyListener(keyController);
        loop.start();
        log.info("Simulation loop started from menu.");
    }

    static void main() {
        SwingUtilities.invokeLater(() -> new App(
                K.APP_TITLE + K.VERSION,
                K.DEFAULT_SIM_WIDTH, K.DEFAULT_SIM_HEIGHT,
                K.DEFAULT_SCALE));
    }
}