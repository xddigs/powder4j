package org.xdg.p4j.core;
    
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdg.p4j.data.ElementID;
import java.util.Arrays;

/**
 * Represents the simulation grid where physical interactions occur.
 * The world maintains the state of each cell and implements the cellular
 * automata rules that govern material behavior and movement.
 */
public class World {
    private static final Logger log = LoggerFactory.getLogger(World.class);
    private final int width;
    private final int height;
    private final byte[] grid;
    private final boolean[] updated;

    public World(int width, int height) {
        log.debug("Constructing simulation world: {}x{}", width, height);
        this.width = width;
        this.height = height;
        this.grid = new byte[width * height];
        this.updated = new boolean[width * height];
        spawnTest();
    }

    public void update() {
        Arrays.fill(updated, false);
        for (int y = height - 1; y >= 0; y--) {
            boolean leftToRight = Math.random() > 0.5;
            for (int i = 0; i < width; i++) {
                int x = leftToRight ? i : (width - 1 - i);
                int index = y * width + x;
                if (updated[index]) continue;
                ElementID type = ElementID.fromId(grid[index]);
                if (type == ElementID.SAND) {
                    update(x, y, index);
                }
            }
        }
    }

    private void update(int x, int y, int idx) {
        if (y >= height - 1) return;
        int below = (y + 1) * width + x;
        int belowLeft = (y + 1) * width + (x - 1);
        int belowRight = (y + 1) * width + (x + 1);

        byte emptyId = ElementID.EMPTY.getId();

        if (grid[below] == emptyId) {
            swap(idx, below);
        } else {
            boolean canLeft = (x > 0 && grid[belowLeft] == emptyId);
            boolean canRight = (x < width - 1 && grid[belowRight] == emptyId);

            if (canLeft && canRight) {
                int target = (Math.random() > 0.5) ? belowLeft : belowRight;
                swap(idx, target);
            } else if (canLeft) {
                swap(idx, belowLeft);
            } else if (canRight) {
                swap(idx, belowRight);
            }
        }
    }

    private void swap(int i, int j) {
        byte temp = grid[i];
        grid[i] = grid[j];
        grid[j] = temp;
        updated[i] = true;
        updated[j] = true;
    }

    public void setCell(int x, int y, ElementID type) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            grid[y * width + x] = type.getId();
        }
    }

    private void spawnTest() {
        log.debug("Spawning initial test particles.");
        int centerX = width / 2;
        for (int y = 10; y < 40; y++) {
            for (int x = centerX - 15; x < centerX + 15; x++) {
                setCell(x, y, ElementID.SAND);
            }
        }
    }

    public byte[] getGrid() { return grid; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}