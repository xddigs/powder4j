package org.p4j.sys;

import org.p4j.core.K;
import org.p4j.data.ElementID;

public class ThermoEngine {

    @FunctionalInterface
    public interface PhaseChangeCallback {
        void onPhaseChange(int idx, float currentTemp);
    }

    private float ambientTemp = K.DEFAULT_AMBIENT_TEMP;
    private final float ambientLossRate = K.DEFAULT_AMBIENT_LOSS_RATE;
    private float simulationSpeed = K.DEFAULT_SIMULATION_SPEED;

    public ThermoEngine() {}

    public void update(byte[] elements, float[] temps, float[] nextTemps,
                       int width, int height, PhaseChangeCallback callback) {
        int totalCells = width * height;

        for (int y = 0; y < height; y++) {
            int rowOffset = y * width;
            for (int x = 0; x < width; x++) {
                int index = rowOffset + x;
                ElementID currentElem = ElementID.fromId(elements[index]);

                if (currentElem == ElementID.EMPTY) {
                    nextTemps[index] = ambientTemp;
                    continue;
                }

                float currentTemp = temps[index];

                if (currentElem == ElementID.FIRE) {
                    currentTemp = Math.max(
                            currentTemp + K.HEAT_ADD_FIRE,
                            currentElem.getDefaultTemp()
                    );
                    temps[index] = currentTemp;
                } else if (currentElem == ElementID.LAVA) {
                    currentTemp = Math.max(
                            currentTemp + K.HEAT_ADD_LAVA,
                            currentElem.getDefaultTemp()
                    );
                    temps[index] = currentTemp;
                }

                float totalDeltaTemp = 0.0f;

                totalDeltaTemp += computeDeltaTemp(
                        currentElem, currentTemp, elements, temps,
                        x + 1, y, width, height
                );
                totalDeltaTemp += computeDeltaTemp(
                        currentElem, currentTemp, elements, temps,
                        x - 1, y, width, height
                );
                totalDeltaTemp += computeDeltaTemp(
                        currentElem, currentTemp, elements, temps,
                        x, y + 1, width, height
                );
                totalDeltaTemp += computeDeltaTemp(
                        currentElem, currentTemp, elements, temps,
                        x, y - 1, width, height
                );

                float newTemp = currentTemp +
                        (totalDeltaTemp * simulationSpeed);

                if (currentElem == ElementID.FIRE ||
                        currentElem == ElementID.LAVA) {
                    newTemp = Math.max(newTemp, currentElem.getDefaultTemp());
                } else {
                    newTemp += (ambientTemp - newTemp) * ambientLossRate;
                }

                nextTemps[index] = newTemp;
            }
        }

        for (int i = 0; i < totalCells; i++) {
            ElementID elem = ElementID.fromId(elements[i]);
            if (elem == ElementID.EMPTY) continue;
            float temp = nextTemps[i];

            boolean canBoil = temp >= elem.getBoilingPoint() +
                    K.LATENT_HEAT_ACTIVATION_DELTA;

            boolean canMelt = temp >= elem.getMeltingPoint() +
                    K.LATENT_HEAT_ACTIVATION_DELTA;

            if (elem.isLiquid() && canBoil) {
                float postBoilTemp = Math.max(
                        elem.getBoilingPoint(),
                        temp - K.BOIL_LATENT_HEAT_CONSUMPTION
                );
                callback.onPhaseChange(i, postBoilTemp);
            } else if (elem.isSolid() && canMelt) {
                float postMeltTemp = Math.max(
                        elem.getMeltingPoint(),
                        temp - K.MELT_LATENT_HEAT_CONSUMPTION
                );
                callback.onPhaseChange(i, postMeltTemp);
            }
        }
    }

    private float computeDeltaTemp(ElementID sourceElem, float sourceTemp,
                                   byte[] elements, float[] temps,
                                   int nx, int ny, int width, int height) {
        if (nx < 0 || nx >= width || ny < 0 || ny >= height) {
            float k = sourceElem.getConductivity() *
                    K.BOUNDS_CONDUCTIVITY_FACTOR;
            return (ambientTemp - sourceTemp) * k;
        }

        int nIndex = nx + ny * width;
        ElementID targetElem = ElementID.fromId(elements[nIndex]);

        if (targetElem == ElementID.EMPTY) {
            float k = sourceElem.getConductivity() *
                    K.EMPTY_CONDUCTIVITY_FACTOR;
            return (ambientTemp - sourceTemp) * k;
        }

        float targetTemp = temps[nIndex];
        float tempDiff = targetTemp - sourceTemp;

        if (Math.abs(tempDiff) < K.MIN_TEMP_DIFF) {
            return 0.0f;
        }

        float avgConductivity = (sourceElem.getConductivity() +
                targetElem.getConductivity()) *
                K.CONDUCTIVITY_AVG_FACTOR;

        float capacityRatio = targetElem.getHeatCapacity() /
                (sourceElem.getHeatCapacity() +
                        targetElem.getHeatCapacity());

        float rawDelta = tempDiff * avgConductivity * capacityRatio;
        float maxAllowedDelta = tempDiff * K.MAX_DELTA_RATIO;

        if (tempDiff > 0) {
            return Math.min(rawDelta, maxAllowedDelta);
        } else {
            return Math.max(rawDelta, maxAllowedDelta);
        }
    }

    public float getAmbientTemp() {
        return ambientTemp;
    }

    public void setAmbientTemp(float ambientTemp) {
        this.ambientTemp = ambientTemp;
    }

    public float getSimulationSpeed() {
        return simulationSpeed;
    }

    public void setSimulationSpeed(float simulationSpeed) {
        this.simulationSpeed = simulationSpeed;
    }
}