package org.p4j.sys;

import org.p4j.core.K;
import org.p4j.core.World;
import org.p4j.data.ElementID;

import java.util.concurrent.ThreadLocalRandom;

public class MovementEngine {
    private final World world;

    public MovementEngine(World world) {
        this.world = world;
    }

    public boolean update(int x, int y, int idx, ElementID type) {
        if (type == ElementID.EMPTY || type == ElementID.STONE) {
            world.setVelocity(idx, 0.0f);
            return false;
        }

        if (type.isPowder() || type == ElementID.SAND) {
            return updatePowder(x, y, idx, type);
        }

        if (type.isLiquid() || type == ElementID.LAVA) {
            return updateLiquid(x, y, idx, type);
        }

        if (type.isGas() || type == ElementID.FIRE) {
            return updateGas(x, y, idx, type);
        }

        return false;
    }

    private boolean updatePowder(int x, int y, int idx, ElementID type) {
        byte currentId = world.getGrid()[idx];

        float currentVel = world.getVelocity(idx) + K.GRAVITY;
        float maxSpeed = K.MAX_FALL_SPEED;
        if (currentVel > maxSpeed) currentVel = maxSpeed;

        int steps = Math.max(1, (int) currentVel);
        int currentX = x;
        int currentY = y;
        int currentIdx = idx;
        boolean hasMoved = false;

        for (int i = 0; i < steps; i++) {
            int belowY = currentY + 1;
            if (belowY >= world.getHeight()) {
                currentVel = 0;
                break;
            }

            int belowIdx = world.getIndex(currentX, belowY);
            if (world.canDisplace(currentId, world.getGrid()[belowIdx])) {
                world.swap(currentIdx, belowIdx);
                currentY = belowY;
                currentIdx = belowIdx;
                hasMoved = true;
                continue;
            }

            boolean leftFirst = ThreadLocalRandom.current().nextBoolean();
            int[] dxs = leftFirst ? new int[]{-1, 1} : new int[]{1, -1};
            boolean hasDiagonallyMoved = false;

            for (int dx : dxs) {
                int diagX = currentX + dx;
                if (diagX >= 0 && diagX < world.getWidth()) {
                    int diagIdx = world.getIndex(diagX, belowY);
                    if (world.canDisplace(currentId, world.getGrid()[diagIdx])) {
                        world.swap(currentIdx, diagIdx);
                        currentX = diagX;
                        currentY = belowY;
                        currentIdx = diagIdx;
                        hasMoved = true;
                        hasDiagonallyMoved = true;
                        break;
                    }
                }
            }

            if (!hasDiagonallyMoved) {
                currentVel = 0;
                break;
            }
        }

        world.setVelocity(currentIdx, currentVel);
        return hasMoved;
    }

    private boolean updateLiquid(int x, int y, int idx, ElementID type) {
        if (updatePowder(x, y, idx, type)) {
            return true;
        }
        world.setVelocity(idx, 0.0f);
        int dir = ThreadLocalRandom.current().nextBoolean() ? 1 : -1;
        int dispersionRate = type.getDispersionRate();
        return world.flow(x, y, idx, dir, dispersionRate, type.getId()) ||
                world.flow(x, y, idx, -dir, dispersionRate, type.getId());
    }

    private boolean updateGas(int x, int y, int idx, ElementID type) {
        float currentVel = world.getVelocity(idx) + K.GRAVITY;
        float maxSpeed = K.MAX_GAS_SPEED;
        if (currentVel > maxSpeed) currentVel = maxSpeed;

        int steps = Math.max(1, (int) currentVel);
        int currentX = x;
        int currentY = y;
        int currentIdx = idx;
        boolean moved = false;

        byte currentId = world.getGrid()[idx];

        for (int i = 0; i < steps; i++) {
            int aboveY = currentY - 1;
            if (aboveY < 0) {
                currentVel = 0;
                break;
            }

            int aboveIdx = world.getIndex(currentX, aboveY);
            if (world.canDisplace(currentId, world.getGrid()[aboveIdx])) {
                world.swap(currentIdx, aboveIdx);
                currentY = aboveY;
                currentIdx = aboveIdx;
                moved = true;
                continue;
            }

            boolean leftFirst = ThreadLocalRandom.current().nextBoolean();
            int[] dxs = leftFirst ? new int[]{-1, 1} : new int[]{1, -1};
            boolean diagMoved = false;

            for (int dx : dxs) {
                int diagX = currentX + dx;
                if (diagX >= 0 && diagX < world.getWidth()) {
                    int diagIdx = world.getIndex(diagX, aboveY);
                    if (world.canDisplace(currentId, world.getGrid()[diagIdx])) {
                        world.swap(currentIdx, diagIdx);
                        currentX = diagX;
                        currentY = aboveY;
                        currentIdx = diagIdx;
                        moved = true;
                        diagMoved = true;
                        break;
                    }
                }
            }

            if (!diagMoved) {
                currentVel = 0;
                break;
            }
        }

        world.setVelocity(currentIdx, currentVel);
        return moved;
    }
}