package org.p4j.sys;

import org.p4j.core.Constants;
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

                if (normalizedDist < 0.3) {
                    world.setCell(targetX, targetY, ElementID.FIRE);
                } else if (normalizedDist < 0.7) {
                    if (random.nextFloat() < 0.25f) {
                        world.setCell(targetX, targetY, ElementID.FIRE);
                    } else {
                        world.setCell(targetX, targetY, ElementID.EMPTY);
                    }
                } else {
                    float rnd = random.nextFloat();
                    if (rnd < 0.35f) {
                        world.setCell(targetX, targetY, ElementID.SMOKE_GRAY);
                    } else if (rnd < 0.60f) {
                        world.setCell(targetX, targetY, ElementID.EMPTY);
                    }
                }
            }
        }
    }

    public static void explodeTNT(World world, int centerX, int centerY) {
        createExplosion(world, centerX, centerY, Constants.TNT_EXPLOSION_RADIUS);
    }
}