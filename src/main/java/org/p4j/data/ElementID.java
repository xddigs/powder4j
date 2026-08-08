package org.p4j.data;

/**
 * Defines the distinct types of elements available within the simulation,
 * including all 118 chemical elements of the periodic table (Z = 1 to 118),
 * alongside custom compounds, mixtures, smokes, wood, bedrock, and specialized blocks.
 */
public enum ElementID {
    EMPTY((short) 0, "Air", "O2", 0xFF0B0E14, false, 0, false, false, false, false, false, 0),
    FIRE((short) 1, "Fire", "Q", 0xFFE06C75, true, 0, false, false, false, true, false, 0),

    ALCOHOL((short) 2, "Alcohol", "EtOH", 0xCC70A5FA, true, 1, true, false, false, false, true, 6),
    GASOLINE((short) 3, "Gasoline", "C8H18", 0xFFD4A373, true, 1, true, true, false, false, true, 5),
    OIL((short) 4, "Oil", "CnHm", 0xFF8A9A20, true, 1, true, true, false, false, true, 4),
    WATER((short) 5, "Water", "H2O", 0xFF4FA6ED, true, 2, false, false, true, false, true, 5),
    ACID((short) 6, "Acid", "HCl", 0xFF2ECC71, true, 2, false, false, false, false, true, 5),

    HONEY((short) 7, "Honey", "C6H12O6", 0xFFEB9816, true, 3, true, false, false, false, true, 1),
    LAVA((short) 8, "Lava", "SiO2+", 0xFFFF4500, true, 3, false, false, false, true, true, 2),
    NITROGLYCERIN((short) 9, "Nitroglycerin", "C3H5N3O9", 0xFFD63031, true, 3, true, true, false, false, true, 4),
    MERCURY((short) 10, "Mercury", "Hg", 0xFFCFD8DC, true, 6, false, false, false, false, true, 1),

    ASH((short) 11, "Ash", "C/Si", 0xFF9E9E9E, true, 2, false, false, false, false, false, 0),
    METHANE((short) 12, "Methane", "CH4", 0x8800FFAA, true, -2, true, false, false, false, false, 0),
    SALT((short) 13, "Salt", "NaCl", 0xFFF5F5F5, true, 3, false, false, false, false, false, 0),
    SAND((short) 14, "Sand", "SiO2", 0xFFE5C07B, true, 3, false, true, false, false, false, 0),
    DIRT((short) 15, "Dirt", "Soil", 0xFF5D4037, true, 3, false, true, false, false, false, 0),
    SEED((short) 16, "Seed", "Sd", 0xFF8BC34A, true, 3, true, true, false, false, false, 0),
    GUNPOWDER((short) 17, "Gunpowder", "KNO3", 0xFF53565A, true, 3, true, true, false, false, false, 0),
    GRAVEL((short) 18, "Gravel", "Gr", 0xFF9B8773, true, 3, false, true, false, false, false, 0),
    SODIUM((short) 19, "Sodium", "Na", 0xFFD1D5DB, true, 3, true, true, false, false, false, 0),

    SILICON((short) 20, "Silicon", "Si", 0xFF5C6BC0, true, 4, false, false, false, false, false, 0),
    CEMENT((short) 21, "Cement", "CaCO3", 0xFF9E9E9E, true, 4, false, false, false, false, false, 0),
    THERMITE((short) 22, "Thermite", "Fe+Al", 0xFFB71C1C, true, 4, true, false, false, false, false, 0),
    WET_SAND((short) 23, "Wet Sand", "SiO2", 0xFF9E753B, true, 4, false, true, false, false, false, 0),
    DENSE_POWDER((short) 24, "Iron Powder", "Fe", 0xFF4A4A4A, true, 5, false, true, false, false, false, 0),

