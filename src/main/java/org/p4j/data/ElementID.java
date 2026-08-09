package org.p4j.data;

/**
 * Defines the distinct types of elements available within the simulation.
 * Pure periodic table elements and thermal catalysts are selectable.
 * Compounds, organic materials, and mixtures must be crafted or generated via reactions.
 */
public enum ElementID {
    EMPTY((byte) 0, "Air", "N2+O2", 0xFF0B0E14, false, 0, false, false, false, false, false, 0),
    HYDROGEN((byte) 1, "Hydrogen", "H2", 0x88E0F2FE, true, -3, true, false, false, false, false, 0),
    CARBON_DIOXIDE((byte) 2, "Carbon Dioxide", "CO2", 0xAA7F8C8D, false, -1, false, false, false, false, false, 0),
    SODIUM((byte) 3, "Sodium", "Na", 0xFFD1D5DB, true, 3, false, true, false, false, false, 0),
    CHLORINE((byte) 4, "Chlorine", "Cl2", 0xFF88FF00, true, 1, false, false, false, false, true, 4),
    MERCURY((byte) 5, "Mercury", "Hg", 0xFFCFD8DC, false, 5, false, false, false, false, true, 1),
    WATER((byte) 6, "Water", "H2O", 0xFF4FA6ED, false, 2, false, false, true, false, true, 5),
    ICE((byte) 7, "Ice", "H2O(s)", 0xFFA5F2F3, false, 4, false, true, false, false, false, 0),
    STEAM((byte) 8, "Steam", "H2O(g)", 0xCCFFFFFF, false, -1, false, false, false, false, false, 0),
    CARBON_MONOXIDE((byte) 9, "Carbon Monoxide", "CO", 0x88555555, false, -2, true, false, false, false, false, 0),
    ASH((byte) 10, "Ash", "C/Ash", 0xFF5C5C5C, false, 2, false, false, false, false, false, 0),
    ACID((byte) 11, "Acid", "HCl", 0xFF2ECC71, false, 2, false, false, false, false, true, 5),
    SALT((byte) 12, "Salt", "NaCl", 0xFFF5F5F5, false, 3, false, false, false, false, false, 0),
    SAND((byte) 13, "Sand", "SiO2", 0xFFE5C07B, false, 3, false, true, false, false, false, 0),
    GLASS((byte) 14, "Glass", "SiO2", 0x80E0F7FA, false, 5, false, false, false, false, false, 0),
    LAVA((byte) 15, "Lava", "SiO2(l)", 0xFFFF4500, false, 3, false, false, false, true, true, 2),
    OBSIDIAN((byte) 16, "Obsidian", "SiO2", 0xFF1C1326, false, 5, false, false, false, false, false, 0),
    CEMENT((byte) 17, "Cement", "CaCO3", 0xFF9E9E9E, false, 4, false, false, false, false, false, 0),
    METHANE((byte) 18, "Methane", "CH4", 0x8800FFAA, false, -2, true, false, false, false, false, 0),
    TNT((byte) 19, "TNT", "C7H5N3O6", 0xFFC23616, false, 1, true, true, false, false, false, 0),
    GASOLINE((byte) 20, "Gasoline", "C8H18", 0xFFD4A373, false, 1, true, true, false, false, true, 5),
    OIL((byte) 21, "Olive Oil", "C18H34O2", 0xFF8A9A20, false, 1, false, true, false, false, true, 4),
    WOOD((byte) 22, "Wood", "C6H10O5", 0xFF8B5A2B, false, 4, true, true, false, false, false, 0),
    MUD((byte) 23, "Mud", "SiO2+H2O", 0xFF3E2723, false, 4, true, true, false, false, true, 1),
    SEED((byte) 24, "Seed", "Sd", 0xFF8BC34A, true, 3, true, true, false, false, false, 0),
    THERMITE((byte) 25, "Thermite", "Fe2O3+Al", 0xFFB71C1C, false, 4, true, false, false, false, false, 0),
    GUNPOWDER((byte) 26, "Gunpowder", "KNO3+S+C", 0xFF53565A, false, 3, true, true, false, false, false, 0),
    STONE((byte) 27, "Stone", "SiO2", 0xFF808080, false, 4, false, false, false, false, false, 0),
    GRAVEL((byte) 28, "Gravel", "SiO2", 0xFF9B8773, false, 3, false, true, false, false, false, 0),
    DIRT((byte) 29, "Dirt", "Soil", 0xFF5D4037, false, 3, false, true, false, false, false, 0),
    WET_SAND((byte) 30, "Wet Sand", "SiO2+H2O", 0xFF9E753B, false, 4, false, true, false, false, false, 0),
    FIRE((byte) 31, "Fire", "Q", 0xFFFF5722, true, 0, false, false, false, true, false, 0),
    GRASS((byte) 32, "Grass", "C6H10O5", 0xFF4CAF50, false, 4, true, true, false, false, false, 0),
    SILICON((byte) 33, "Silicon", "Si", 0xFF5C6BC0, true, 4, false, true, false, false, false, 0),
    CARBON((byte) 34, "Carbon", "C", 0xFF222222, true, 3, true, false, false, false, false, 0),
    OXYGEN((byte) 35, "Oxygen", "O2", 0x88B0E0E6, true, -2, false, false, false, false, false, 3),
    IRON((byte) 36, "Iron", "Fe", 0xFF795548, true, 4, false, false, false, false, false, 0),
    NITROGEN((byte) 37, "Nitrogen", "N2", 0x88A0C4FF, true, -2, false, false, false, false, false, 0);

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

    private static final ElementID[] BY_ID;

    static {
        int maxId = 0;
        for (ElementID element : values()) {
            if ((element.id & 0xFF) > maxId) {
                maxId = element.id & 0xFF;
            }
        }

        BY_ID = new ElementID[maxId + 1];

        for (ElementID element : values()) {
            BY_ID[element.id & 0xFF] = element;
        }
    }

    ElementID(byte id, String name, String symbol, int colorArgb, boolean isSelectable,
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

    public boolean isGas() {
        return density < 0;
    }

    public boolean isPowder() {
        return this == SAND || this == SILICON || this == DIRT ||
                this == SEED || this == SALT || this == SODIUM ||
                this == CEMENT || this == GUNPOWDER || this == THERMITE ||
                this == CARBON || this == ASH;
    }

    public boolean isSolid() {
        return !isLiquid && !isGas() && id != EMPTY.id && id != FIRE.id;
    }

    public int getDispersionRate() {
        return dispersionRate;
    }

    public static ElementID fromId(byte id) {
        int index = id & 0xFF;
        if (index >= BY_ID.length || BY_ID[index] == null) {
            return EMPTY;
        }
        return BY_ID[index];
    }
}