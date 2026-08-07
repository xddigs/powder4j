package org.xdg.p4j.data;

/**
 * Defines the distinct types of elements available within the simulation.
 * Each element possesses unique properties, such as its identifier,
 * descriptive name, and visual color representation.
 */
public enum ElementID {
    EMPTY((byte) 0, "Air", 0xFF0B0E14),
    WALL ((byte) 1, "Stone", 0xFF808080),
    SAND ((byte) 2, "Sand", 0xFFE5C07B),
    WATER((byte) 3, "Water", 0xFF4FA6ED),
    FIRE ((byte) 4, "Fire", 0xFFE06C75);

    private final byte id;
    private final String name;
    private final int colorArgb;
    private static final ElementID[] BY_ID = values();

    ElementID(byte id, String name, int colorArgb) {
        this.id = id;
        this.name = name;
        this.colorArgb = colorArgb;
    }

    public byte getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getColorArgb() {
        return colorArgb;
    }

    public static ElementID fromId(byte id) {
        if (id < 0 || id >= BY_ID.length) return EMPTY;
        return BY_ID[id];
    }
}