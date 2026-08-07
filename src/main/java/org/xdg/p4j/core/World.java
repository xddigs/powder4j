package org.xdg.p4j.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdg.p4j.data.ElementID;

import java.util.Arrays;

/**
 * Represents the simulation grid where physical interactions occur.
 * The world maintains the state of each cell and implements the cellular
 * automata rules that govern material behavior, gravity, and fluid mechanics.
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
            boolean leftToRight = Math.random() > Constants.RANDOM_THRESHOLD;

            for (int i = 0; i < width; i++) {
                int x = leftToRight ? i : (width - 1 - i);
                int index = y * width + x;

                if (updated[index]) continue;

                ElementID type = ElementID.fromId(grid[index]);

                switch (type) {
                    case SAND -> updateSand(x, y, index);
                    case WATER -> updateWater(x, y, index);
                    case FIRE -> updateFire(x, y, index);
                    default -> {}
                }
            }
        }
    }

    private void updateSand(int x, int y, int idx) {
        if (y >= height - 1) return;

        int below = (y + 1) * width + x;
        int belowLeft = (y + 1) * width + (x - 1);
        int belowRight = (y + 1) * width + (x + 1);

        if (isSwappableForSand(grid[below])) {
            swap(idx, below);
        } else {
            boolean canLeft = (x > 0 && isSwappableForSand(grid[belowLeft]));
            boolean canRight = (x < width - 1 && isSwappableForSand(grid[belowRight]));

            if (canLeft && canRight) {
                int target = (Math.random() > Constants.RANDOM_THRESHOLD) ? belowLeft : belowRight;
                swap(idx, target);
            } else if (canLeft) {
                swap(idx, belowLeft);
            } else if (canRight) {
                swap(idx, belowRight);
            }
        }
    }

    private void updateWater(int x, int y, int idx) {
        if (y >= height - 1) return;

        int below = (y + 1) * width + x;
        int belowLeft = (y + 1) * width + (x - 1);
        int belowRight = (y + 1) * width + (x + 1);
        int left = y * width + (x - 1);
        int right = y * width + (x + 1);

        byte emptyId = ElementID.EMPTY.getId();

        if (grid[below] == emptyId) {
            swap(idx, below);
            return;
        }

        boolean canLeft = (x > 0 && grid[belowLeft] == emptyId);
        boolean canRight = (x < width - 1 && grid[belowRight] == emptyId);

        if (canLeft && canRight) {
            int target = (Math.random() > Constants.RANDOM_THRESHOLD) ? belowLeft : belowRight;
            swap(idx, target);
            return;
        } else if (canLeft) {
            swap(idx, belowLeft);
            return;
        } else if (canRight) {
            swap(idx, belowRight);
            return;
        }

        boolean canMoveLeft = (x > 0 && grid[left] == emptyId);
        boolean canMoveRight = (x < width - 1 && grid[right] == emptyId);

        if (canMoveLeft && canMoveRight) {
            int target = (Math.random() > Constants.RANDOM_THRESHOLD) ? left : right;
            swap(idx, target);
        } else if (canMoveLeft) {
            swap(idx, left);
        } else if (canMoveRight) {
            swap(idx, right);
        }
    }

    private void updateFire(int x, int y, int idx) {
        if (Math.random() < 0.15) {
            grid[idx] = ElementID.EMPTY.getId();
            return;
        }

        if (y <= 0) return;
        int above = (y - 1) * width + x;
        int aboveLeft = (y - 1) * width + (x - 1);
        int aboveRight = (y - 1) * width + (x + 1);
        byte emptyId = ElementID.EMPTY.getId();

        if (grid[above] == emptyId) {
            swap(idx, above);
        } else {
            boolean canLeft = (x > 0 && grid[aboveLeft] == emptyId);
            boolean canRight = (x < width - 1 && grid[aboveRight] == emptyId);

            if (canLeft && canRight) {
                int target = (Math.random() > Constants.RANDOM_THRESHOLD) ? aboveLeft : aboveRight;
                swap(idx, target);
            } else if (canLeft) {
                swap(idx, aboveLeft);
            } else if (canRight) {
                swap(idx, aboveRight);
            }
        }
    }

    private boolean isSwappableForSand(byte targetId) {
        return targetId == ElementID.EMPTY.getId() || targetId == ElementID.WATER.getId();
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

        for (int y = height - 40; y < height - 10; y++) {
            for (int x = centerX - 30; x < centerX + 30; x++) {
                setCell(x, y, ElementID.WATER);
            }
        }
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