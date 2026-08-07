package org.xdg.p4j.data;

/**
 * Defines the distinct types of elements available within the simulation.
 * Each element possesses unique properties, such as its identifier,
 * descriptive name, and visual color representation.
 */
public enum ElementID {
    EMPTY((byte) 0, "Air", 0xFF0B0E14),
    WALL((byte) 1, "Stone", 0xFF808080),
    SAND((byte) 2, "Sand", 0xFFE5C07B),
    GRAVEL((byte) 3, "Gravel", 0xFF9B8773),
    WATER((byte) 4, "Water", 0xFF4FA6ED),
    FIRE((byte) 5, "Fire", 0xFFE06C75),
    OIL((byte) 6, "Oil", 0xFF8A9A20),
    SMOKE_DARK((byte) 7, "Dark Smoke", 0xFF3E4451),
    SMOKE_GRAY((byte) 8, "Smoke", 0xFFABB2BF),
    SMOKE_LIGHT((byte) 9, "Light Smoke", 0xFFDCDFE4),
    LAVA((byte) 10, "Lava", 0xFFFF4500),
    ACID((byte) 11, "Acid", 0xFF2ECC71),
    WOOD((byte) 12, "Wood", 0xFF8B5A2B),
    GUNPOWDER((byte) 13, "Gunpowder", 0xFF53565A),
    SODIUM((byte) 14, "Sodium", 0xFFD1D5DB),
    ICE((byte) 15, "Ice", 0xFFA5F2F3);

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