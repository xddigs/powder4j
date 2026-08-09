package org.p4j.sys;

import org.p4j.core.K;
import org.p4j.core.World;
import org.p4j.data.ElementID;

import java.util.concurrent.ThreadLocalRandom;

public class ReactionEngine {
    private final World world;

    public ReactionEngine(World world) {
        this.world = world;
    }

    public boolean process(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        ElementID type = ElementID.fromId(grid[idx]);

        return switch (type) {
            case CARBON -> reactCarbon(x, y, idx);
            case HYDROGEN, OXYGEN -> reactHydrogen(x, y, idx);
            case SAND -> reactSand(x, y, idx);
            case WATER, OIL, GASOLINE, MERCURY -> reactFluid(x, y, idx, type);
            case SILICON, DIRT, SEED, SALT, SODIUM, MUD -> reactPowder(x, y, idx, type);
            case TNT -> reactTNT(x, y, idx);
            case LAVA -> reactLava(x, y, idx);
            case STEAM -> reactSteam(x, y, idx);
            case METHANE -> reactMethane(x, y, idx);
            case CEMENT -> reactCement(x, y, idx);
            case CHLORINE -> reactChlorine(x, y, idx);
            case THERMITE -> reactThermite(x, y, idx);
            case ACID -> reactAcid(x, y, idx);
            case GUNPOWDER -> reactGunpowder(x, y, idx);
            case WOOD -> reactWood(x, y, idx);
            case FIRE -> reactFire(x, y, idx);
            case CARBON_MONOXIDE, CARBON_DIOXIDE -> reactGas(x, y, idx);
            default -> false;
        };
    }

