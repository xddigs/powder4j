package org.xdg.p4j.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdg.p4j.data.ElementID;

import java.util.Arrays;

public class World {
    private static final Logger log = LoggerFactory.getLogger(World.class);
    private final int width;
    private final int height;
    private final byte[] grid;
    private final boolean[] updated;
    private final float[] velocity;

    public World(int width, int height) {
        log.debug("Constructing simulation world: {}x{}", width, height);
        this.width = width;
        this.height = height;
        this.grid = new byte[width * height];
        this.updated = new boolean[width * height];
        this.velocity = new float[width * height];
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
                    case SAND, GRAVEL -> updateSand(x, y, index);
                    case WATER, OIL -> updateFluid(x, y, index);
                    case FIRE -> updateFire(x, y, index);
                    case SMOKE_DARK, SMOKE_GRAY, SMOKE_LIGHT -> updateSmoke(x, y, index);
                    default -> velocity[index] = 0;
                }
            }
        }
    }

    private int getDensity(byte id) {
        ElementID el = ElementID.fromId(id);
        return switch (el) {
            case SAND, GRAVEL -> 3;
            case WATER -> 2;
            case OIL -> 1;
            default -> 0;
        };
    }

    private boolean canDisplace(byte upperId, byte lowerId) {
        if (lowerId == ElementID.WALL.getId()) return false;
        return getDensity(upperId) > getDensity(lowerId);
    }

    private void updateSand(int x, int y, int idx) {
        if (y >= height - 1) {
            velocity[idx] = 0;
            return;
        }

        byte currentId = grid[idx];
        float currentVel = velocity[idx] + Constants.GRAVITY;
        if (currentVel > Constants.MAX_FALL_SPEED) {
            currentVel = Constants.MAX_FALL_SPEED;
        }

        int maxPossibleSteps = (int) currentVel;
        if (maxPossibleSteps < 1) maxPossibleSteps = 1;
        int actualSteps = 0;
        int currentY = y;

        while (actualSteps < maxPossibleSteps) {
            int nextY = currentY + 1;
            if (nextY >= height) break;

            int targetIdx = nextY * width + x;
            if (canDisplace(currentId, grid[targetIdx])) {
                currentY = nextY;
                actualSteps++;
            } else {
                break;
            }
        }

        if (actualSteps > 0) {
            int targetIdx = currentY * width + x;
            velocity[idx] = currentVel;
            swap(idx, targetIdx);
            return;
        }

        velocity[idx] = 0;
        int belowLeft = (y + 1) * width + (x - 1);
        int belowRight = (y + 1) * width + (x + 1);

        boolean canLeft = (x > 0 && canDisplace(currentId, grid[belowLeft]));
        boolean canRight = (x < width - 1 && canDisplace(currentId, grid[belowRight]));

        if (canLeft && canRight) {
            int target = (Math.random() > Constants.RANDOM_THRESHOLD) ? belowLeft : belowRight;
            swap(idx, target);
        } else if (canLeft) {
            swap(idx, belowLeft);
        } else if (canRight) {
            swap(idx, belowRight);
        }
    }

    private void updateFluid(int x, int y, int idx) {
        if (y >= height - 1) {
            velocity[idx] = 0;
            return;
        }

        byte currentId = grid[idx];
        byte emptyId = ElementID.EMPTY.getId();
        int actualSteps = 0;
        int currentY = y;

        while (actualSteps < Constants.WATER_FALL_SPEED) {
            int nextY = currentY + 1;
            if (nextY >= height) break;

            int targetIdx = nextY * width + x;
            if (canDisplace(currentId, grid[targetIdx])) {
                currentY = nextY;
                actualSteps++;
            } else {
                break;
            }
        }

        if (actualSteps > 0) {
            int targetIdx = currentY * width + x;
            swap(idx, targetIdx);
            return;
        }

        int belowLeft = (y + 1) * width + (x - 1);
        int belowRight = (y + 1) * width + (x + 1);

        boolean canBelowLeft = (x > 0 && canDisplace(currentId, grid[belowLeft]));
        boolean canBelowRight = (x < width - 1 && canDisplace(currentId, grid[belowRight]));

        if (canBelowLeft && canBelowRight) {
            int target = (Math.random() > Constants.RANDOM_THRESHOLD) ? belowLeft : belowRight;
            swap(idx, target);
            return;
        } else if (canBelowLeft) {
            swap(idx, belowLeft);
            return;
        } else if (canBelowRight) {
            swap(idx, belowRight);
            return;
        }

        boolean leftToRight = Math.random() > 0.5;
        int bestTargetIdx = -1;

        for (int i = Constants.WATER_DISPERSION_RATE; i >= 1; i--) {
            int dir = leftToRight ? 1 : -1;
            int nextX = x + (i * dir);
            if (nextX >= 0 && nextX < width) {
                int targetIdx = y * width + nextX;
                if (grid[targetIdx] == emptyId) {
                    if (isPathClearHorizontal(x, nextX, y)) {
                        bestTargetIdx = targetIdx;
                        break;
                    }
                }
            }
        }

        if (bestTargetIdx != -1) {
            swap(idx, bestTargetIdx);
            return;
        }

        for (int i = Constants.WATER_DISPERSION_RATE; i >= 1; i--) {
            int dir = leftToRight ? -1 : 1;
            int nextX = x + (i * dir);
            if (nextX >= 0 && nextX < width) {
                int targetIdx = y * width + nextX;
                if (grid[targetIdx] == emptyId) {
                    if (isPathClearHorizontal(x, nextX, y)) {
                        bestTargetIdx = targetIdx;
                        break;
                    }
                }
            }
        }

        if (bestTargetIdx != -1) {
            swap(idx, bestTargetIdx);
        }
    }

    private boolean isPathClearHorizontal(int startX, int endX, int y) {
        int step = (endX > startX) ? 1 : -1;
        int currentX = startX + step;
        byte emptyId = ElementID.EMPTY.getId();

        while (currentX != endX) {
            if (grid[y * width + currentX] != emptyId) {
                return false;
            }
            currentX += step;
        }
        return true;
    }

    private void updateFire(int x, int y, int idx) {
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    ElementID neighbor = ElementID.fromId(grid[nIdx]);
                    if ((neighbor == ElementID.WATER || neighbor == ElementID.OIL)
                            && Math.random() < Constants.FIRE_EVAPORATION_CHANCE) {

                        double rand = Math.random();
                        ElementID smokeType = ElementID.SMOKE_DARK;
                        if (rand > 0.66) smokeType = ElementID.SMOKE_LIGHT;
                        else if (rand > 0.33) smokeType = ElementID.SMOKE_GRAY;

                        grid[nIdx] = smokeType.getId();
                        velocity[nIdx] = 0;
                        updated[nIdx] = true;
                    }
                }
            }
        }

        if (Math.random() < 0.15) {
            grid[idx] = ElementID.EMPTY.getId();
            velocity[idx] = 0;
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
                int target = (Math.random() > Constants.RANDOM_THRESHOLD) ?
                        aboveLeft : aboveRight;
                swap(idx, target);
            } else if (canLeft) {
                swap(idx, aboveLeft);
            } else if (canRight) {
                swap(idx, aboveRight);
            }
        }
    }

    private void updateSmoke(int x, int y, int idx) {
        if (Math.random() < Constants.SMOKE_DISSIPATION_CHANCE) {
            grid[idx] = ElementID.EMPTY.getId();
            velocity[idx] = 0;
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
                int target = (Math.random() > Constants.RANDOM_THRESHOLD) ?
                        aboveLeft : aboveRight;
                swap(idx, target);
            } else if (canLeft) {
                swap(idx, aboveLeft);
            } else if (canRight) {
                swap(idx, aboveRight);
            }
        }
    }

    private void swap(int i, int j) {
        byte tempGrid = grid[i];
        grid[i] = grid[j];
        grid[j] = tempGrid;

        float tempVel = velocity[i];
        velocity[i] = velocity[j];
        velocity[j] = tempVel;

        updated[i] = true;
        updated[j] = true;
    }

    public void setCell(int x, int y, ElementID type) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            int index = y * width + x;
            grid[index] = type.getId();
            velocity[index] = 0;
        }
    }

    private void spawnTest() {
        log.debug("Spawning initial test particles.");
        int centerX = width / 2;

        for (int y = height - 20; y < height - 10; y++) {
            for (int x = centerX - 30; x < centerX + 30; x++) {
                setCell(x, y, ElementID.WATER);
            }
        }

        for (int y = height - 35; y < height - 25; y++) {
            for (int x = centerX - 15; x < centerX + 15; x++) {
                setCell(x, y, ElementID.OIL);
            }
        }
    }

    public byte[] getGrid() { return grid; }
}