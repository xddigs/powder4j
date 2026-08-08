package org.p4j.data;

/**
 * Defines the distinct types of elements available within the simulation.
 * Reorganized by atomic complexity: Pure Elements (sorted by Z), Inorganic Compounds,
 * Organics/Hydrocarbons, Complex Mixtures & Minerals, and Energy States.
 */
public enum ElementID {
    EMPTY((byte) 0, "Air", "O2", 0xFF0B0E14, false, 0, false, false, false, false, false, 0),
    SMOKE_DARK((byte) 1, "Dark Smoke", "C", 0xFF3E4451, false, -1, false, false, false, false, false, 0),
    SODIUM((byte) 2, "Sodium", "Na", 0xFFD1D5DB, false, 3, false, true, false, false, false, 0),
    CHLORINE((byte) 3, "Chlorine", "Cl2", 0xFF88FF00, false, 1, false, false, false, false, false, 0),
    MERCURY((byte) 4, "Mercury", "Hg", 0xFFCFD8DC, false, 5, false, false, false, false, true, 1),
    WATER((byte) 5, "Water", "H2O", 0xFF4FA6ED, true, 2, false, false, true, false, true, 5),
    ICE((byte) 6, "Ice", "H2O(s)", 0xFFA5F2F3, false, 4, false, true, false, false, false, 0),
    STEAM((byte) 7, "Steam", "H2O(g)", 0xCCFFFFFF, false, -1, false, false, false, false, false, 0),
    SMOKE_LIGHT((byte) 8, "Light Smoke", "H2O(g)", 0xFFDCDFE4, false, -1, false, false, false, false, false, 0),
    SMOKE_GRAY((byte) 9, "Smoke", "CO2", 0xFFABB2BF, false, -1, false, false, false, false, false, 0),
    ACID((byte) 10, "Acid", "HCl", 0xFF2ECC71, false, 2, false, false, false, false, true, 5),
    SALT((byte) 11, "Salt", "NaCl", 0xFFF5F5F5, false, 3, false, false, false, false, false, 0),
    SAND((byte) 12, "Sand", "SiO2", 0xFFE5C07B, true, 3, false, true, false, false, false, 0),
    GLASS((byte) 13, "Glass", "SiO2", 0x80E0F7FA, false, 5, false, false, false, false, false, 0),
    LAVA((byte) 14, "Lava", "SiO2+", 0xFFFF4500, false, 3, false, false, false, true, true, 2),
    OBSIDIAN((byte) 15, "Obsidian", "SiO2+", 0xFF1C1326, false, 5, false, false, false, false, false, 0),
    CEMENT((byte) 16, "Cement", "CaCO3", 0xFF9E9E9E, false, 4, false, false, false, false, false, 0),
    METHANE((byte) 17, "Methane", "CH4", 0x8800FFAA, false, -2, true, false, false, false, false, 0),
    TNT((byte) 18, "TNT", "C2H4", 0xFFC23616, false, 1, true, true, false, false, false, 0),
    GASOLINE((byte) 19, "Gasoline", "C8H18", 0xFFD4A373, false, 1, true, true, false, false, true, 5),
    OIL((byte) 20, "Oil", "CnHm", 0xFF8A9A20, true, 1, true, true, false, false, true, 4),
    WOOD((byte) 21, "Wood", "C6H10O5", 0xFF8B5A2B, false, 4, true, true, false, false, false, 0),
    MUD((byte) 22, "Mud", "SiO2+H2O", 0xFF3E2723, false, 4, true, true, false, false, true, 1),
    SEED((byte) 23, "Seed", "Sd", 0xFF8BC34A, true, 3, true, true, false, false, false, 0),
    THERMITE((byte) 24, "Thermite", "Fe+Al", 0xFFB71C1C, false, 4, true, false, false, false, false, 0),
    GUNPOWDER((byte) 25, "Gunpowder", "KNO3", 0xFF53565A, false, 3, true, true, false, false, false, 0),
    STONE((byte) 26, "Stone", "ST", 0xFF808080, true, 4, false, false, false, false, false, 0),
    GRAVEL((byte) 27, "Gravel", "Gr", 0xFF9B8773, false, 3, false, true, false, false, false, 0),
    DIRT((byte) 28, "Dirt", "Soil", 0xFF5D4037, true, 3, false, true, false, false, false, 0),
    WET_SAND((byte) 29, "Wet Sand", "SiO2", 0xFF9E753B, false, 4, false, true, false, false, false, 0),
    FIRE((byte) 30, "Fire", "Q", 0xFFE06C75, true, 0, false, false, false, true, false, 0),
    GRASS((byte) 31, "Grass", "G", 0xFF4CAF50, false, 4, true, true, false, false, false, 0),
    SILICON((byte) 32, "Silicon", "Si", 0xFF5C6BC0, true, 4, false, true, false, false, false, 0);

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