package org.xdg.p4j.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdg.p4j.render.FastRenderer;
import org.xdg.p4j.render.Palette;

public class SimulationLoop implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(SimulationLoop.class);
    private final World world;
    private final FastRenderer renderer;
    private final Palette palette;
    private boolean running;
    private Thread thread;

    public SimulationLoop(World world, FastRenderer renderer, Palette palette) {
        this.world = world;
        this.renderer = renderer;
        this.palette = palette;
    }

    public synchronized void start() {
        if (running) return;
        running = true;
        thread = new Thread(this, "SimulationLoop");
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                world.update();
                delta--;
            }
            renderer.updatePixels(world.getGrid(), palette);
            renderer.render();
        }
    }
}
