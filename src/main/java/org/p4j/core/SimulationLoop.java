package org.p4j.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.p4j.input.Brush;
import org.p4j.input.KeyboardController;
import org.p4j.input.MouseController;
import org.p4j.render.RenderingEngine;
import org.p4j.render.Palette;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Manages the execution of the simulation and rendering at a fixed frequency.
 * This class ensures that the world state updates and frame rendering occur
 * consistently to provide a fluid visual experience.
 */
public class SimulationLoop implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(SimulationLoop.class);
    private final World world;
    private final RenderingEngine render;
    private final Palette palette;
    private boolean isRunning;
    private Thread thread;
    private final KeyboardController keyController;
    private final MouseController mouseController;

    private final Brush brush;

    public SimulationLoop(World world, RenderingEngine render, Palette palette,
                          KeyboardController keyController,
                          MouseController mouseController, Brush brush) {
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
                if (keyController.wasShakePressed()) {
                    float intensity = K.INERTIA_FORCE;
                    float forceX = (ThreadLocalRandom.current().nextFloat()
                            * 2.0f - 1.0f) * intensity;
                    float forceY = (ThreadLocalRandom.current().nextFloat()
                            * 2.0f - 1.0f) * intensity;
                    world.applyInertia(forceX, forceY);
                }
                delta--;
            }
            render.updatePixels(world);
            render.render(world, keyController, mouseController, brush);
            world.getCards().update(mouseController);
        }
    }
}
