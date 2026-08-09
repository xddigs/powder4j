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

        float density = Math.max(1, type.getDensity());
        float gravityFactor = density / K.GRAVITY_MASS_FACTOR;

        float currentVel = world.getVelocity(idx) + (K.GRAVITY * gravityFactor);
        float maxSpeed = K.MAX_FALL_SPEED * (0.8f + (density * 0.1f));
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
        float currentVel = world.getVelocity(idx) + 0.3f;
        float maxSpeed = K.MAX_GAS_SPEED;
        if (currentVel > maxSpeed) currentVel = maxSpeed;

        int steps = Math.max(1, (int) currentVel);
        int currentX = x;
        int currentY = y;
        int currentIdx = idx;
        boolean hasMoved = false;
        byte currentId = world.getGrid()[idx];

        for (int i = 0; i < steps; i++) {
            int aboveY = currentY - 1;
            boolean movedThisStep = false;

            if (aboveY >= 0) {
                int aboveIdx = world.getIndex(currentX, aboveY);
                if (canGasDisplace(currentId, world.getGrid()[aboveIdx])) {
                    int prevIdx = currentIdx;
                    world.swap(currentIdx, aboveIdx);
                    leaveGasTrail(prevIdx, type);
                    currentY = aboveY;
                    currentIdx = aboveIdx;
                    hasMoved = true;
                    movedThisStep = true;
                } else {
                    boolean isLeftFirst = ThreadLocalRandom.current().nextBoolean();
                    int[] dxs = isLeftFirst ? new int[]{-1, 1} : new int[]{1, -1};

                    for (int dx : dxs) {
                        int diagX = currentX + dx;
                        if (diagX >= 0 && diagX < world.getWidth()) {
                            int diagIdx = world.getIndex(diagX, aboveY);
                            if (canGasDisplace(currentId, world.getGrid()[diagIdx])) {
                                int prevIdx = currentIdx;
                                world.swap(currentIdx, diagIdx);
                                leaveGasTrail(prevIdx, type);
                                currentX = diagX;
                                currentY = aboveY;
                                currentIdx = diagIdx;
                                hasMoved = true;
                                movedThisStep = true;
                                break;
                            }
                        }
                    }
                }
            }

            if (!movedThisStep) {
                int dispersionRate = Math.max(1, type.getDispersionRate());
                int dir = ThreadLocalRandom.current().nextBoolean() ? 1 : -1;
                if (world.flow(currentX, currentY, currentIdx, dir, dispersionRate, currentId) ||
                        world.flow(currentX, currentY, currentIdx, -dir, dispersionRate, currentId)) {
                    hasMoved = true;
                }
                break;
            }
        }

        world.setVelocity(currentIdx, hasMoved ? currentVel : 0.0f);
        return hasMoved;
    }

    private void leaveGasTrail(int trailIdx, ElementID gasType) {
        if (ThreadLocalRandom.current().nextFloat() > K.GAS_TRAIL_CHANCE) return;
        byte[] grid = world.getGrid();

        if (grid[trailIdx] == ElementID.EMPTY.getId()) {
            if (gasType == ElementID.METHANE) {
                grid[trailIdx] = ElementID.CARBON.getId();
            } else if (gasType == ElementID.STEAM) {
                grid[trailIdx] = ElementID.SMOKE_LIGHT.getId();
            } else if (gasType == ElementID.CHLORINE) {
                grid[trailIdx] = ElementID.ACID.getId();
            }
            world.getUpdated()[trailIdx] = true;
        }
    }

    private boolean canGasDisplace(byte currentId, byte targetId) {
        if (targetId == ElementID.EMPTY.getId()) {
            return true;
        }
        if (targetId == ElementID.STONE.getId()) {
            return false;
        }

        float currentDensity = ElementID.fromId(currentId).getDensity();
        float targetDensity = ElementID.fromId(targetId).getDensity();

        return currentDensity < targetDensity;
    }
}