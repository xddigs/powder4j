package org.p4j.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.p4j.input.Brush;
import org.p4j.input.KeyboardController;
import org.p4j.input.MouseController;
import org.p4j.render.FastRender;
import org.p4j.render.Palette;

/**
 * Manages the execution of the simulation and rendering at a fixed frequency.
 * This class ensures that the world state updates and frame rendering occur
 * consistently to provide a fluid visual experience.
 */
public class SimulationLoop implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(SimulationLoop.class);
    private final World world;
    private final FastRender render;
    private final Palette palette;
    private boolean isRunning;
    private Thread thread;
    private final KeyboardController keyController;
    private final MouseController mouseController;

    private final Brush brush;

    public SimulationLoop(World world, FastRender render, Palette palette,
                          KeyboardController keyController, MouseController mouseController, Brush brush) {
        this.world = world;
        this.render = render;
        this.palette = palette;
        this.keyController = keyController;
        this.mouseController = mouseController;
        this.brush = brush;
    }

    public synchronized void start() {
        if (isRunning) return;
        log.debug("Starting simulation loop thread.");
        isRunning = true;
        thread = new Thread(this, "SimulationLoop");
        thread.start();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = K.TICKS_PER_SECOND;
        double ns = K.NANOSECONDS_IN_SECOND / amountOfTicks;
        double delta = 0;
        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                world.update();
                delta--;
            }
            render.updatePixels(world.getGrid(), world.getWidth());
            render.render(world, keyController, mouseController, brush);
        }
    }
}
