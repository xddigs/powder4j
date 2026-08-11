package org.p4j.sys;

import org.p4j.core.K;
import org.p4j.core.World;
import org.p4j.data.ElementID;

import java.util.concurrent.ThreadLocalRandom;

public class ExplosionSystem {

    public static void createExplosion(World world, int centerX, int centerY, int radius) {
        int radiusSq = radius * radius;
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                int distSq = dx * dx + dy * dy;

                if (distSq > radiusSq) continue;

                int targetX = centerX + dx;
                int targetY = centerY + dy;

                if (!world.isInBounds(targetX, targetY)) continue;
                double normalizedDist = Math.sqrt(distSq) / (double) radius;

                if (normalizedDist < K.EXPLOSION_CORE_RADIUS_RATIO) {
                    world.setCell(targetX, targetY, ElementID.FIRE);
                } else if (normalizedDist < K.EXPLOSION_MID_RADIUS_RATIO) {
                    if (random.nextFloat() < K.EXPLOSION_MID_FIRE_CHANCE) {
                        world.setCell(targetX, targetY, ElementID.FIRE);
                    } else {
                        world.setCell(targetX, targetY, ElementID.VOID);
                    }
                } else {
                    float rnd = random.nextFloat();
                    if (rnd < K.EXPLOSION_OUTER_CO2_CHANCE) {
                        world.setCell(targetX, targetY, ElementID.CARBON_DIOXIDE);
                    } else if (rnd < K.EXPLOSION_OUTER_EMPTY_CHANCE) {
                        world.setCell(targetX, targetY, ElementID.VOID);
                    }
                }
            }
        }
    }

    public static void explodeTNT(World world, int centerX, int centerY) {
        createExplosion(world, centerX, centerY, K.TNT_EXPLOSION_RADIUS);
    }
}