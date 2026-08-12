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

    private final World world;
    private final Brush brush;
    private final Palette palette;

    private final RenderingEngine renderingEngine2D;

    private final KeyboardController keyController;
    private final MouseController mouseController;
    private SimulationLoop loop;

    public App(String title, int simulationWidth, int simulationHeight, int scale) {
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        this.world = new World(simulationWidth, simulationHeight);
        this.brush = new Brush(ElementID.SODIUM, K.DEFAULT_BRUSH_RADIUS, world);
        this.palette = new Palette();

        this.renderingEngine2D = new RenderingEngine(simulationWidth, simulationHeight, world, scale);

        this.keyController = new KeyboardController(brush, world, renderingEngine2D);
        this.mouseController = new MouseController(world, brush, keyController, scale);

        this.cardLayout = new CardLayout();
        this.mainContainer = new JPanel(cardLayout);
        Menu menuPanel = new Menu(this::start);

        mainContainer.add(menuPanel, "MENU");
        mainContainer.add(renderingEngine2D, "GAME_2D");

        add(mainContainer);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        cardLayout.show(mainContainer, "MENU");
    }

    private void start() {
        log.info("Starting a new fresh simulation");
        cardLayout.show(mainContainer, "GAME_2D");
        setupEngineInput(renderingEngine2D);

        this.loop = new SimulationLoop(world, renderingEngine2D,
                keyController, mouseController, brush);

        loop.start();
    }

    private void setupEngineInput(Component engineComponent) {
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        engineComponent.setFocusTraversalKeysEnabled(false);
        engineComponent.requestFocusInWindow();
        engineComponent.addMouseListener(mouseController);
        engineComponent.addMouseMotionListener(mouseController);
        engineComponent.addMouseWheelListener(mouseController);
        engineComponent.addKeyListener(keyController);
        addKeyListener(keyController);
    }

    static void main() {
        SwingUtilities.invokeLater(() -> new App(
                K.APP_TITLE + K.VERSION,
                K.DEFAULT_SIM_WIDTH, K.DEFAULT_SIM_HEIGHT,
                K.DEFAULT_SCALE));
    }
}