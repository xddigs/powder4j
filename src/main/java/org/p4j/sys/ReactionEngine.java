package org.p4j.sys;

import org.p4j.core.K;
import org.p4j.core.World;
import org.p4j.data.ElementID;
import org.p4j.data.ElementReaction;
import org.p4j.data.NeighborVisitor;
import org.p4j.data.Recipe;

import java.util.concurrent.ThreadLocalRandom;

public class ReactionEngine {
    private final World world;
    private final ElementReaction[] reactions = new ElementReaction[256];
    private final RecipeRegistry registry = new RecipeRegistry();

    public ReactionEngine(World world) {
        this.world = world;
        init();
        registerReactions();
    }

    private void init() {
        registry.register(ElementID.OXYGEN, ElementID.COPPER,
                          ElementID.COPPER_OXIDIZED, ElementID.COPPER_OXIDIZED);

        registry.register(ElementID.HYDROGEN, ElementID.CHLORINE,
                          ElementID.ACID, ElementID.ACID);

        registry.register(ElementID.ACID, ElementID.STEEL,
                          ElementID.ASH, ElementID.ASH);

        registry.register(ElementID.SULFUR, ElementID.CARBON,
                          ElementID.BLACK_POWDER, ElementID.BLACK_POWDER);

        registry.register(ElementID.SILICON, ElementID.OXYGEN,
                          ElementID.SAND, ElementID.SAND);

        registry.register(ElementID.SODIUM, ElementID.ACID,
                          ElementID.SALT, ElementID.HYDROGEN);

        registry.register(ElementID.SODIUM, ElementID.WATER,
                ElementID.SODIUM_HYDROXIDE, ElementID.HYDROGEN);

        registry.register(ElementID.ACID, ElementID.OIL,
                ElementID.NITROGLYCERIN, ElementID.WATER);

        registry.register(ElementID.ACID, ElementID.SODIUM_HYDROXIDE,
                ElementID.SALT, ElementID.WATER);
    }

    private void registerReactions() {
        register(ElementID.SAND, this::reactSand);
        register(ElementID.CARBON, this::reactCarbon);
        register(ElementID.TNT, this::reactTNT);
        register(ElementID.COPPER, this::reactCopper);
        register(ElementID.CEMENT, this::reactCement);
        register(ElementID.THERMITE, this::reactThermite);
        register(ElementID.ACID, this::reactAcid);
        register(ElementID.WOOD, this::reactWood);
        register(ElementID.HYDROGEN, this::reactHydrogen);
        register(ElementID.NITROGLYCERIN, this::reactNitroglycerin);
    }

    private void register(ElementID id, ElementReaction reaction) {
        reactions[id.getId() & 0xFF] = reaction;
    }

    public boolean process(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        ElementID type = ElementID.fromId(grid[idx]);
        ElementReaction reaction = reactions[type.getId() & 0xFF];
        if (reaction != null && reaction.process(x, y, idx)) {
            return true;
        }

        if (type.isLiquid()) return reactFluid(x, y, idx, type);
        if (type.isPowder()) return reactPowder(x, y, idx, type);
        if (type.isGas())    return reactGas(x, y, idx);

        return false;
    }

