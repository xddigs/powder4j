package org.p4j.core;

import org.p4j.data.ElementID;
import org.p4j.render.HeatMap;
import org.p4j.sys.CardsEngine;
import org.p4j.sys.MovementEngine;
import org.p4j.sys.ReactionEngine;
import org.p4j.sys.ThermoEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The entire simulated world and its rulesets. Holds the state of the simulation
 * grid and delegates element processing to the ReactionEngine.
 */
public class World {
    private static final Logger log = LoggerFactory.getLogger(World.class);
    private final ReactionEngine reaction;
    private final MovementEngine movement;
    private final CardsEngine cards;
    private final HeatMap heatmap;
    private final ThermoEngine thermo;
    private final int width;
    private final int height;
    private final byte[] grid;
    private final short[] atoms;
    private final float[] velocity;
    private final boolean[] updated;
    private float[] temperature;
    private float[] nextTemperature;

    public World(int width, int height) {
        log.debug("Constructing simulation world: {}x{}", width, height);
        this.reaction = new ReactionEngine(this);
        this.movement = new MovementEngine(this);
        this.cards = new CardsEngine(this);
        this.heatmap = new HeatMap(this);
        this.thermo = new ThermoEngine();
        this.width = width;
        this.height = height;
        this.grid = new byte[width * height];
        this.atoms = new short[width * height];
        this.temperature = new float[width * height];
        this.nextTemperature = new float[width * height];
        this.updated = new boolean[width * height];
        this.velocity = new float[width * height];
        Arrays.fill(temperature, K.DEFAULT_AMBIENT_TEMP);
        Arrays.fill(nextTemperature, K.DEFAULT_AMBIENT_TEMP);
        Arrays.fill(this.atoms, (short) -1);
    }

    public void update() {
        Arrays.fill(updated, false);
        if (!K.IS_RUNNING) return;

        thermo.update(grid, temperature, nextTemperature, width, height,
                (idx, newElement, newTemp) -> {
                    grid[idx] = newElement.getId();
                    temperature[idx] = newTemp;
                    nextTemperature[idx] = newTemp;
                    updated[idx] = true;
                });

        float[] tempSwap = temperature;
        temperature = nextTemperature;
        nextTemperature = tempSwap;

        for (int y = height - 1; y >= 0; y--) {
            boolean leftToRight = ThreadLocalRandom.current().nextBoolean();

            for (int i = 0; i < width; i++) {
                int x = leftToRight ? i : (width - 1 - i);
                int idx = getIndex(x, y);
                byte id = grid[idx];

                if (updated[idx]) continue;
                if (id == 0) continue;

                boolean hasReacted = reaction.process(x, y, idx);
                if (hasReacted) continue;

                byte currentId = grid[idx];
                if (currentId == 0) continue;
                ElementID currentType = ElementID.fromId(currentId);
                movement.update(x, y, idx, currentType);
            }
        }
    }

    public void swap(int i, int j) {
        byte tempGrid = grid[i];
        grid[i] = grid[j];
        grid[j] = tempGrid;

        short tempAtom = atoms[i];
        atoms[i] = atoms[j];
        atoms[j] = tempAtom;

        float tempVel = velocity[i];
        velocity[i] = velocity[j];
        velocity[j] = tempVel;

        float tempT = temperature[i];
        temperature[i] = temperature[j];
        temperature[j] = tempT;

        float tempNextT = nextTemperature[i];
        nextTemperature[i] = nextTemperature[j];
        nextTemperature[j] = tempNextT;

        updated[i] = true;
        updated[j] = true;
    }

