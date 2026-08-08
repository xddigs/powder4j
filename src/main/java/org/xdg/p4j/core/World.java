package org.xdg.p4j.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdg.p4j.data.ElementID;

import java.util.*;

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
    private final List<Body> bodies;

    public World(int width, int height) {
        log.debug("Constructing simulation world: {}x{}", width, height);
        this.width = width;
        this.height = height;
        this.grid = new byte[width * height];
        this.updated = new boolean[width * height];
        this.velocity = new float[width * height];
        this.bodies = new ArrayList<>();
    }

    public void update() {
        if (!Constants.IS_RUNNING) return;

        float dt = (float) (1.0f / Constants.TICKS_PER_SECOND);
        for (int i = bodies.size() - 1; i >= 0; i--) {
            Body body = bodies.get(i);
            PhysicsEngine.updateBody(body, this, dt);

            if (body.isSettled) {
                updateObject(body);
                bodies.remove(i);
            }
        }

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
                    case WET_SAND -> updateSolid(x, y, index);
                    case STONE, GLASS, ICE -> updateRigidSolid(x, y, index);
                    case SODIUM, SALT -> updatePowder(x, y, index);
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

    public void updateObject(Body body) {
        float cos = (float) Math.cos(body.angle);
        float sin = (float) Math.sin(body.angle);
        int centerX = body.maskWidth / 2;
        int centerY = body.maskHeight / 2;

        for (int ly = 0; ly < body.maskHeight; ly++) {
            for (int lx = 0; lx < body.maskWidth; lx++) {
                byte localPixel = body.pixels[ly * body.maskWidth + lx];
                if (localPixel == ElementID.EMPTY.getId()) continue;

                int relX = lx - centerX;
                int relY = ly - centerY;

                int wx = Math.round(body.x + (relX * cos - relY * sin));
                int wy = Math.round(body.y + (relX * sin + relY * cos));

                if (wx >= 0 && wx < getWidth() && wy >= 0 && wy < getHeight()) {
                    setCell(wx, wy, ElementID.fromId(localPixel));
                }
            }
        }
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
                        grid[idx] = ElementID.OBSIDIAN.getId();
                        grid[nIdx] = ElementID.STEAM.getId();
                        velocity[idx] = 0;
                        velocity[nIdx] = 0;
                        updated[idx] = true;
                        updated[nIdx] = true;
                        return;

                    } else if (neighbor == ElementID.SAND) {
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
            dispersion += (int) (currentVel * Constants.FLUID_MOMENTUM_DISPERSION_MULTIPLIER);
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

    private boolean flow(int x, int y, int idx, int dir,
                         int maxDistance, byte currentId) {
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
        boolean hasTouchedWater = false;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    ElementID neighbor = ElementID.fromId(grid[nIdx]);

                    if (neighbor == ElementID.ACID) {
                        grid[idx] = ElementID.CHLORINE.getId();
                        grid[nIdx] = ElementID.SAND.getId();
                        explode(x, y);
                        return;
                    }

                    if (neighbor.isWater() && grid[idx] == ElementID.SALT.getId()) {
                        grid[idx] = ElementID.SMOKE_GRAY.getId();
                        grid[nIdx] = ElementID.SMOKE_LIGHT.getId();
                        return;
                    }

                    if (neighbor.isWater()) {
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

    private void updateSteam(int x, int y, int idx) {
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
                    boolean isCorrosible = neighbor != ElementID.EMPTY
                            && neighbor.isCorrosible();

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

                        grid[nIdx] = smokeType.getId();
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


    private void updateRigidSolid(int x, int y, int idx) {
        if (y >= height - 1) return;
        int belowIdx = (y + 1) * width + x;
        byte belowId = grid[belowIdx];

        if (belowId == ElementID.EMPTY.getId() || canDisplace(grid[idx], belowId)) {
            detachAsBody(x, y);
        }
    }


    public void detachAsBody(int startX, int startY) {
        byte initialType = grid[startY * width + startX];
        if (initialType == ElementID.EMPTY.getId()) return;

        List<Integer> connectedIndices = new ArrayList<>();
        boolean[] visited = new boolean[width * height];
        Queue<Integer> queue = new LinkedList<>();

        int startIdx = startY * width + startX;
        queue.add(startIdx);
        visited[startIdx] = true;

        int minX = startX, maxX = startX;
        int minY = startY, maxY = startY;

        while (!queue.isEmpty()) {
            int idx = queue.poll();
            connectedIndices.add(idx);

            int cx = idx % width;
            int cy = idx / width;

            minX = Math.min(minX, cx);
            maxX = Math.max(maxX, cx);
            minY = Math.min(minY, cy);
            maxY = Math.max(maxY, cy);

            int[] dx = {0, 0, -1, 1};
            int[] dy = {-1, 1, 0, 0};

            for (int i = 0; i < 4; i++) {
                int nx = cx + dx[i];
                int ny = cy + dy[i];

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    if (!visited[nIdx] && grid[nIdx] == initialType) {
                        visited[nIdx] = true;
                        queue.add(nIdx);
                    }
                }
            }
        }

        int maskW = maxX - minX + 1;
        int maskH = maxY - minY + 1;
        byte[] maskPixels = new byte[maskW * maskH];
        Arrays.fill(maskPixels, ElementID.EMPTY.getId());

        for (int idx : connectedIndices) {
            int cx = idx % width;
            int cy = idx / width;

            int localX = cx - minX;
            int localY = cy - minY;

            maskPixels[localY * maskW + localX] = grid[idx];
            grid[idx] = ElementID.EMPTY.getId();
            velocity[idx] = 0;
        }

        float centerX = minX + maskW / 2.0f;
        float centerY = minY + maskH / 2.0f;

        Body newBody = new Body(centerX, centerY, maskW, maskH, maskPixels);
        this.bodies.add(newBody);
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

        stepsX = Math.clamp(stepsX, -Constants.INERTIA_MAX_STEP_LIMIT, Constants.INERTIA_MAX_STEP_LIMIT);
        stepsY = Math.clamp(stepsY, -Constants.INERTIA_MAX_STEP_LIMIT, Constants.INERTIA_MAX_STEP_LIMIT);

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
                            typeId == ElementID.STONE.getId()) continue;

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
                            typeId == ElementID.STONE.getId()) continue;

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

    public List<Body> getBodies() {
        return bodies;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public byte[] getGrid() { return grid; }
}