package org.xdg.p4j.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdg.p4j.data.ElementID;

import java.util.Arrays;

/**
 * Represents a 2D grid of elements. A byte represents each element.
 * The byte represents the element ID. The ID is used to determine the element
 * type. The ID is also used to determine the element's color.
 */
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
    }

    public void update() {
        if (!Constants.IS_RUNNING) return;
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
                    case SODIUM -> updateSodium(x, y, index);
                    case GUNPOWDER -> updateGunpowder(x, y, index);
                    case TNT -> updateTNT(x, y, index);
                    case WOOD -> updateWood(x, y, index);
                    case WATER, OIL, GASOLINE, MERCURY -> updateFluid(x, y, index);
                    case ACID -> updateAcid(x, y, index);
                    case LAVA -> updateLava(x, y, index);
                    case FIRE -> updateFire(x, y, index);
                    case SMOKE_DARK, SMOKE_GRAY, SMOKE_LIGHT ->
                            updateSmoke(x, y, index);
                    default -> velocity[index] = 0;
                }
            }
        }
    }

    private boolean canDisplace(byte upperId, byte lowerId) {
        if (lowerId == ElementID.STONE.getId()) return false;
        return ElementID.fromId(upperId).getDensity() >
                ElementID.fromId(lowerId).getDensity();
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
        boolean canRight = (x < width - 1 && canDisplace(currentId,
                grid[belowRight]));

        if (canLeft && canRight) {
            int target = (Math.random() > Constants.RANDOM_THRESHOLD) ?
                    belowLeft : belowRight;
            swap(idx, target);
        } else if (canLeft) {
            swap(idx, belowLeft);
        } else if (canRight) {
            swap(idx, belowRight);
        }
    }

    private void updateTNT(int x, int y, int idx) {
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    ElementID neighbor = ElementID.fromId(grid[ny * width + nx]);
                    if (neighbor == ElementID.FIRE || neighbor == ElementID.LAVA || neighbor.isHot()) {
                        explodeTNT(x, y, Constants.TNT_EXPLOSION_RADIUS);
                        return;
                    }
                }
            }
        }

        updateSand(x, y, idx);
    }

    private void updateLava(int x, int y, int idx) {
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    ElementID neighbor = ElementID.fromId(grid[nIdx]);
                    if (neighbor == ElementID.WATER) {
                        grid[idx] = ElementID.STONE.getId();
                        grid[nIdx] = ElementID.SMOKE_LIGHT.getId();
                        velocity[idx] = 0;
                        velocity[nIdx] = 0;
                        updated[idx] = true;
                        updated[nIdx] = true;
                        return;
                    } else if (neighbor.isFlammable()) {
                        grid[nIdx] = ElementID.FIRE.getId();
                        updated[nIdx] = true;
                    } else if (neighbor == ElementID.ICE) {
                        grid[nIdx] = ElementID.WATER.getId();
                        updated[nIdx] = true;
                    }
                }
            }
        }

        if (Math.random() < 0.65) {
            return;
        }

        updateFluid(x, y, idx);
    }

    private void updateFluid(int x, int y, int idx) {
        ElementID currentElement = ElementID.fromId(grid[idx]);
        boolean isFlammableFluid = currentElement.isFlammable();

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    ElementID neighbor = ElementID.fromId(grid[nIdx]);

                    if (isFlammableFluid && neighbor.isHot()) {
                        if (currentElement == ElementID.GASOLINE) {
                            explode(x, y);
                        } else {
                            grid[idx] = ElementID.FIRE.getId();
                            updated[idx] = true;
                        }
                        return;
                    } else if (neighbor.isFlammable()) {
                        if (currentElement.isHot()) {
                            grid[nIdx] = ElementID.SMOKE_GRAY.getId();
                            updated[nIdx] = true;
                        }
                    }
                }
            }
        }

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
        boolean canBelowRight = (x < width - 1 && canDisplace(currentId,
                grid[belowRight]));

        if (canBelowLeft && canBelowRight) {
            int target = (Math.random() > Constants.RANDOM_THRESHOLD) ?
                    belowLeft : belowRight;
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

    private void updateSodium(int x, int y, int idx) {
        boolean hasTouchedWater = false;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    if (ElementID.fromId(grid[ny * width + nx]).isWater()) {
                        hasTouchedWater = true;
                        break;
                    }
                }
            }
            if (hasTouchedWater) break;
        }

        if (hasTouchedWater) {
            explode(x, y);
            return;
        }

        updateSand(x, y, idx);
    }

    private void updateAcid(int x, int y, int idx) {
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    ElementID neighbor = ElementID.fromId(grid[nIdx]);
                    boolean isCorrosible = neighbor != ElementID.EMPTY
                            && neighbor.isCorrosible();

                    if (isCorrosible) {
                        grid[nIdx] = ElementID.SMOKE_GRAY.getId();
                        velocity[nIdx] = 0;
                        updated[nIdx] = true;
                        grid[idx] = ElementID.EMPTY.getId();
                        velocity[idx] = 0;
                        return;
                    }
                }
            }
        }

        updateFluid(x, y, idx);
    }

    private void updateGunpowder(int x, int y, int idx) {
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    ElementID neighbor = ElementID.fromId(grid[nIdx]);
                    if (neighbor == ElementID.SAND) {
                        if (Math.random() < 0.1) {
                            grid[idx] = ElementID.TNT.getId();
                            grid[nIdx] = ElementID.EMPTY.getId();
                            velocity[idx] = 0;
                            updated[idx] = true;
                            updated[nIdx] = true;
                            return;
                        }
                    }

                    if (neighbor == ElementID.FIRE) {
                        grid[idx] = ElementID.FIRE.getId();
                        velocity[idx] = 0;
                        updated[idx] = true;
                        return;
                    }
                }
            }
        }

        updateSand(x, y, idx);
    }

    private void updateWood(int x, int y, int idx) {
        boolean isNearFire = false;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    if (ElementID.fromId(grid[ny * width + nx]) == ElementID.FIRE) {
                        isNearFire = true;
                        break;
                    }
                }
            }
            if (isNearFire) break;
        }

        if (isNearFire) {
            if (Math.random() < Constants.WOOD_BURN_CHANCE) {
                grid[idx] = ElementID.FIRE.getId();
                velocity[idx] = 0;
                updated[idx] = true;
            }
        }
    }

    private void explode(int centerX, int centerY) {
        for (int dy = -4; dy <= 4; dy++) {
            for (int dx = -4; dx <= 4; dx++) {
                if (dx * dx + dy * dy > 4 * 4) continue;

                int nx = centerX + dx;
                int ny = centerY + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    byte currentId = grid[nIdx];

                    if (currentId != ElementID.STONE.getId()) {
                        ElementID resultType;
                        if (Math.random() > 0.3) {
                            resultType = ElementID.FIRE;
                        } else {
                            resultType = ElementID.SMOKE_LIGHT;
                        }

                        grid[nIdx] = resultType.getId();
                        velocity[nIdx] = 0;
                        updated[nIdx] = true;
                    }
                }
            }
        }
    }

    private void explodeTNT(int centerX, int centerY, int radius) {
        int centerIdx = centerY * width + centerX;
        grid[centerIdx] = ElementID.EMPTY.getId();
        updated[centerIdx] = true;

        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                if (dx * dx + dy * dy > radius * radius) continue;

                int nx = centerX + dx;
                int ny = centerY + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    ElementID neighbor = ElementID.fromId(grid[nIdx]);

                    if (neighbor == ElementID.TNT && !updated[nIdx]) {
                        explodeTNT(nx, ny, radius);
                        continue;
                    }

                    if (Math.random() < 0.8) {
                        grid[nIdx] = (Math.random() > 0.4) ?
                                ElementID.FIRE.getId() : ElementID.SMOKE_DARK.getId();
                        velocity[nIdx] = -((float) Math.random() * 8f + 4f);
                        updated[nIdx] = true;
                    }
                }
            }
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
        boolean nearFuel = false;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    ElementID neighbor = ElementID.fromId(grid[nIdx]);
                    if (neighbor.isFlammable()) {
                        nearFuel = true;
                        if (neighbor == ElementID.OIL) {
                            grid[nIdx] = ElementID.FIRE.getId();
                            updated[nIdx] = true;
                        } else if (neighbor.isFlammable() && neighbor != ElementID.OIL &&
                                Math.random() < Constants.WOOD_IGNITION_CHANCE) {
                            grid[nIdx] = ElementID.FIRE.getId();
                            updated[nIdx] = true;
                        }
                    } else if (neighbor == ElementID.WATER &&
                            Math.random() < Constants.FIRE_EVAPORATION_CHANCE) {
                        double rand = Math.random();
                        ElementID smokeType = ElementID.SMOKE_LIGHT;
                        if (rand > 0.66) smokeType = ElementID.SMOKE_GRAY;
                        grid[nIdx] = smokeType.getId();
                        updated[nIdx] = true;
                        grid[idx] = ElementID.EMPTY.getId();
                        velocity[idx] = 0;
                        return;
                    } else if (neighbor == ElementID.ICE) {
                        grid[nIdx] = ElementID.WATER.getId();
                        updated[nIdx] = true;
                    }
                }
            }
        }

        if (Math.random() < Constants.FIRE_DISSIPATION_CHANCE) {
            grid[idx] = ElementID.EMPTY.getId();
            velocity[idx] = 0;
            return;
        }

        if (nearFuel && Math.random() < 0.7) {
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public byte[] getGrid() { return grid; }
}