    public boolean flow(int x, int y, int idx, int dir, int maxDistance, byte currentId) {
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

    public boolean soak(int waterX, int waterY, int waterIdx) {
        int maxDepth = K.MUD_MAX_DEPTH;
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

    public void growGrass(int startX, int startY) {
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

    public void growTree(int startX, int startY) {
        int waterAbsorbed = absorb(startX, startY,
                K.TREE_WATER_ABSORB_RADIUS,
                K.TREE_WATER_ABSORB_MAX);
        int baseHeight = ThreadLocalRandom.current().nextInt(
                K.TREE_BASE_HEIGHT_MIN,
                K.TREE_BASE_HEIGHT_MAX);

        int treeHeight = baseHeight + (
                waterAbsorbed * K.TREE_HEIGHT_PER_WATER);

        int trunkWidth = K.TREE_TRUNK_BASE_WIDTH + (
                waterAbsorbed / K.TREE_WATER_DIVISOR_TRUNK_WIDTH);

        int maxLeafRadius = K.TREE_LEAF_BASE_RADIUS + (
                waterAbsorbed / K.TREE_WATER_DIVISOR_LEAF_RADIUS);

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
                            current.isLiquid() ||
                            current == ElementID.GRASS) {
                        grid[idx] = ElementID.WOOD.getId();
                        updated[idx] = true;
                    }
                }
            }

            int canopyStartHeight = treeHeight - (K.TREE_LEAF_CANOPY_OFFSET_BASE +
                    waterAbsorbed / K.TREE_WATER_DIVISOR_CANOPY_OFFSET);
            if (i >= canopyStartHeight) {
                int currentLeafRadius = Math.min(maxLeafRadius, (treeHeight - i) +
                        K.TREE_LEAF_RADIUS_HEIGHT_OFFSET);

                for (int ly = -currentLeafRadius; ly <= currentLeafRadius; ly++) {
                    for (int lx = -currentLeafRadius; lx <= currentLeafRadius; lx++) {
                        if (lx * lx + ly * ly <= currentLeafRadius * currentLeafRadius +
                                K.TREE_LEAF_CIRCLE_TOLERANCE) {
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

            float curveChance = trunkWidth > K.TREE_TRUNK_BASE_WIDTH ?
                    K.TREE_CURVE_CHANCE_THICK : K.TREE_CURVE_CHANCE_THIN;

            if (i > K.TREE_CURVE_MIN_HEIGHT_STEP &&
                    ThreadLocalRandom.current().nextFloat() < curveChance) {
                currentX += ThreadLocalRandom.current().nextBoolean() ? 1 : -1;
                currentX = Math.clamp(currentX, K.TREE_MIN_X_MARGIN,
                        width - K.TREE_MAX_X_MARGIN_OFFSET);
            }

            currentY--;
        }
    }

    public int absorb(int startX, int startY, int radius, int maxWater) {
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

    public void setCell(int x, int y, ElementID type) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            int index = y * width + x;
            grid[index] = type.getId();
            atoms[index] = -1;
            velocity[index] = 0;
            temperature[index] = type.getDefaultTemp();
            nextTemperature[index] = type.getDefaultTemp();
        }
    }

    public boolean canDisplace(byte upperId, byte lowerId) {
        if (lowerId == ElementID.STONE.getId()) return false;
        return ElementID.fromId(upperId).getDensity() >
                ElementID.fromId(lowerId).getDensity();
    }

    public void applyInertia(float forceX, float forceY) {
        int stepsX = Math.round(forceX * K.INERTIA_SENSITIVITY);
        int stepsY = Math.round(forceY * K.INERTIA_SENSITIVITY);

        if (stepsX == 0 && stepsY == 0) return;

        stepsX = Math.clamp(stepsX, -K.INERTIA_MAX_STEP_LIMIT,
                K.INERTIA_MAX_STEP_LIMIT);

        stepsY = Math.clamp(stepsY, -K.INERTIA_MAX_STEP_LIMIT,
                K.INERTIA_MAX_STEP_LIMIT);

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
                        typeId == ElementID.STONE.getId() ||
                        typeId == ElementID.IRON.getId()) {
                        continue;
                    }

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

                    if (typeId == ElementID.EMPTY.getId() ||
                        typeId == ElementID.STONE.getId() ||
                        typeId == ElementID.IRON.getId()) {
                        continue;
                    }

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

    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public int getIndex(int x, int y) {
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

    public short[] getAtoms() {
        return atoms;
    }


    public ElementID getElementAt(int x, int y) {
        return ElementID.fromId(grid[getIndex(x, y)]);
    }

    public float getTemperatureAt(int x, int y) {
        return temperature[getIndex(x, y)];
    }

    public boolean[] getUpdated() {
        return updated;
    }

    public float[] getVelocity() {
        return velocity;
    }

    public float getVelocity(int idx) {
        return velocity[idx];
    }

    public void setVelocity(int idx, float vel) {
        this.velocity[idx] = vel;
    }

    public float getTemperature(int idx) {
        return temperature[idx];
    }

    public void setTemperature(int idx, float temp) {
        this.temperature[idx] = temp;
    }

    public void addTemperature(int idx, float amount) {
        this.temperature[idx] += amount;
    }

    public ReactionEngine getReaction() {
        return reaction;
    }

    public MovementEngine getMovement() {
        return movement;
    }

    public CardsEngine getCards() {
        return cards;
    }

    public ThermoEngine getThermo() {
        return thermo;
    }

    public HeatMap getHeatMap() {
        return heatmap;
    }
}