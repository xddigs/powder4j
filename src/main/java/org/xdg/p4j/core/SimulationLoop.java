package org.xdg.p4j.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdg.p4j.input.KeyboardController;
import org.xdg.p4j.input.MouseController;
import org.xdg.p4j.render.FastRender;
import org.xdg.p4j.render.Palette;

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

    public SimulationLoop(World world, FastRender render, Palette palette,
                          KeyboardController keyController, MouseController mouseController) {
        this.world = world;
        this.render = render;
        this.palette = palette;
        this.keyController = keyController;
        this.mouseController = mouseController;
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
        double amountOfTicks = Constants.TICKS_PER_SECOND;
        double ns = Constants.NANOSECONDS_IN_SECOND / amountOfTicks;
        double delta = 0;
        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                world.update();
                delta--;
            }
            render.updatePixels(world.getGrid(), palette);
            render.render(keyController, mouseController);
        }
    }
}
