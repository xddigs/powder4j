package org.p4j;

import org.p4j.core.K;
import org.p4j.core.SimulationLoop;
import org.p4j.core.World;
import org.p4j.data.ElementID;
import org.p4j.gui.Engine;
import org.p4j.gui.Menu;
import org.p4j.input.Brush;
import org.p4j.input.KeyboardController;
import org.p4j.input.MouseController;
import org.p4j.render.Lwjgl3Engine;
import org.p4j.render.RenderingEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    private final World world;
    private final Engine engine;
    private final SimulationLoop loop;

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private RenderingEngine swingRender;
    private KeyboardController keyController;
    private MouseController mouseController;

    public App(String title, int simWidth, int simHeight, int simDepth, int scale, boolean is3D) {
        this.world = new World(simWidth, simHeight, simDepth);

        if (is3D) {
            this.engine = new Lwjgl3Engine(simWidth, simHeight, simDepth, world);
            this.engine.init(world);

            this.loop = new SimulationLoop(world, engine);
            this.loop.start();
        } else {
            this.frame = new JFrame(title);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setFocusTraversalKeysEnabled(false);

            Brush brush = new Brush(ElementID.SODIUM, K.DEFAULT_BRUSH_RADIUS, world);
            this.swingRender = new RenderingEngine(simWidth, simHeight, world, scale);
            this.engine = swingRender;

            this.keyController = new KeyboardController(brush, world, swingRender);
            this.mouseController = new MouseController(world, brush, keyController, scale);

            this.loop = new SimulationLoop(world, swingRender, keyController, mouseController, brush);

            this.cardLayout = new CardLayout();
            this.mainContainer = new JPanel(cardLayout);
            Menu menuPanel = new Menu(this::start);

            mainContainer.add(menuPanel, "MENU");
            mainContainer.add(swingRender, "GAME");

            frame.add(mainContainer);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            cardLayout.show(mainContainer, "MENU");
        }
    }

    private void start() {
        if (cardLayout != null && mainContainer != null && swingRender != null) {
            cardLayout.show(mainContainer, "GAME");
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            swingRender.setFocusTraversalKeysEnabled(false);
            swingRender.requestFocusInWindow();
            swingRender.addMouseListener(mouseController);
            swingRender.addMouseMotionListener(mouseController);
            swingRender.addMouseWheelListener(mouseController);
            swingRender.addKeyListener(keyController);
            frame.addKeyListener(keyController);

            loop.start();
            log.info("Simulation loop started from menu.");
        }
    }

    static void main() {
        if (K.IS_3D) {
            new App(K.APP_TITLE + K.VERSION, K.DEFAULT_SIM_WIDTH, K.DEFAULT_SIM_HEIGHT,
                    K.DEFAULT_SIM_DEPTH, K.DEFAULT_SCALE, true);
        } else {
            SwingUtilities.invokeLater(() -> new App(
                    K.APP_TITLE + K.VERSION,
                    K.DEFAULT_SIM_WIDTH, K.DEFAULT_SIM_HEIGHT, 1,
                    K.DEFAULT_SCALE, false));
        }
    }
}