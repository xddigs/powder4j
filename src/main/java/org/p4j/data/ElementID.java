package org.p4j.data;

/**
 * Defines the distinct types of elements available within the simulation.
 * Each element possesses unique properties, such as its identifier,
 * descriptive name, chemical symbol, visual color representation, and UI selectability.
 */
public enum ElementID {
    EMPTY((byte) 0, "Air", "O2", 0xFF0B0E14, false, 0, false, false, false, false, false, 0),
    STONE((byte) 1, "Stone", "ST", 0xFF808080, true, 4, false, false, false, false, false, 0),
    SAND((byte) 2, "Sand", "SiO2", 0xFFE5C07B, true, 3, false, true, false, false, false, 0),
    GRAVEL((byte) 3, "Gravel", "Gr", 0xFF9B8773, true, 3, false, true, false, false, false, 0),
    WATER((byte) 4, "Water", "H2O", 0xFF4FA6ED, true, 2, false, false, true, false, true, 5),
    FIRE((byte) 5, "Fire", "Q", 0xFFE06C75, true, 0, false, false, false, true, false, 0),
    OIL((byte) 6, "Oil", "CnHm", 0xFF8A9A20, true, 1, true, true, false, false, true, 4),
    SMOKE_DARK((byte) 7, "Dark Smoke", "C", 0xFF3E4451, false, -1, false, false, false, false, false, 0),
    SMOKE_GRAY((byte) 8, "Smoke", "CO2", 0xFFABB2BF, false, -1, false, false, false, false, false, 0),
    SMOKE_LIGHT((byte) 9, "Light Smoke", "H2O(g)", 0xFFDCDFE4, false, -1, false, false, false, false, false, 0),
    LAVA((byte) 10, "Lava", "SiO2+", 0xFFFF4500, true, 3, false, false, false, true, true, 2),
    ACID((byte) 11, "Acid", "HCl", 0xFF2ECC71, true, 2, false, false, false, false, true, 5),
    WOOD((byte) 12, "Wood", "C6H10O5", 0xFF8B5A2B, true, 4, true, true, false, false, false, 0),
    GUNPOWDER((byte) 13, "Gunpowder", "KNO3", 0xFF53565A, true, 3, true, true, false, false, false, 0),
    SODIUM((byte) 14, "Sodium", "Na", 0xFFD1D5DB, true, 3, false, true, false, false, false, 0),
    ICE((byte) 15, "Ice", "H2O(s)", 0xFFA5F2F3, true, 4, false, true, false, false, false, 0),
    GASOLINE((byte) 16, "Gasoline", "C8H18", 0xFFD4A373, true, 1, true, true, false, false, true, 5),
    TNT((byte) 17, "TNT", "C2H4", 0xFFC23616, false, 1, true, true, false, false, false, 0),
    MERCURY((byte) 18, "Mercury", "Hg", 0xFFCFD8DC, true, 5, false, false, false, false, true, 1),
    CHLORINE((byte) 19, "Chlorine", "Cl2", 0xFF88FF00, false, 1, false, false, false, false, false, 0),
    SALT((byte) 20, "Salt", "NaCl", 0xFFF5F5F5, true, 3, false, false, false, false, false, 0),
    METHANE((byte) 21, "Methane", "CH4", 0x8800FFaa, true, -2, true, false, false, false, false, 0),
    STEAM((byte) 22, "Steam", "H2O(g)", 0xCCFFFFFF, true, -1, false, false, false, false, false, 0),
    GLASS((byte) 23, "Glass", "SiO2", 0x80E0F7FA, false, 5, false, false, false, false, false, 0),
    CEMENT((byte) 24, "Cement", "CaCO3", 0xFF9E9E9E, true, 4, false, false, false, false, false, 0),
    PLANT((byte) 25, "Plant", "C6H10O5", 0xFF2E7D32, true, 4, true, true, false, false, false, 0),
    THERMITE((byte) 26, "Thermite", "Fe+Al", 0xFFB71C1C, true, 4, true, false, false, false, false, 0),
    OBSIDIAN((byte) 27, "Obsidian", "SiO2+", 0xFF1C1326, false, 5, false, false, false, false, false, 0),
    WET_SAND((byte) 28, "Wet Sand", "SiO2", 0xFF9E753B, true, 4, false, true, false, false, false, 0);

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
    private final boolean isLiquid;
    private final int dispersionRate;
    private static final ElementID[] BY_ID = values();

    ElementID(byte id, String name, String symbol, int colorArgb, boolean isSelectable,
              int density, boolean isFlammable, boolean isCorrosible, boolean isWater, boolean isHot,
              boolean isLiquid, int dispersionRate) {
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
        this.isLiquid = isLiquid;
        this.dispersionRate = dispersionRate;
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

    public boolean isLiquid() {
        return isLiquid;
    }

    public int getDispersionRate() {
        return dispersionRate;
    }

    public static ElementID fromId(byte id) {
        if (id < 0 || id >= BY_ID.length) return EMPTY;
        return BY_ID[id];
    }
}