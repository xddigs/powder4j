package org.p4j.sys;

import org.p4j.data.ElementID;

/**
 * Engine responsible for thermodynamic simulation:
 * - Conductive heat transfer between neighboring cells.
 * - Ambient thermal exchange.
 * - Evaluation and triggering of phase changes (melting and boiling).
 */
public class ThermoEngine {

    @FunctionalInterface
    public interface PhaseChangeCallback {
        void onPhaseChange(int index, ElementID newElement, float currentTemp);
    }

    private float ambientTemp = 20.0f;
    private final float ambientLossRate = 0.0005f;
    private float simulationSpeed = 0.2f;

    public ThermoEngine() {}

    public ThermoEngine(float ambientTemp, float simulationSpeed) {
        this.ambientTemp = ambientTemp;
        this.simulationSpeed = simulationSpeed;
    }

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
                float heatFlow = 0.0f;

                heatFlow += computeFlow(currentElem, currentTemp,
                        elements, temps, x + 1, y, width, height);
                heatFlow += computeFlow(currentElem, currentTemp,
                        elements, temps, x - 1, y, width, height);
                heatFlow += computeFlow(currentElem, currentTemp,
                        elements, temps, x, y + 1, width, height);
                heatFlow += computeFlow(currentElem, currentTemp,
                        elements, temps, x, y - 1, width, height);

                float deltaTemp = (heatFlow * simulationSpeed) / currentElem.getHeatCapacity();
                float newTemp = currentTemp + deltaTemp;

                newTemp += (ambientTemp - newTemp) * ambientLossRate;
                nextTemps[index] = newTemp;
            }
        }

        for (int i = 0; i < totalCells; i++) {
            ElementID elem = ElementID.fromId(elements[i]);
            if (elem == ElementID.EMPTY) continue;
            float temp = nextTemps[i];

            if (temp >= elem.getBoilingPoint() && elem.getBoilInto() != null) {
                callback.onPhaseChange(i, elem.getBoilInto(), temp);
            } else if (temp >= elem.getMeltingPoint() && elem.getMeltTo() != null) {
                callback.onPhaseChange(i, elem.getMeltTo(), temp);
            }
        }
    }

    private float computeFlow(ElementID sourceElem, float sourceTemp,
                              byte[] elements, float[] temps,
                              int nx, int ny, int width, int height) {
        if (nx < 0 || nx >= width || ny < 0 || ny >= height) {
            float k = sourceElem.getConductivity() * 0.05f;
            return k * (ambientTemp - sourceTemp);
        }

        int nIndex = nx + ny * width;
        ElementID targetElem = ElementID.fromId(elements[nIndex]);

        if (targetElem == ElementID.EMPTY) {
            float k = sourceElem.getConductivity() * 0.02f;
            return k * (ambientTemp - sourceTemp);
        }

        float targetTemp = temps[nIndex];
        float avgConductivity = (sourceElem.getConductivity() +
                targetElem.getConductivity()) * 0.5f;

        return avgConductivity * (targetTemp - sourceTemp);
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