    private boolean forEachNeighbor(int x, int y, NeighborVisitor visitor) {
        byte[] grid = world.getGrid();
        int width = world.getWidth();

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;

                if (world.isInBounds(nx, ny)) {
                    int nIdx = ny * width + nx;
                    ElementID neighbor = ElementID.fromId(grid[nIdx]);
                    if (visitor.visit(nx, ny, nIdx, neighbor)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean reactSand(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();

        return forEachNeighbor(x, y, (_, _, nIdx,
                                      neighbor) -> {
            if (neighbor.isWater()) {
                grid[idx] = ElementID.WET_SAND.getId();
                grid[nIdx] = ElementID.WET_SAND.getId();
                updated[idx] = true;
                updated[nIdx] = true;
                return true;
            }
            return false;
        });
    }

    private boolean reactCarbon(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();

        return forEachNeighbor(x, y, (_, _, nIdx, neighbor) -> {
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
                return true;
            }
            if ((neighbor == ElementID.FIRE || neighbor == ElementID.LAVA) &&
                    ThreadLocalRandom.current().nextFloat() < K.FIRE_IGNITION_CHANCE) {
                grid[idx] = ElementID.FIRE.getId();
                updated[idx] = true;
                return true;
            }
            return false;
        });
    }

    private boolean reactTNT(int x, int y, int idx) {
        return forEachNeighbor(x, y, (_, _, _, neighbor) -> {
            if (neighbor == ElementID.FIRE || neighbor == ElementID.LAVA || neighbor.isHot()) {
                ExplosionSystem.explodeTNT(world, x, y);
                return true;
            }
            return false;
        });
    }

    private boolean reactCopper(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();

        return forEachNeighbor(x, y, (_, _, nIdx, neighbor) -> {
            if (neighbor == ElementID.OXYGEN) {
                grid[nIdx] = ElementID.COPPER_OXIDIZED.getId();
                updated[idx] = true;
                return true;
            }
            return false;
        });
    }

    private boolean reactCement(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();

        return forEachNeighbor(x, y, (_, _, nIdx, neighbor) -> {
            if (neighbor.isWater()) {
                grid[idx] = ElementID.STONE.getId();
                grid[nIdx] = ElementID.VOID.getId();
                updated[idx] = true;
                updated[nIdx] = true;
                return true;
            }
            return false;
        });
    }

    private boolean reactThermite(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();

        return forEachNeighbor(x, y, (_, _, _, neighbor) -> {
            if (neighbor == ElementID.FIRE || neighbor == ElementID.LAVA || neighbor.isHot()) {
                meltStone(x, y);
                grid[idx] = ElementID.FIRE.getId();
                updated[idx] = true;
                return true;
            }
            return false;
        });
    }

    private boolean reactAcid(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();
        float[] velocity = world.getVelocity();

        return forEachNeighbor(x, y, (_, _, nIdx, neighbor) -> {
            if (neighbor == ElementID.SODIUM) {
                ExplosionSystem.createExplosion(world, x, y,
                        K.GENERAL_EXPLOSION_RADIUS);
                grid[idx] = ElementID.SALT.getId();
                grid[nIdx] = ElementID.HYDROGEN.getId();
                updated[idx] = true;
                updated[nIdx] = true;
                return true;
            }

            if (neighbor != ElementID.VOID && neighbor.isCorrosible()) {
                grid[nIdx] = ElementID.CARBON_MONOXIDE.getId();
                velocity[nIdx] = 0;
                updated[nIdx] = true;
                grid[idx] = ElementID.VOID.getId();
                velocity[idx] = 0;
                return true;
            }
            return false;
        });
    }

    private boolean reactWood(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();

        boolean isNearFire = forEachNeighbor(
                x, y, (_, _, _, neighbor) -> neighbor == ElementID.FIRE);

        if (isNearFire && ThreadLocalRandom.current().nextFloat() <
                K.WOOD_IGNITION_CHANCE) {
            grid[idx] = ElementID.CARBON.getId();
            updated[idx] = true;

            int smokeY = y - 1;
            if (world.isInBounds(x, smokeY)) {
                int smokeIdx = world.getIndex(x, smokeY);
                if (grid[smokeIdx] == ElementID.VOID.getId()) {
                    grid[smokeIdx] = ElementID.CARBON_MONOXIDE.getId();
                    updated[smokeIdx] = true;
                }
            }
            return true;
        }

        if (ThreadLocalRandom.current().nextFloat() < K.WOOD_ABSORPTION_CHANCE) {
            absorb(x, y, 1, 1);
        }
        return false;
    }

    private boolean reactHydrogen(int x, int y, int idx) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();
        float[] velocity = world.getVelocity();

        boolean reacted = forEachNeighbor(x, y, (_, _, nIdx, neighbor) -> {
            ElementID current = ElementID.fromId(grid[idx]);

            if ((current == ElementID.OXYGEN && neighbor == ElementID.HYDROGEN) ||
                    (current == ElementID.HYDROGEN && neighbor == ElementID.OXYGEN)) {
                if (ThreadLocalRandom.current().nextFloat() < K.WATER_CREATION_CHANCE) {
                    grid[idx] = ElementID.WATER.getId();
                    grid[nIdx] = ElementID.WATER.getId();
                    velocity[idx] = 0;
                    velocity[nIdx] = 0;
                    updated[idx] = true;
                    updated[nIdx] = true;
                    return true;
                }
            }

            if (current == ElementID.HYDROGEN && neighbor == ElementID.CARBON) {
                grid[idx] = ElementID.BEIGE_POWDER.getId();
                grid[nIdx] = ElementID.BEIGE_POWDER.getId();
                updated[idx] = true;
                updated[nIdx] = true;
                return true;
            }

            if (neighbor == ElementID.FIRE || neighbor == ElementID.LAVA) {
                ExplosionSystem.createExplosion(world, x, y,
                        K.GENERAL_EXPLOSION_RADIUS);
                grid[idx] = ElementID.STEAM.getId();
                grid[nIdx] = ElementID.STEAM.getId();
                updated[idx] = true;
                return true;
            }
            return false;
        });

        if (reacted) return true;

        if (ThreadLocalRandom.current().nextFloat() <
                K.HYDROGEN_DISSIPATION_CHANCE) {
            grid[idx] = ElementID.VOID.getId();
            velocity[idx] = 0;
            return true;
        }
        return false;
    }

    private boolean reactFluid(int x, int y, int idx, ElementID e) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();

        return forEachNeighbor(x, y, (_, _, nIdx, neighbor) -> {
            float currentTemp = world.getTemperatureAt(x, y);

            if (e == ElementID.OIL && neighbor == ElementID.STEAM) {
                if (ThreadLocalRandom.current().nextFloat() <
                        K.GASOLINE_CREATION_CHANCE) {
                    grid[idx] = ElementID.GASOLINE.getId();
                    grid[nIdx] = ElementID.CARBON_MONOXIDE.getId();
                    updated[idx] = true;
                    updated[nIdx] = true;
                    return true;
                }
            }

            if (e.isWater() && currentTemp < e.getDefaultTemp()) {
                grid[idx] = ElementID.ICE.getId();
                grid[nIdx] = ElementID.ICE.getId();
                updated[idx] = true;
                updated[nIdx] = true;
                return true;
            }

            if (e.isWater() && (neighbor == ElementID.MUD || neighbor == ElementID.DIRT)) {
                if (canBeSoaked(x, y, idx)) return true;
            }

            if (e.isFlammable() && neighbor.isHot()) {
                if (e == ElementID.GASOLINE) {
                    ExplosionSystem.createExplosion(world, x, y,
                            K.GENERAL_EXPLOSION_RADIUS);
                } else {
                    grid[idx] = ElementID.FIRE.getId();
                    updated[idx] = true;
                }
                return true;
            } else if (e.isWater() && neighbor.isFlammable() && e.isHot()) {
                grid[idx] = ElementID.STEAM.getId();
                grid[nIdx] = ElementID.STEAM.getId();
                updated[nIdx] = true;
                return true;
            }
            return false;
        });
    }

