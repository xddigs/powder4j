package org.xdg.p4j.data;

/**
 * Defines the distinct types of elements available within the simulation.
 * Each element possesses unique properties, such as its identifier,
 * descriptive name, chemical symbol, visual color representation, and UI selectability.
 */
public enum ElementID {
    EMPTY((byte) 0, "Air", "O2", 0xFF0B0E14, false, 0, false, false, false, false),
    STONE((byte) 1, "Stone", "ST", 0xFF808080, true, 4, false, false, false, false),
    SAND((byte) 2, "Sand", "SiO2", 0xFFE5C07B, true, 3, false, true, false, false),
    GRAVEL((byte) 3, "Gravel", "Gr", 0xFF9B8773, true, 3, false, true, false, false),
    WATER((byte) 4, "Water", "H2O", 0xFF4FA6ED, true, 2, false, false, true, false),
    FIRE((byte) 5, "Fire", "Q", 0xFFE06C75, true, 0, false, false, false, true),
    OIL((byte) 6, "Oil", "CnHm", 0xFF8A9A20, true, 1, true, true, false, false),
    SMOKE_DARK((byte) 7, "Dark Smoke", "C", 0xFF3E4451, false, -1, false, false, false, false),
    SMOKE_GRAY((byte) 8, "Smoke", "CO2", 0xFFABB2BF, false, -1, false, false, false, false),
    SMOKE_LIGHT((byte) 9, "Light Smoke", "H2O(g)", 0xFFDCDFE4, false, -1, false, false, false, false),
    LAVA((byte) 10, "Lava", "SiO2+", 0xFFFF4500, true, 3, false, false, false, true),
    ACID((byte) 11, "Acid", "HCl", 0xFF2ECC71, true, 2, false, false, false, false),
    WOOD((byte) 12, "Wood", "C6H10O5", 0xFF8B5A2B, true, 4, true, true, false, false),
    GUNPOWDER((byte) 13, "Gunpowder", "KNO3", 0xFF53565A, true, 3, true, true, false, false),
    SODIUM((byte) 14, "Sodium", "Na", 0xFFD1D5DB, true, 3, false, true, false, false),
    ICE((byte) 15, "Ice", "H2O(s)", 0xFFA5F2F3, true, 4, false, true, false, false),
    GASOLINE((byte) 16, "Gasoline", "C8H18", 0xFFD4A373, true, 1, true , true , false, false),
    TNT((byte) 17, "TNT", "C2H4", 0xFFC23616, false, 1, true, true, false, false),
    MERCURY((byte) 18, "Mercury", "Hg", 0xFFCFD8DC, true, 5, false, false, false, false);

    private final byte id;
    private final String name;
    private final String symbol;
    private final int colorArgb;
    private final boolean isSelectable;
    private final int density;
    private final boolean isFlammable;
    private final boolean isCorrosible;
    private final boolean isWater;
    private final boolean isHot;
    private static final ElementID[] BY_ID = values();

    ElementID(byte id, String name, String symbol, int colorArgb, boolean isSelectable,
              int density, boolean isFlammable, boolean isCorrosible, boolean isWater, boolean isHot) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.colorArgb = colorArgb;
        this.isSelectable = isSelectable;
        this.density = density;
        this.isFlammable = isFlammable;
        this.isCorrosible = isCorrosible;
        this.isWater = isWater;
        this.isHot = isHot;
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

    public int getDensity() {
        return density;
    }

    public boolean isFlammable() {
        return isFlammable;
    }

    public boolean isCorrosible() {
        return isCorrosible;
    }

    public boolean isWater() {
        return isWater;
    }

    public boolean isHot() {
        return isHot;
    }

    public static ElementID fromId(byte id) {
        if (id < 0 || id >= BY_ID.length) return EMPTY;
        return BY_ID[id];
    }
}