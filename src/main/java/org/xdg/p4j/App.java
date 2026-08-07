package org.xdg.p4j;

import org.xdg.p4j.core.SimulationLoop;
import org.xdg.p4j.core.World;
import org.xdg.p4j.data.ElementID;
import org.xdg.p4j.input.Brush;
import org.xdg.p4j.input.MouseController;
import org.xdg.p4j.render.FastRenderer;
import org.xdg.p4j.render.Palette;

import javax.swing.*;

public class App extends JFrame {
    private final FastRenderer render;
    private final Palette palette;
    private final World world;
    private final SimulationLoop loop;
    private final Brush brush;
    private final MouseController mouseController;

    public App(String title, int simulationWidth, int simulationHeight, int scale) {
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        this.palette = new Palette();
        this.world = new World(simulationWidth, simulationHeight);
        this.render = new FastRenderer(simulationWidth, simulationHeight, scale);
        this.brush = new Brush(ElementID.SAND, 3);
        this.loop = new SimulationLoop(world, render, palette);
        this.mouseController = new MouseController(world, brush, scale);

        add(render);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        render.addMouseListener(mouseController);
        render.addMouseMotionListener(mouseController);
        render.addMouseWheelListener(mouseController);

        loop.start();
    }

    static void main() {
        SwingUtilities.invokeLater(() -> new App("Powder4J",
                200, 200, 4));
    }
}