    private boolean reactPowder(int x, int y, int idx, ElementID e) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();

        boolean reacted = forEachNeighbor(x, y, (_, _, nIdx,
                                                 neighbor) -> {
            Recipe recipe = registry.getRecipe(e, neighbor);
            if (recipe != null) {
                grid[idx] = recipe.resultA().getId();
                grid[nIdx] = recipe.resultB().getId();
                updated[idx] = true;
                updated[nIdx] = true;
                return true;
            }

            if (e == ElementID.BLACK_POWDER) {
                float currentTemp = world.getTemperatureAt(x, y);
                if (currentTemp > e.getDefaultTemp() || neighbor.isHot()) {
                    grid[idx] = ElementID.GUNPOWDER.getId();
                    grid[nIdx] = ElementID.GUNPOWDER.getId();
                    updated[idx] = true;
                    updated[nIdx] = true;
                    return true;
                }
            }

            if (e == ElementID.GUNPOWDER) {
                float currentTemp = world.getTemperatureAt(x, y);
                if (currentTemp > e.getMeltingPoint()) {
                    grid[idx] = ElementID.FIRE.getId();
                    grid[nIdx] = ElementID.FIRE.getId();
                    updated[idx] = true;
                    updated[nIdx] = true;
                    ExplosionSystem.createExplosion(world, x, y, K.GENERAL_EXPLOSION_RADIUS);
                    return true;
                }
            }

            if (e == ElementID.DIRT && neighbor.isWater()) {
                if (ThreadLocalRandom.current().nextFloat() < K.MUD_SPREAD_CHANCE) {
                    grid[idx] = ElementID.MUD.getId();
                    grid[nIdx] = ElementID.MUD.getId();
                    updated[idx] = true;
                    updated[nIdx] = true;
                    return true;
                }
            }

            if (e == ElementID.SULFUR && neighbor == ElementID.WATER) {
                grid[idx] = ElementID.FIRE.getId();
                grid[nIdx] = ElementID.FIRE.getId();
                updated[idx] = true;
                return true;
            }

            if (e == ElementID.SALT && (neighbor == ElementID.FIRE ||
                neighbor == ElementID.LAVA)) {
                grid[idx] = ElementID.SODIUM.getId();
                grid[nIdx] = ElementID.CHLORINE.getId();
                updated[idx] = true;
                updated[nIdx] = true;
                return true;
            }

            if (e == ElementID.SULFUR) {
                float currentTemp = world.getTemperatureAt(x, y);
                if (currentTemp > e.getBoilingPoint()) {
                    grid[idx] = ElementID.MERCURY.getId();
                    grid[nIdx] = ElementID.MERCURY.getId();
                    updated[idx] = true;
                    updated[nIdx] = true;
                }
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
            return false;
        });

        if (reacted) return true;

        if (e == ElementID.SEED) {
            int belowY = y + 1;
            if (world.isInBounds(x, belowY)) {
                int belowIdx = world.getIndex(x, belowY);
                ElementID ground = ElementID.fromId(grid[belowIdx]);

                if (ground == ElementID.SEED) {
                    grid[idx] = ElementID.VOID.getId();
                    updated[idx] = true;
                    return true;
                }

                if (ground == ElementID.DIRT || ground == ElementID.MUD || ground.isWater()) {
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
        return false;
    }

    private boolean reactGas(int x, int y, int idx) {
        if (!world.isInBounds(x, y + 1)) return false;

        byte[] grid = world.getGrid();
        float[] velocity = world.getVelocity();
        boolean[] updated = world.getUpdated();
        int nIdx = world.getIndex(x, y + 1);
        ElementID e = ElementID.fromId(grid[idx]);
        ElementID neighbor = ElementID.fromId(grid[nIdx]);
        float temp = world.getTemperatureAt(x, y);

        if (ThreadLocalRandom.current().nextFloat() <
                K.SMOKE_DISSIPATION_CHANCE) {
            grid[idx] = ElementID.VOID.getId();
            velocity[idx] = 0;
            return true;
        }

        if (neighbor == ElementID.SODIUM) {
            grid[idx] = ElementID.SALT.getId();
            grid[nIdx] = ElementID.SALT.getId();
            updated[idx] = true;
            updated[nIdx] = true;
            return true;
        }

        if (e == ElementID.HYDROGEN && neighbor == ElementID.CHLORINE) {
            grid[idx] = ElementID.ACID.getId();
            grid[nIdx] = ElementID.ACID.getId();
            velocity[idx] = 0;
            velocity[nIdx] = 0;
            updated[idx] = true;
            updated[nIdx] = true;
            return true;
        }

        if (e == ElementID.CHLORINE && neighbor.isHot()) {
            if (temp > e.getBoilingPoint()) {
                ExplosionSystem.createExplosion(world, x, y,
                        K.CHLORINE_EXPLOSION_RADIUS);
                grid[idx] = ElementID.VOID.getId();
                grid[nIdx] = ElementID.VOID.getId();
                velocity[idx] = 0;
                velocity[nIdx] = 0;
                updated[idx] = true;
            }
        }
        return false;
    }

    private boolean reactNitroglycerin(int x, int y, int idx) {
        float[] velocity = world.getVelocity();

        if (Math.abs(velocity[idx]) > K.NITRO_SPEED_MIN) {
            ExplosionSystem.createExplosion(world, x, y,
                    K.GENERAL_EXPLOSION_RADIUS * 2);
            return true;
        }

        return forEachNeighbor(x, y, (_, _, _, neighbor) -> {
            if (neighbor == ElementID.FIRE ||
                neighbor == ElementID.LAVA ||
                neighbor.isHot()) {
                ExplosionSystem.createExplosion(world, x, y,
                        K.GENERAL_EXPLOSION_RADIUS * 2);
                return true;
            }
            return false;
        });
    }

    private boolean canBeSoaked(int waterX, int waterY, int waterIdx) {
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
                    grid[waterIdx] = ElementID.VOID.getId();
                    velocity[waterIdx] = 0;
                    updated[waterIdx] = true;

                    grid[targetIdx] = ElementID.MUD.getId();
                    updated[targetIdx] = true;
                    return true;
                }

                if (element != ElementID.MUD) break;
            }
        }
        return false;
    }

