package org.xdg.p4j.data;

/**
 * Defines the distinct types of elements available within the simulation.
 * Each element possesses unique properties, such as its identifier,
 * descriptive name, chemical symbol, visual color representation, and UI selectability.
 */
public enum ElementID {
    EMPTY((byte) 0, "Air", "O2", 0xFF0B0E14, false),
    STONE((byte) 1, "Stone", "ST", 0xFF808080, true),
    SAND((byte) 2, "Sand", "SiO2", 0xFFE5C07B, true),
    GRAVEL((byte) 3, "Gravel", "Gr", 0xFF9B8773, true),
    WATER((byte) 4, "Water", "H2O", 0xFF4FA6ED, true),
    FIRE((byte) 5, "Fire", "Q", 0xFFE06C75, true),
    OIL((byte) 6, "Oil", "CnHm", 0xFF8A9A20, true),
    SMOKE_DARK((byte) 7, "Dark Smoke", "C", 0xFF3E4451, false),
    SMOKE_GRAY((byte) 8, "Smoke", "CO2", 0xFFABB2BF, false),
    SMOKE_LIGHT((byte) 9, "Light Smoke", "H2O(g)", 0xFFDCDFE4, false),
    LAVA((byte) 10, "Lava", "SiO2+", 0xFFFF4500, true),
    ACID((byte) 11, "Acid", "HCl", 0xFF2ECC71, true),
    WOOD((byte) 12, "Wood", "C6H10O5", 0xFF8B5A2B, true),
    GUNPOWDER((byte) 13, "Gunpowder", "KNO3", 0xFF53565A, true),
    SODIUM((byte) 14, "Sodium", "Na", 0xFFD1D5DB, true),
    ICE((byte) 15, "Ice", "H2O(s)", 0xFFA5F2F3, true);

    private final byte id;
    private final String name;
    private final String symbol;
    private final int colorArgb;
    private final boolean isSelectable;
    private static final ElementID[] BY_ID = values();

    ElementID(byte id, String name, String symbol, int colorArgb, boolean isSelectable) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.colorArgb = colorArgb;
        this.isSelectable = isSelectable;
    }

    public byte getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getColorArgb() {
        return colorArgb;
    }

    public boolean isSelectable() {
        return isSelectable;
    }

    public static ElementID fromId(byte id) {
        if (id < 0 || id >= BY_ID.length) return EMPTY;
        return BY_ID[id];
    }
}