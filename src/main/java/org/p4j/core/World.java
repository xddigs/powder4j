package org.p4j.core;

import org.p4j.data.ElementID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The entire simulated world and its rulesets. How it behaves, what it does,
 * and how it is updated.
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
                    case SAND, GRAVEL, CEMENT -> updateSand(x, y, index);
                    case HYDROGEN -> updateHydrogen(x, y, index);
                    case CARBON -> updateCarbon(x, y, index);
                    case WET_SAND, GRASS -> updateSolid(x, y, index);
                    case SODIUM, SALT, DIRT, SEED, SILICON -> updatePowder(x, y, index);
                    case THERMITE -> updateThermite(x, y, index);
                    case GUNPOWDER -> updateGunpowder(x, y, index);
                    case TNT -> updateTNT(x, y, index);
                    case WOOD -> updateWood(x, y, index);
                    case WATER, OIL, GASOLINE, MERCURY -> updateFluid(x, y, index);
                    case CHLORINE -> updateChlorine(x, y, index);
                    case ACID -> updateAcid(x, y, index);
                    case LAVA -> updateLava(x, y, index);
                    case FIRE -> updateFire(x, y, index);
                    case STEAM -> updateSteam(x, y, index);
                    case METHANE -> updateMethane(x, y, index);
                    case SMOKE_DARK, SMOKE_GRAY, SMOKE_LIGHT -> updateSmoke(x, y, index);
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
        ElementID currentType = ElementID.fromId(grid[idx]);

        if (currentType == ElementID.SAND) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    int nx = x + dx;
                    int ny = y + dy;
                    if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                        int nIdx = ny * width + nx;
                        if (ElementID.fromId(grid[nIdx]).isWater()) {
                            grid[idx] = ElementID.WET_SAND.getId();
                            grid[nIdx] = ElementID.EMPTY.getId();
                            updated[idx] = true;
                            updated[nIdx] = true;
                            return;
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
            int target = (Math.random() > Constants.RANDOM_THRESHOLD) ?
                    belowLeft : belowRight;
            swap(idx, target);
        } else if (canLeft) {
            swap(idx, belowLeft);
        } else if (canRight) {
            swap(idx, belowRight);
        }
    }

    private void updateCarbon(int x, int y, int idx) {
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (isInBounds(nx, ny)) {
                    int nIdx = getIndex(nx, ny);
                    ElementID neighbor = ElementID.fromId(grid[nIdx]);

                    if (neighbor == ElementID.FIRE) {
                        if (Math.random() < Constants.FIRE_IGNITION_CHANCE) {
                            grid[idx] = ElementID.FIRE.getId();
                            updated[idx] = true;
                            return;
                        }
                    }
                }
            }
        }

        updateSolid(x, y, idx);
    }

    private void updateSolid(int x, int y, int idx) {
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
            idx = targetIdx;
        }

        ElementID currentElement = ElementID.fromId(grid[idx]);
        if (currentElement == ElementID.GRASS) {
            int belowY = y + actualSteps + 1;

            if (belowY < height) {
                int belowIdx = belowY * width + x;
                ElementID belowElement = ElementID.fromId(grid[belowIdx]);
                if (belowElement == ElementID.GRASS) {
                    grid[idx] = ElementID.EMPTY.getId();
                    velocity[idx] = 0;
                    updated[idx] = true;
                    return;
                }
            }

            updateWood(x, y + actualSteps, idx);
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
                    if (neighbor == ElementID.FIRE ||
                            neighbor == ElementID.LAVA || neighbor.isHot()) {
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
                        grid[idx] = ElementID.OBSIDIAN.getId();
                        grid[nIdx] = ElementID.STEAM.getId();
                        velocity[idx] = 0;
                        velocity[nIdx] = 0;
                        updated[idx] = true;
                        updated[nIdx] = true;
                        return;

                    } else if (neighbor == ElementID.SAND ||
                            neighbor == ElementID.SILICON) {
                        grid[nIdx] = ElementID.GLASS.getId();
                        updated[nIdx] = true;

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

        if (Math.random() < Constants.LAVA_FLOW_SKIP_CHANCE) {
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

                    if (currentElement == ElementID.OIL && neighbor == ElementID.STEAM) {
                        if (Math.random() < Constants.GASOLINE_CREATION_CHANCE) {
                            grid[idx] = ElementID.GASOLINE.getId();
                            grid[nIdx] = ElementID.SMOKE_DARK.getId();
                            updated[idx] = true;
                            updated[nIdx] = true;
                            return;
                        }
                    }

                    if (currentElement.isWater() && (neighbor == ElementID.MUD
                            || neighbor == ElementID.DIRT)) {
                        if (soak(x, y, idx)) {
                            return;
                        }
                    }

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

        int belowLeft = (y + 1) * width + (x - 1);
        int belowRight = (y + 1) * width + (x + 1);

        boolean canBelowLeft = (x > 0 && canDisplace(currentId, grid[belowLeft]));
        boolean canBelowRight = (x < width - 1 && canDisplace(currentId, grid[belowRight]));

        if (canBelowLeft && canBelowRight) {
            int target = (Math.random() > Constants.RANDOM_THRESHOLD) ? belowLeft : belowRight;
            velocity[target] = currentVel * Constants.FLUID_DIAGONAL_VELOCITY_RETENTION;
            swap(idx, target);
            return;

        } else if (canBelowLeft) {
            velocity[belowLeft] = currentVel *
                    Constants.FLUID_DIAGONAL_VELOCITY_RETENTION;
            swap(idx, belowLeft);
            return;

        } else if (canBelowRight) {
            velocity[belowRight] = currentVel *
                    Constants.FLUID_DIAGONAL_VELOCITY_RETENTION;
            swap(idx, belowRight);
            return;
        }

        int dispersion = currentElement.getDispersionRate();
        if (currentVel > Constants.FLUID_MOMENTUM_THRESHOLD) {
            dispersion += (int) (currentVel *
                    Constants.FLUID_MOMENTUM_DISPERSION_MULTIPLIER);
        }

        if (y > 0) {
            ElementID aboveElement = ElementID.fromId(grid[(y - 1) * width + x]);
            if (aboveElement.isLiquid() || aboveElement == currentElement) {
                dispersion += Constants.FLUID_HYDROSTATIC_PRESSURE_BONUS;
            }
        }

        velocity[idx] = 0;
        boolean goRightFirst = Math.random() > Constants.RANDOM_THRESHOLD;
        int primaryDir = goRightFirst ? 1 : -1;

        if (!flow(x, y, idx, primaryDir, dispersion, currentId)) {
            flow(x, y, idx, -primaryDir, dispersion, currentId);
        }
    }

    private boolean soak(int waterX, int waterY, int waterIdx) {
        int maxDepth = Constants.MUD_MAX_DEPTH;
        int[] xOffsets = {0, -1, 1};

        for (int dx : xOffsets) {
            int checkX = waterX + dx;
            if (checkX < 0 || checkX >= width) continue;

            for (int dy = 1; dy <= maxDepth; dy++) {
                int checkY = waterY + dy;
                if (checkY >= height) break;

                int targetIdx = checkY * width + checkX;
                ElementID element = ElementID.fromId(grid[targetIdx]);

                if (element == ElementID.DIRT) {
                    grid[waterIdx] = ElementID.EMPTY.getId();
                    velocity[waterIdx] = 0;
                    updated[waterIdx] = true;

                    grid[targetIdx] = ElementID.MUD.getId();
                    updated[targetIdx] = true;
                    return true;
                }

                if (element != ElementID.MUD) {
                    break;
                }
            }
        }

        return false;
    }

    private boolean flow(int x, int y, int idx, int dir, int maxDistance, byte currentId) {
        int bestX = x;
        for (int i = 1; i <= maxDistance; i++) {
            int nextX = x + (dir * i);
            if (nextX < 0 || nextX >= width) break;

            int targetIdx = y * width + nextX;
            byte targetId = grid[targetIdx];

            if (canDisplace(currentId, targetId)) {
                bestX = nextX;
                if (targetId == ElementID.EMPTY.getId()) {
                    break;
                }
            } else {
                break;
            }
        }

        if (bestX != x) {
            swap(idx, y * width + bestX);
            return true;
        }
        return false;
    }

    private void updatePowder(int x, int y, int idx) {
        ElementID currentType = ElementID.fromId(grid[idx]);
        boolean hasTouchedWater = false;

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    ElementID neighbor = ElementID.fromId(grid[nIdx]);

                    if (currentType == ElementID.SILICON && (
                            neighbor == ElementID.GRAVEL ||
                                    neighbor == ElementID.STONE)) {
                        if (Math.random() < Constants.THERMITE_CREATION_CHANCE) {
                            grid[idx] = ElementID.THERMITE.getId();
                            grid[nIdx] = ElementID.EMPTY.getId();
                            updated[idx] = true;
                            updated[nIdx] = true;
                            return;
                        }
                    }

                    if (currentType == ElementID.SILICON) {
                        if (neighbor == ElementID.FIRE ||
                                neighbor == ElementID.LAVA) {
                            grid[idx] = ElementID.GLASS.getId();
                            updated[idx] = true;
                            return;
                        } else if (neighbor == ElementID.SALT) {
                            grid[idx] = ElementID.SODIUM.getId();
                            grid[nIdx] = ElementID.SAND.getId();
                            updated[idx] = true;
                            updated[nIdx] = true;
                            return;
                        }
                    }

                    if (currentType == ElementID.DIRT && neighbor.isWater()) {
                        if (ThreadLocalRandom.current().nextFloat() <
                                Constants.MUD_SPREAD_CHANCE) {
                            grid[idx] = ElementID.MUD.getId();
                            grid[nIdx] = ElementID.MUD.getId();
                            updated[idx] = true;
                            updated[nIdx] = true;
                            return;
                        }
                    }

                    if (currentType == ElementID.SALT && neighbor == ElementID.DIRT) {
                        if (Math.random() < Constants.GUNPOWDER_CREATION_CHANCE) {
                            grid[idx] = ElementID.GUNPOWDER.getId();
                            grid[nIdx] = ElementID.EMPTY.getId();
                            updated[idx] = true;
                            updated[nIdx] = true;
                            return;
                        }
                    }

                    if (currentType == ElementID.SEED) {
                        boolean isOnFertileGround = (neighbor == ElementID.DIRT ||
                                neighbor == ElementID.MUD || neighbor.isWater());
                        if (isOnFertileGround) {
                            float rand = ThreadLocalRandom.current().nextFloat();
                            if (rand < Constants.GROW_TREE_CHANCE) {
                                growTree(x, y);
                                return;
                            } else if (rand < Constants.GROW_GRASS_CHANCE) {
                                growGrass(x, y);
                                return;
                            }
                        }
                    }

                    if (neighbor == ElementID.ACID) {
                        grid[idx] = ElementID.CHLORINE.getId();
                        grid[nIdx] = ElementID.SAND.getId();
                        explode(x, y);
                        return;
                    }

                    if (currentType == ElementID.SALT && (neighbor == ElementID.FIRE ||
                            neighbor == ElementID.LAVA)) {
                        grid[idx] = ElementID.SODIUM.getId();
                        grid[nIdx] = ElementID.CHLORINE.getId();
                        updated[idx] = true;
                        updated[nIdx] = true;
                        return;
                    }

                    if (currentType == ElementID.SALT && neighbor.isWater()) {
                        grid[idx] = ElementID.SMOKE_GRAY.getId();
                        grid[nIdx] = ElementID.SMOKE_LIGHT.getId();
                        return;
                    }

                    if (currentType == ElementID.SODIUM && neighbor.isWater()) {
                        grid[idx] = ElementID.FIRE.getId();
                        grid[nIdx] = ElementID.SMOKE_DARK.getId();
                        updated[idx] = true;
                        updated[nIdx] = true;
                        explode(x, y);
                        return;
                    }
                }
            }
        }

        updateSand(x, y, idx);
    }

    private void growGrass(int startX, int startY) {
        int idx = startY * width + startX;
        grid[idx] = ElementID.GRASS.getId();
        updated[idx] = true;

        int patchSize = ThreadLocalRandom.current().nextInt(1, 3);
        for (int i = 0; i < patchSize; i++) {
            int dx = ThreadLocalRandom.current().nextInt(-1, 2);
            int dy = ThreadLocalRandom.current().nextInt(-1, 1);
            int nx = startX + dx;
            int ny = startY + dy;

            if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                int nIdx = ny * width + nx;
                if (grid[nIdx] == ElementID.EMPTY.getId()) {
                    grid[nIdx] = ElementID.GRASS.getId();
                    updated[nIdx] = true;
                }
            }
        }
    }

    private void growTree(int startX, int startY) {
        int waterAbsorbed = absorb(startX, startY,
                Constants.TREE_WATER_ABSORB_RADIUS,
                Constants.TREE_WATER_ABSORB_MAX);
        int baseHeight = ThreadLocalRandom.current().nextInt(
                Constants.TREE_BASE_HEIGHT_MIN,
                Constants.TREE_BASE_HEIGHT_MAX);

        int treeHeight = baseHeight + (
                waterAbsorbed * Constants.TREE_HEIGHT_PER_WATER);

        int trunkWidth = Constants.TREE_TRUNK_BASE_WIDTH + (
                waterAbsorbed / Constants.TREE_WATER_DIVISOR_TRUNK_WIDTH);

        int maxLeafRadius = Constants.TREE_LEAF_BASE_RADIUS + (
                waterAbsorbed / Constants.TREE_WATER_DIVISOR_LEAF_RADIUS);

        int currentY = startY;
        int currentX = startX;

        for (int i = 0; i < treeHeight; i++) {
            if (currentY < 0) break;

            int halfWidth = trunkWidth / 2;
            for (int wx = -halfWidth; wx <= halfWidth; wx++) {
                int tx = currentX + wx;
                if (tx >= 0 && tx < width) {
                    int idx = currentY * width + tx;
                    ElementID current = ElementID.fromId(grid[idx]);
                    if (current == ElementID.EMPTY || current == ElementID.SEED ||
                            current.isLiquid() || current == ElementID.SMOKE_LIGHT ||
                            current == ElementID.GRASS) {
                        grid[idx] = ElementID.WOOD.getId();
                        updated[idx] = true;
                    }
                }
            }

            int canopyStartHeight = treeHeight - (Constants.TREE_LEAF_CANOPY_OFFSET_BASE +
                    waterAbsorbed / Constants.TREE_WATER_DIVISOR_CANOPY_OFFSET);
            if (i >= canopyStartHeight) {
                int currentLeafRadius = Math.min(maxLeafRadius, (treeHeight - i) +
                        Constants.TREE_LEAF_RADIUS_HEIGHT_OFFSET);

                for (int ly = -currentLeafRadius; ly <= currentLeafRadius; ly++) {
                    for (int lx = -currentLeafRadius; lx <= currentLeafRadius; lx++) {
                        if (lx * lx + ly * ly <= currentLeafRadius * currentLeafRadius +
                                Constants.TREE_LEAF_CIRCLE_TOLERANCE) {
                            int leafX = currentX + lx;
                            int leafY = currentY + ly;

                            if (leafX >= 0 && leafX < width && leafY >= 0 && leafY < height) {
                                int leafIdx = leafY * width + leafX;
                                if (grid[leafIdx] == ElementID.EMPTY.getId()) {
                                    grid[leafIdx] = ElementID.GRASS.getId();
                                    updated[leafIdx] = true;
                                }
                            }
                        }
                    }
                }
            }

            float curveChance = trunkWidth > Constants.TREE_TRUNK_BASE_WIDTH ?
                    Constants.TREE_CURVE_CHANCE_THICK : Constants.TREE_CURVE_CHANCE_THIN;

            if (i > Constants.TREE_CURVE_MIN_HEIGHT_STEP &&
                    ThreadLocalRandom.current().nextFloat() < curveChance) {
                currentX += ThreadLocalRandom.current().nextBoolean() ? 1 : -1;
                currentX = Math.clamp(currentX, Constants.TREE_MIN_X_MARGIN,
                        width - Constants.TREE_MAX_X_MARGIN_OFFSET);
            }

            currentY--;
        }
    }

    private int absorb(int startX, int startY, int radius, int maxWater) {
        int count = 0;
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                if (count >= maxWater) return count;

                int nx = startX + dx;
                int ny = startY + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    ElementID element = ElementID.fromId(grid[nIdx]);

                    if (isInBounds(nx, ny) && element.isWater()) {
                        grid[nIdx] = ElementID.EMPTY.getId();
                        updated[nIdx] = true;
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private void updateSteam(int x, int y, int idx) {
        if (y < 5 && Math.random() < Constants.ICE_CREATION_CHANCE) {
            grid[idx] = ElementID.ICE.getId();
            velocity[idx] = 0;
            updated[idx] = true;
            return;
        }

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    ElementID neighbor = ElementID.fromId(grid[nIdx]);

                    if (neighbor == ElementID.ICE) {
                        grid[nIdx] = ElementID.WATER.getId();
                        grid[idx] = ElementID.WATER.getId();
                        updated[nIdx] = true;
                        updated[idx] = true;
                        return;
                    }
                }
            }
        }

        if (Math.random() < Constants.STEAM_CONDENSE_CHANCE) {
            grid[idx] = ElementID.WATER.getId();
            velocity[idx] = 0;
            updated[idx] = true;
            return;
        }

        updateSmoke(x, y, idx);
    }

    private void updateMethane(int x, int y, int idx) {
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    ElementID neighbor = ElementID.fromId(grid[ny * width + nx]);
                    if (neighbor == ElementID.FIRE || neighbor == ElementID.LAVA ||
                            neighbor.isHot()) {
                        grid[idx] = ElementID.FIRE.getId();
                        explode(x, y);
                        return;
                    }
                }
            }
        }

        updateSmoke(x, y, idx);
    }

    private void updateCement(int x, int y, int idx) {
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    if (ElementID.fromId(grid[nIdx]).isWater()) {
                        grid[idx] = ElementID.STONE.getId();
                        grid[nIdx] = ElementID.EMPTY.getId();
                        updated[idx] = true;
                        updated[nIdx] = true;
                        return;
                    }
                }
            }
        }

        updateSand(x, y, idx);
    }

    private void updateChlorine(int x, int y, int idx) {
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    ElementID neighbor = ElementID.fromId(grid[nIdx]);

                    if (neighbor == ElementID.WATER) {
                        grid[idx] = ElementID.ACID.getId();
                        updated[idx] = true;
                        return;
                    }
                    if (neighbor == ElementID.WOOD) {
                        grid[nIdx] = ElementID.SMOKE_DARK.getId();
                        grid[idx] = ElementID.EMPTY.getId();
                        updated[nIdx] = true;
                        updated[idx] = true;
                        return;
                    }
                }
            }
        }

        updateSmoke(x, y, idx);
    }

    private void updateThermite(int x, int y, int idx) {
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    ElementID neighbor = ElementID.fromId(grid[nIdx]);

                    if (neighbor == ElementID.FIRE ||
                            neighbor == ElementID.LAVA || neighbor.isHot()) {
                        meltStone(x, y);
                        grid[idx] = ElementID.FIRE.getId();
                        updated[idx] = true;
                        return;
                    }
                }
            }
        }

        updateSand(x, y, idx);
    }

    private void meltStone(int centerX, int centerY) {
        int radius = 2;
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                int nx = centerX + dx;
                int ny = centerY + dy;
                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    if (grid[nIdx] == ElementID.STONE.getId()) {
                        grid[nIdx] = ElementID.LAVA.getId();
                        updated[nIdx] = true;
                    }
                }
            }
        }
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
                    boolean isCorrosible = neighbor != ElementID.EMPTY && neighbor.isCorrosible();

                    if (neighbor == ElementID.SODIUM) {
                        grid[idx] = ElementID.SAND.getId();
                        grid[nIdx] = ElementID.CHLORINE.getId();
                        explodeChlorine(x, y);
                        return;
                    }

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
                        if (Math.random() < Constants.TNT_CRAFTING_CHANCE) {
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
        int fireX = x;
        int fireY = y;

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;

                int nx = x + dx;
                int ny = y + dy;

                if (isInBounds(nx, ny)) {
                    ElementID neighbor = ElementID.fromId(grid[ny * width + nx]);
                    if (neighbor == ElementID.FIRE) {
                        isNearFire = true;
                        break;
                    }
                }
            }
            if (isNearFire) break;
        }

        if (isNearFire) {
            if (Math.random() < Constants.WOOD_IGNITION_CHANCE) {
                grid[idx] = ElementID.CARBON.getId();
                updated[idx] = true;

                int smokeY = y - 1;
                if (isInBounds(x, smokeY)) {
                    int smokeIdx = smokeY * width + x;
                    if (grid[smokeIdx] == ElementID.EMPTY.getId()) {
                        grid[smokeIdx] = ElementID.SMOKE_DARK.getId();
                        updated[smokeIdx] = true;
                    }
                }
                return;
            }
        }

        if (Math.random() < Constants.WOOD_ABSORPTION_CHANCE) {
            absorb(x, y, 1, 1);
        }
    }

    private void explode(int centerX, int centerY) {
        int radius = Constants.GENERAL_EXPLOSION_RADIUS;
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                if (dx * dx + dy * dy > radius * radius) continue;

                int nx = centerX + dx;
                int ny = centerY + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    byte currentId = grid[nIdx];

                    if (currentId != ElementID.STONE.getId()) {
                        ElementID resultType;
                        if (Math.random() > Constants.WOOD_IGNITION_CHANCE) {
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

                    if (Math.random() < Constants.TNT_SPAWN_DEBRIS_CHANCE) {
                        grid[nIdx] = (Math.random() > Constants.TNT_FIRE_SPAWN_THRESHOLD) ?
                                ElementID.FIRE.getId() : ElementID.SMOKE_DARK.getId();
                        velocity[nIdx] = -((float) Math.random() * Constants.TNT_DEBRIS_MAX_VELOCITY
                                + Constants.TNT_DEBRIS_MIN_VELOCITY);
                        updated[nIdx] = true;
                    }
                }
            }
        }
    }

    private void explodeChlorine(int centerX, int centerY) {
        int radius = Constants.CHLORINE_EXPLOSION_RADIUS;
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                if (dx * dx + dy * dy > radius * radius) continue;

                int nx = centerX + dx;
                int ny = centerY + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    byte currentId = grid[nIdx];

                    if (currentId == ElementID.EMPTY.getId() ||
                            currentId == ElementID.ACID.getId() ||
                            ElementID.fromId(currentId).isWater()) {

                        grid[nIdx] = ElementID.CHLORINE.getId();
                        velocity[nIdx] = -((float) Math.random()
                                * Constants.CHLORINE_DEBRIS_MAX_VELOCITY
                                + Constants.CHLORINE_DEBRIS_MIN_VELOCITY);
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

                    if (neighbor == ElementID.STONE) {
                        if (Math.random() < Constants.GRAVEL_CREATION_CHANCE) {
                            grid[nIdx] = ElementID.GRAVEL.getId();
                            updated[nIdx] = true;
                        } else if (Math.random() < Constants.LAVA_CREATION_CHANCE) {
                            grid[nIdx] = ElementID.LAVA.getId();
                            updated[nIdx] = true;
                        }
                    }

                    if (neighbor == ElementID.DIRT && Math.random() <
                            Constants.CEMENT_CREATION_CHANCE) {
                        grid[nIdx] = ElementID.CEMENT.getId();
                        updated[nIdx] = true;
                    }

                    if (neighbor == ElementID.MUD && Math.random() <
                            Constants.METHANE_CREATION_CHANCE) {
                        grid[nIdx] = ElementID.METHANE.getId();
                        grid[idx] = ElementID.DIRT.getId();
                        updated[nIdx] = true;
                        updated[idx] = true;
                        return;
                    }

                    if (neighbor == ElementID.OIL && Math.random() <
                            Constants.MERCURY_CREATION_CHANCE) {
                        grid[nIdx] = ElementID.MERCURY.getId();
                        updated[nIdx] = true;
                    }

                    if (neighbor == ElementID.SILICON && Math.random() <
                            Constants.GUNPOWDER_CREATION_CHANCE) {
                        grid[nIdx] = ElementID.GUNPOWDER.getId();
                        updated[nIdx] = true;
                    }

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
                        if (rand > Constants.FIRE_SMOKE_GRAY_THRESHOLD) {
                            smokeType = ElementID.SMOKE_GRAY;
                        }

                        if (Math.random() < Constants.SALT_CHANCE) {
                            grid[nIdx] = ElementID.SALT.getId();
                        } else {
                            grid[nIdx] = smokeType.getId();
                        }
                        updated[nIdx] = true;
                        grid[idx] = ElementID.EMPTY.getId();
                        velocity[idx] = 0;
                        return;

                    } else if (neighbor == ElementID.ICE) {
                        grid[nIdx] = ElementID.WATER.getId();
                        updated[nIdx] = true;

                    } else if (neighbor == ElementID.SAND && Math.random() <
                            Constants.GLASS_FUSION_CHANCE) {
                        grid[nIdx] = ElementID.GLASS.getId();
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

        if (nearFuel && Math.random() < Constants.FIRE_NEAR_FUEL_PAUSE_CHANCE) {
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

    private void updateHydrogen(int x, int y, int idx) {
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (isInBounds(nx, ny)) {
                    ElementID neighbor = ElementID.fromId(grid[getIndex(nx, ny)]);
                    if (neighbor == ElementID.FIRE || neighbor == ElementID.LAVA
                            || neighbor.isHot()) {
                        explode(x, y);
                        grid[idx] = ElementID.STEAM.getId();
                        updated[idx] = true;
                        return;
                    }
                }
            }
        }

        if (Math.random() < Constants.HYDROGEN_DISSIPATION_CHANCE) {
            grid[idx] = ElementID.EMPTY.getId();
            velocity[idx] = 0;
            return;
        }

        if (y <= 0) return;

        int aboveIdx = getIndex(x, y - 1);
        ElementID aboveElement = ElementID.fromId(grid[aboveIdx]);

        boolean canRiseAbove = aboveElement == ElementID.EMPTY ||
                (aboveElement.getDensity() > ElementID.HYDROGEN.getDensity()
                        && aboveElement.getDensity() < 0);

        if (canRiseAbove) {
            swap(idx, aboveIdx);
            return;
        }

        int aboveLeft = (y - 1) * width + (x - 1);
        int aboveRight = (y - 1) * width + (x + 1);

        boolean canLeft = (x > 0 && isGasReplaceable(grid[aboveLeft]));
        boolean canRight = (x < width - 1 && isGasReplaceable(grid[aboveRight]));

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

    private boolean isGasReplaceable(byte targetId) {
        ElementID target = ElementID.fromId(targetId);
        return target == ElementID.EMPTY || (target.getDensity() >
                ElementID.HYDROGEN.getDensity() && target.getDensity() < 0);
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

    public void applyInertia(float forceX, float forceY) {
        int stepsX = Math.round(forceX * Constants.INERTIA_SENSITIVITY);
        int stepsY = Math.round(forceY * Constants.INERTIA_SENSITIVITY);

        if (stepsX == 0 && stepsY == 0) return;

        stepsX = Math.clamp(stepsX, -Constants.INERTIA_MAX_STEP_LIMIT,
                Constants.INERTIA_MAX_STEP_LIMIT);

        stepsY = Math.clamp(stepsY, -Constants.INERTIA_MAX_STEP_LIMIT,
                Constants.INERTIA_MAX_STEP_LIMIT);

        if (stepsY != 0) {
            boolean moveUp = stepsY < 0;
            int absStepsY = Math.abs(stepsY);

            int startY = moveUp ? 0 : height - 1;
            int endY = moveUp ? height : -1;
            int dirY = moveUp ? 1 : -1;

            for (int y = startY; y != endY; y += dirY) {
                for (int x = 0; x < width; x++) {
                    int idx = y * width + x;
                    byte typeId = grid[idx];

                    if (typeId == ElementID.EMPTY.getId() ||
                            typeId == ElementID.STONE.getId())
                        continue;

                    int currentY = y;
                    for (int s = 0; s < absStepsY; s++) {
                        int targetY = currentY + (moveUp ? -1 : 1);
                        if (targetY < 0 || targetY >= height) break;

                        int targetIdx = targetY * width + x;
                        if (grid[targetIdx] == ElementID.EMPTY.getId() ||
                                canDisplace(grid[idx], grid[targetIdx])) {
                            swap(currentY * width + x, targetIdx);
                            currentY = targetY;
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        if (stepsX != 0) {
            boolean moveLeft = stepsX < 0;
            int absStepsX = Math.abs(stepsX);

            int startX = moveLeft ? 0 : width - 1;
            int endX = moveLeft ? width : -1;
            int dirX = moveLeft ? 1 : -1;

            for (int y = 0; y < height; y++) {
                for (int x = startX; x != endX; x += dirX) {
                    int idx = y * width + x;
                    byte typeId = grid[idx];

                    if (typeId == ElementID.EMPTY.getId() || typeId ==
                            ElementID.STONE.getId())
                        continue;

                    int currentX = x;
                    for (int s = 0; s < absStepsX; s++) {
                        int targetX = currentX + (moveLeft ? -1 : 1);
                        if (targetX < 0 || targetX >= width) break;

                        int targetIdx = y * width + targetX;
                        if (grid[targetIdx] == ElementID.EMPTY.getId() ||
                                canDisplace(grid[idx], grid[targetIdx])) {
                            swap(y * width + currentX, targetIdx);
                            currentX = targetX;
                        } else {
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private int getIndex(int x, int y) {
        return y * width + x;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public byte[] getGrid() {
        return grid;
    }
}