    private boolean reactSand(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();
        int width = world.getWidth();
        int height = world.getHeight();

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (world.isInBounds(nx, ny)) {
                    int nIdx = world.getIndex(nx, ny);
                    ElementID e = ElementID.fromId(grid[nIdx]);
                    ElementID neighbor = ElementID.fromId(grid[nIdx]);
                    if (e.isHot() && neighbor == ElementID.SAND) {
                        if (Math.random() < K.LAVA_CREATION_CHANCE) {
                            grid[idx] = ElementID.LAVA.getId();
                            grid[nIdx] = ElementID.LAVA.getId();
                            updated[idx] = true;
                            return true;
                        }
                    }

                    if (neighbor.isWater()) {
                        grid[idx] = ElementID.WET_SAND.getId();
                        grid[nIdx] = ElementID.EMPTY.getId();
                        updated[idx] = true;
                        updated[nIdx] = true;
                        expandWetElement(x, y, ElementID.WET_SAND);

                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void expandWetElement(int startX, int startY,
                                  ElementID wetElement) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();
        int[][] directions = {{0, 1}, {-1, 1}, {1, 1}, {-1, 0}, {1, 0}};

        for (int[] dir : directions) {
            int targetX = startX + dir[0];
            int targetY = startY + dir[1];

            if (world.isInBounds(targetX, targetY)) {
                int targetIdx = world.getIndex(targetX, targetY);
                if (grid[targetIdx] == ElementID.EMPTY.getId()) {
                    grid[targetIdx] = wetElement.getId();
                    updated[targetIdx] = true;
                    break;
                }
            }
        }
    }

    private boolean reactCarbon(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (world.isInBounds(nx, ny)) {
                    int nIdx = world.getIndex(nx, ny);
                    ElementID neighbor = ElementID.fromId(grid[nIdx]);

                    if (neighbor == ElementID.WATER) {
                        grid[idx] = ElementID.OIL.getId();
                        grid[nIdx] = ElementID.OIL.getId();
                        updated[idx] = true;
                        return true;
                    }

                    if (neighbor == ElementID.SILICON) {
                        grid[idx] = ElementID.DIRT.getId();
                        grid[nIdx] = ElementID.DIRT.getId();
                        updated[idx] = true;
                    }

                    if (neighbor == ElementID.FIRE || neighbor == ElementID.LAVA) {
                        if (Math.random() < K.FIRE_IGNITION_CHANCE) {
                            grid[idx] = ElementID.FIRE.getId();
                            updated[idx] = true;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean reactTNT(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        int width = world.getWidth();
        int height = world.getHeight();

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    ElementID neighbor = ElementID.fromId(grid[ny * width + nx]);
                    if (neighbor == ElementID.FIRE || neighbor ==
                            ElementID.LAVA || neighbor.isHot()) {
                        ExplosionSystem.explodeTNT(world, x, y);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean reactLava(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();
        float[] velocity = world.getVelocity();
        int width = world.getWidth();
        int height = world.getHeight();

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
                        return true;

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
        return false;
    }

    private boolean reactFluid(int x, int y, int idx,
                                      ElementID e) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();
        boolean isFlammableFluid = e.isFlammable();
        int width = world.getWidth();
        int height = world.getHeight();

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    ElementID neighbor = ElementID.fromId(grid[nIdx]);

                    if (e == ElementID.OIL && neighbor == ElementID.STEAM) {
                        if (Math.random() < K.GASOLINE_CREATION_CHANCE) {
                            grid[idx] = ElementID.GASOLINE.getId();
                            grid[nIdx] = ElementID.CARBON_MONOXIDE.getId();
                            updated[idx] = true;
                            updated[nIdx] = true;
                            return true;
                        }
                    }

                    if (e.isWater() && (neighbor == ElementID.MUD ||
                            neighbor == ElementID.DIRT)) {
                        if (soak(x, y, idx)) {
                            return true;
                        }
                    }

                    if (isFlammableFluid && neighbor.isHot()) {
                        if (e == ElementID.GASOLINE) {
                            ExplosionSystem.createExplosion(world, x, y,
                                    K.GENERAL_EXPLOSION_RADIUS);
                        } else {
                            grid[idx] = ElementID.FIRE.getId();
                            updated[idx] = true;
                        }
                        return true;

                    } else if (e.isWater() && neighbor.isFlammable()) {
                        if (e.isHot()) {
                            grid[idx] = ElementID.STEAM.getId();
                            grid[nIdx] = ElementID.STEAM.getId();
                            updated[nIdx] = true;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean reactPowder(int x, int y, int idx,
                                       ElementID e) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();
        int width = world.getWidth();
        int height = world.getHeight();

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    ElementID neighbor = ElementID.fromId(grid[nIdx]);

                    if (e == ElementID.SILICON) {
                        if (neighbor == ElementID.FIRE) {
                            grid[idx] = ElementID.GLASS.getId();
                            updated[idx] = true;
                            return true;
                        }

                        if (neighbor == ElementID.OXYGEN) {
                            grid[idx] = ElementID.SAND.getId();
                            grid[nIdx] = ElementID.SAND.getId();
                            updated[idx] = true;
                            return true;
                        }
                    }

                    if (e == ElementID.SAND) {
                        if (neighbor == ElementID.FIRE) {
                            grid[idx] = ElementID.LAVA.getId();
                            grid[nIdx] = ElementID.LAVA.getId();
                            updated[idx] = true;
                            return true;
                        }
                    }

                    if (e == ElementID.DIRT && neighbor.isWater()) {
                        if (ThreadLocalRandom.current().nextFloat() <
                                K.MUD_SPREAD_CHANCE) {
                            grid[idx] = ElementID.MUD.getId();
                            grid[nIdx] = ElementID.MUD.getId();
                            updated[idx] = true;
                            updated[nIdx] = true;
                            return true;
                        }
                    }

                    if (e == ElementID.SEED) {
                        int belowY = y + 1;
                        if (world.isInBounds(x, belowY)) {
                            int belowIdx = world.getIndex(x, belowY);
                            ElementID ground = ElementID.fromId(grid[belowIdx]);
                            boolean isOnFertileGround = (ground == ElementID.DIRT ||
                                    ground == ElementID.MUD ||
                                    ground.isWater());

                            boolean isStackedOnSeed = (ground == ElementID.SEED);

                            if (isStackedOnSeed) {
                                grid[idx] = ElementID.EMPTY.getId();
                                updated[idx] = true;
                                return true;
                            }

                            if (isOnFertileGround) {
                                float rand = ThreadLocalRandom.current().nextFloat();
                                if (rand < K.GROW_TREE_CHANCE) {
                                    world.growTree(x, y);
                                    return true;
                                } else if (rand < K.GROW_GRASS_CHANCE) {
                                    world.growGrass(x, y);
                                    return true;
                                }
                            }
                        }
                    }

                    if (e == ElementID.SALT && (neighbor == ElementID.FIRE ||
                            neighbor == ElementID.LAVA)) {
                        grid[idx] = ElementID.SODIUM.getId();
                        grid[nIdx] = ElementID.CHLORINE.getId();
                        updated[idx] = true;
                        updated[nIdx] = true;
                        return true;
                    }

                    if (e == ElementID.SALT && neighbor.isWater()) {
                        grid[idx] = ElementID.WATER.getId();
                        updated[idx] = true;
                        return true;
                    }

                    if (e == ElementID.SODIUM && neighbor.isWater()) {
                        ExplosionSystem.createExplosion(world, x, y,
                                K.GENERAL_EXPLOSION_RADIUS);
                        grid[idx] = ElementID.FIRE.getId();
                        grid[nIdx] = ElementID.HYDROGEN.getId();
                        updated[idx] = true;
                        updated[nIdx] = true;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean reactSteam(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();
        float[] velocity = world.getVelocity();
        int width = world.getWidth();
        int height = world.getHeight();

        if (y < 5 && Math.random() < K.ICE_CREATION_CHANCE) {
            grid[idx] = ElementID.ICE.getId();
            velocity[idx] = 0;
            updated[idx] = true;
            return true;
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
                        return true;
                    }
                }
            }
        }

        if (Math.random() < K.STEAM_CONDENSE_CHANCE) {
            grid[idx] = ElementID.WATER.getId();
            velocity[idx] = 0;
            updated[idx] = true;
            return true;
        }

        return false;
    }

    private boolean reactMethane(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        int width = world.getWidth();
        int height = world.getHeight();

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    ElementID neighbor = ElementID.fromId(grid[ny * width + nx]);
                    if (neighbor == ElementID.FIRE ||
                            neighbor == ElementID.LAVA ||
                            neighbor.isHot()) {
                        grid[idx] = ElementID.FIRE.getId();
                        ExplosionSystem.createExplosion(world, x, y,
                                K.GENERAL_EXPLOSION_RADIUS);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean reactCement(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();
        int width = world.getWidth();
        int height = world.getHeight();

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
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean reactChlorine(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();
        int width = world.getWidth();
        int height = world.getHeight();

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    ElementID neighbor = ElementID.fromId(grid[nIdx]);

                    if (neighbor == ElementID.SODIUM) {
                        grid[idx] = ElementID.SALT.getId();
                        grid[nIdx] = ElementID.SALT.getId();
                        updated[idx] = true;
                        updated[nIdx] = true;
                        return true;
                    }

                    if (neighbor == ElementID.FIRE ||
                            neighbor == ElementID.LAVA ||
                            neighbor.isHot() ||
                            neighbor == ElementID.HYDROGEN) {
                        ExplosionSystem.createExplosion(world, x, y,
                                K.CHLORINE_EXPLOSION_RADIUS);
                        return true;
                    }

                    if (neighbor == ElementID.WATER) {
                        grid[idx] = ElementID.ACID.getId();
                        updated[idx] = true;
                        return true;
                    }
                    if (neighbor == ElementID.WOOD) {
                        grid[nIdx] = ElementID.CARBON_DIOXIDE.getId();
                        grid[idx] = ElementID.EMPTY.getId();
                        updated[nIdx] = true;
                        updated[idx] = true;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean reactThermite(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();
        int width = world.getWidth();
        int height = world.getHeight();

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    ElementID neighbor = ElementID.fromId(grid[nIdx]);

                    if (neighbor == ElementID.FIRE ||
                            neighbor == ElementID.LAVA ||
                            neighbor.isHot()) {
                        meltStone(x, y);
                        grid[idx] = ElementID.FIRE.getId();
                        updated[idx] = true;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean reactAcid(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();
        float[] velocity = world.getVelocity();
        int width = world.getWidth();
        int height = world.getHeight();

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    ElementID neighbor = ElementID.fromId(grid[nIdx]);
                    boolean isCorrosible = neighbor != ElementID.EMPTY &&
                            neighbor.isCorrosible();

                    if (neighbor == ElementID.SODIUM) {
                        ExplosionSystem.createExplosion(world, x, y,
                                K.GENERAL_EXPLOSION_RADIUS);
                        grid[idx] = ElementID.SALT.getId();
                        grid[nIdx] = ElementID.HYDROGEN.getId();
                        updated[idx] = true;
                        updated[nIdx] = true;
                        return true;
                    }

                    if (isCorrosible) {
                        grid[nIdx] = ElementID.CARBON_MONOXIDE.getId();
                        velocity[nIdx] = 0;
                        updated[nIdx] = true;
                        grid[idx] = ElementID.EMPTY.getId();
                        velocity[idx] = 0;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean reactGunpowder(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();
        float[] velocity = world.getVelocity();
        int width = world.getWidth();
        int height = world.getHeight();

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    ElementID neighbor = ElementID.fromId(grid[nIdx]);

                    if (neighbor == ElementID.FIRE ||
                            neighbor == ElementID.LAVA ||
                            neighbor.isHot()) {
                        grid[idx] = ElementID.FIRE.getId();
                        velocity[idx] = 0;
                        updated[idx] = true;
                        ExplosionSystem.createExplosion(world, x, y,
                                K.GENERAL_EXPLOSION_RADIUS);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean reactWood(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();
        int width = world.getWidth();
        boolean isNearFire = false;

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;

                int nx = x + dx;
                int ny = y + dy;

                if (world.isInBounds(nx, ny)) {
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
            if (Math.random() < K.WOOD_IGNITION_CHANCE) {
                grid[idx] = ElementID.CARBON.getId();
                updated[idx] = true;

                int smokeY = y - 1;
                if (world.isInBounds(x, smokeY)) {
                    int smokeIdx = smokeY * width + x;
                    if (grid[smokeIdx] == ElementID.EMPTY.getId()) {
                        grid[smokeIdx] = ElementID.CARBON_MONOXIDE.getId();
                        updated[smokeIdx] = true;
                    }
                }
                return true;
            }
        }

        if (Math.random() < K.WOOD_ABSORPTION_CHANCE) {
            absorb(x, y, 1, 1);
        }
        return false;
    }

    private boolean reactFire(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();
        float[] velocity = world.getVelocity();
        int width = world.getWidth();
        int height = world.getHeight();

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
                        if (Math.random() < K.GRAVEL_CREATION_CHANCE) {
                            grid[nIdx] = ElementID.GRAVEL.getId();
                            updated[nIdx] = true;
                        }
                    }

                    if (neighbor == ElementID.DIRT && Math.random() <
                            K.CEMENT_CREATION_CHANCE) {
                        grid[nIdx] = ElementID.CEMENT.getId();
                        updated[nIdx] = true;
                    }

                    if (neighbor == ElementID.MUD && Math.random() <
                            K.METHANE_CREATION_CHANCE) {
                        grid[nIdx] = ElementID.DIRT.getId();
                        grid[idx] = ElementID.STEAM.getId();
                        updated[nIdx] = true;
                        updated[idx] = true;
                        return true;
                    }

                    if (neighbor == ElementID.SILICON && Math.random() <
                            K.GLASS_FUSION_CHANCE) {
                        grid[nIdx] = ElementID.GLASS.getId();
                        updated[nIdx] = true;
                    }

                    if (neighbor.isFlammable()) {
                        nearFuel = true;
                        if (neighbor == ElementID.OIL) {
                            grid[nIdx] = ElementID.FIRE.getId();
                            grid[idx] = ElementID.CARBON_MONOXIDE.getId();
                            updated[nIdx] = true;
                            updated[idx] = true;
                            return true;

                        } else if (neighbor.isFlammable() &&
                                neighbor != ElementID.OIL &&
                                Math.random() < K.WOOD_IGNITION_CHANCE) {
                            grid[nIdx] = ElementID.FIRE.getId();
                            updated[nIdx] = true;
                        }

                    } else if (neighbor == ElementID.WATER &&
                            Math.random() < K.FIRE_EVAPORATION_CHANCE) {
                        double rand = Math.random();
                        ElementID smokeType = ElementID.CARBON_DIOXIDE;
                        if (rand > K.FIRE_SMOKE_GRAY_THRESHOLD) {
                            smokeType = ElementID.CARBON_MONOXIDE;
                        }

                        if (Math.random() < K.SALT_CHANCE) {
                            grid[nIdx] = ElementID.SALT.getId();
                        } else {
                            grid[nIdx] = smokeType.getId();
                        }
                        updated[nIdx] = true;
                        grid[idx] = ElementID.EMPTY.getId();
                        velocity[idx] = 0;
                        return true;

                    } else if (neighbor == ElementID.ICE) {
                        grid[nIdx] = ElementID.WATER.getId();
                        updated[nIdx] = true;

                    } else if (neighbor == ElementID.SAND &&
                            Math.random() < K.GLASS_FUSION_CHANCE) {
                        grid[nIdx] = ElementID.GLASS.getId();
                        updated[nIdx] = true;
                    }
                }
            }
        }

        if (Math.random() < K.FIRE_DISSIPATION_CHANCE) {
            grid[idx] = ElementID.EMPTY.getId();
            velocity[idx] = 0;
            return true;
        }

        return nearFuel && Math.random() < K.FIRE_NEAR_FUEL_PAUSE_CHANCE;
    }

    private boolean reactHydrogen(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();
        float[] velocity = world.getVelocity();

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (world.isInBounds(nx, ny)) {
                    ElementID neighbor = ElementID.fromId(grid[world.getIndex(nx, ny)]);
                    ElementID e = ElementID.fromId(grid[idx]);
                    int nIdx = world.getIndex(nx, ny);

                    if (e == ElementID.HYDROGEN && neighbor == ElementID.EMPTY) {
                        if (Math.random() < K.WATER_CREATION_CHANCE) {
                            grid[idx] = ElementID.WATER.getId();
                            grid[nIdx] = ElementID.WATER.getId();
                            updated[idx] = true;
                            updated[nIdx] = true;
                            return true;
                        }
                    }

                    if (e == ElementID.OXYGEN && neighbor == ElementID.HYDROGEN) {
                        grid[idx] = ElementID.WATER.getId();
                        grid[nIdx] = ElementID.WATER.getId();
                        updated[idx] = true;
                        return true;
                    }

                    if (e == ElementID.HYDROGEN && neighbor == ElementID.CHLORINE) {
                        grid[idx] = ElementID.ACID.getId();
                        grid[nIdx] = ElementID.ACID.getId();
                        updated[idx] = true;
                        return true;
                    }

                    if (neighbor == ElementID.FIRE ||
                            neighbor == ElementID.LAVA ||
                            neighbor.isHot()) {
                        ExplosionSystem.createExplosion(world, x, y,
                                K.GENERAL_EXPLOSION_RADIUS);
                        grid[idx] = ElementID.STEAM.getId();
                        grid[nIdx] = ElementID.STEAM.getId();
                        updated[idx] = true;
                        return true;
                    }
                }
            }
        }

        if (Math.random() < K.HYDROGEN_DISSIPATION_CHANCE) {
            grid[idx] = ElementID.EMPTY.getId();
            velocity[idx] = 0;
            return true;
        }
        return false;
    }

    private boolean reactGas(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        float[] velocity = world.getVelocity();

        if (Math.random() < K.SMOKE_DISSIPATION_CHANCE) {
            grid[idx] = ElementID.EMPTY.getId();
            velocity[idx] = 0;
            return true;
        }
        return false;
    }

    private boolean soak(int waterX, int waterY, int waterIdx) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();
        float[] velocity = world.getVelocity();
        int width = world.getWidth();
        int height = world.getHeight();

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

    private void meltStone(int centerX, int centerY) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();
        int width = world.getWidth();
        int height = world.getHeight();

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

    public void heat(int x, int y, float tempAmount) {
        if (!world.isInBounds(x, y)) return;
        int idx = world.getIndex(x, y);
        world.addTemperature(idx, tempAmount);
    }

    public void absorb(int startX, int startY, int radius, int maxWater) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();
        int width = world.getWidth();
        int height = world.getHeight();

        int count = 0;
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                if (count >= maxWater) return;

                int nx = startX + dx;
                int ny = startY + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int nIdx = ny * width + nx;
                    ElementID element = ElementID.fromId(grid[nIdx]);

                    if (world.isInBounds(nx, ny) && element.isWater()) {
                        grid[nIdx] = ElementID.EMPTY.getId();
                        updated[nIdx] = true;
                        count++;
                    }
                }
            }
        }
    }
}