    WOOD((short) 25, "Wood", "C6H10O5", 0xFF8B5A2B, true, 4, true, true, false, false, false, 0),
    MUD((short) 26, "Mud", "SiO2+H2O", 0xFF3E2723, true, 4, true, true, false, false, true, 1),
    ICE((short) 27, "Ice", "H2O(s)", 0xFFA5F2F3, true, 4, false, true, false, false, false, 0),
    STONE((short) 28, "Stone", "ST", 0xFF808080, true, 4, false, false, false, false, false, 0),
    GRASS((short) 29, "Grass", "G", 0xFF4CAF50, true, 4, true, true, false, false, false, 0),
    CARBON((short) 30, "Carbon", "C", 0xFF222222, true, 4, true, false, false, false, false, 0),
    GLASS((short) 31, "Glass", "SiO2", 0x80E0F7FA, true, 5, false, false, false, false, false, 0),
    OBSIDIAN((short) 32, "Obsidian", "SiO2+", 0xFF1C1326, true, 5, false, false, false, false, false, 0),
    TNT((short) 33, "TNT", "C7H5N3O6", 0xFFC23616, true, 1, true, true, false, false, false, 0),
    BEDROCK((short) 34, "Bedrock", "BR", 0xFF111111, true, 100, false, false, false, false, false, 0),

    STEAM((short) 35, "Steam", "H2O(g)", 0xCCFFFFFF, true, -1, false, false, false, false, false, 0),
    SMOKE_DARK((short) 36, "Dark Smoke", "C", 0xFF3E4451, false, -1, false, false, false, false, false, 0),
    SMOKE_LIGHT((short) 37, "Light Smoke", "H2O(g)", 0xFFDCDFE4, false, -1, false, false, false, false, false, 0),
    SMOKE_GRAY((short) 38, "Smoke", "CO2", 0xFFABB2BF, false, -1, false, false, false, false, false, 0),
    CHLORINE((short) 39, "Chlorine", "Cl2", 0xFF88FF00, true, -1, false, true, false, false, false, 0),
    HYDROGEN((short) 40, "Hydrogen", "H2", 0x88E0F2FE, true, -3, true, false, false, false, false, 0),
    HELIUM((short) 41, "Helium", "He", 0x88FFEEFF, true, -3, false, false, false, false, false, 0),
    PLASMA((short) 42, "Plasma", "P+", 0xFFFF00FF, true, -4, false, false, false, true, false, 0);

    private final short id;
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

    private static final ElementID[] BY_ID;

    static {
        int maxId = 0;
        for (ElementID element : values()) {
            if ((element.id & 0xFFFF) > maxId) {
                maxId = element.id & 0xFFFF;
            }
        }

        BY_ID = new ElementID[maxId + 1];

        for (ElementID element : values()) {
            BY_ID[element.id & 0xFFFF] = element;
        }
    }

    ElementID(short id, String name, String symbol, int colorArgb, boolean isSelectable,
              int density, boolean isFlammable, boolean isCorrosible, boolean isWater,
              boolean isHot, boolean isLiquid, int dispersionRate) {
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

    public short getId() {
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

    public boolean isGas() {
        return density < 0;
    }

    public boolean isPowder() {
        return this == SAND || this == SILICON || this == DIRT ||
                this == SEED || this == SALT || this == SODIUM ||
                this == CEMENT || this == GUNPOWDER || this == THERMITE ||
                this == GRAVEL || this == WET_SAND || this == ASH ||
                this == DENSE_POWDER;
    }

    public boolean isSolid() {
        return !isLiquid && !isGas() && id != EMPTY.id && id != FIRE.id;
    }

    public int getDispersionRate() {
        return dispersionRate;
    }

    public static ElementID fromId(short id) {
        int index = id & 0xFFFF;
        if (index >= BY_ID.length || BY_ID[index] == null) {
            return EMPTY;
        }
        return BY_ID[index];
    }

    public static ElementID fromId(byte id) {
        return fromId((short) (id & 0xFF));
    }
}