    private void meltStone(int centerX, int centerY) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();

        int radius = 2;
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                int nx = centerX + dx;
                int ny = centerY + dy;
                if (world.isInBounds(nx, ny)) {
                    int nIdx = world.getIndex(nx, ny);
                    if (grid[nIdx] == ElementID.STONE.getId()) {
                        grid[nIdx] = ElementID.LAVA.getId();
                        updated[nIdx] = true;
                    }
                }
            }
        }
    }

    public ElementID produce(ElementID elementA, ElementID elementB) {
        Recipe recipe = registry.getRecipe(elementA, elementB);
        return (recipe != null) ? recipe.resultA() : null;
    }

    public void absorb(int startX, int startY, int radius, int maxWater) {
        byte[] grid = world.getGrid();
        boolean[] updated = world.getUpdated();

        int count = 0;
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                if (count >= maxWater) return;

                int nx = startX + dx;
                int ny = startY + dy;

                if (world.isInBounds(nx, ny)) {
                    int nIdx = world.getIndex(nx, ny);
                    ElementID element = ElementID.fromId(grid[nIdx]);

                    if (element.isWater()) {
                        grid[nIdx] = ElementID.VOID.getId();
                        updated[nIdx] = true;
                        count++;
                    }
                }
            }
        }
    }
}