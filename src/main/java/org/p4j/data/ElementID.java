package org.p4j.data;

/**
 * Defines the distinct types of elements available within the simulation.
 * Pure periodic table elements and thermal catalysts are selectable.
 * Compounds, organic materials, and mixtures must be crafted or generated via reactions.
 */
public enum ElementID {
    EMPTY((byte) 0, "Air", "N2+O2", 0xFF25273B, false, 0, false, false, false, false, State.NONE, false, 0, 20.0f, Float.MAX_VALUE, Float.MAX_VALUE, 1.00f, 0.02f),
    HYDROGEN((byte) 1, "Hydrogen", "H2", 0x88E0F2FE, true, -3, true, false, false, false, State.GAS, false, 0, 20.0f, -259.1f, -252.8f, 14.30f, 0.18f),
    CARBON_DIOXIDE((byte) 2, "Carbon Dioxide", "CO2", 0xAA7F8C8D, false, -1, false, false, false, false, State.GAS, false, 0, 20.0f, -78.5f, Float.MAX_VALUE, 0.84f, 0.015f),
    SODIUM((byte) 3, "Sodium", "Na", 0xFFD1D5DB, true, 3, false, true, false, false, State.SOLID, true, 0, 20.0f, 97.8f, 883.0f, 1.23f, 0.80f),
    CHLORINE((byte) 4, "Chlorine", "Cl2", 0xFF88FF00, true, 1, false, false, false, false, State.LIQUID, false, 4, 20.0f, -101.5f, -34.0f, 0.48f, 0.01f),
    MERCURY((byte) 5, "Mercury", "Hg", 0xFFCFD8DC, false, 5, false, false, false, false, State.LIQUID, false, 1, 20.0f, -38.8f, 356.7f, 0.14f, 0.25f),
    WATER((byte) 6, "Water", "H2O", 0xFF4FA6ED, false, 2, false, false, true, false, State.LIQUID, false, 5, 20.0f, Float.NEGATIVE_INFINITY, 100.0f, 4.18f, 0.60f),
    ICE((byte) 7, "Ice", "H2O(s)", 0xFFA5F2F3, false, 4, false, true, false, false, State.SOLID, false, 0, -10.0f, 0.0f, Float.MAX_VALUE, 2.09f, 0.70f),
    STEAM((byte) 8, "Steam", "H2O(g)", 0xCCFFFFFF, false, -1, false, false, false, false, State.GAS, false, 0, 120.0f, Float.MAX_VALUE, Float.MAX_VALUE, 2.01f, 0.03f),
    CARBON_MONOXIDE((byte) 9, "Carbon Monoxide", "CO", 0x88555555, false, -2, true, false, false, false, State.GAS, false, 0, 20.0f, -205.0f, -191.5f, 1.04f, 0.02f),
    ASH((byte) 10, "Ash", "C/Ash", 0xFF5C5C5C, false, 2, false, false, false, false, State.SOLID, true, 0, 20.0f, 1200.0f, Float.MAX_VALUE, 0.80f, 0.10f),
    ACID((byte) 11, "Acid", "HCl", 0xFF2ECC71, false, 2, false, false, false, false, State.LIQUID, false, 5, 20.0f, -30.0f, 108.0f, 3.10f, 0.50f),
    SALT((byte) 12, "Salt", "NaCl", 0xFFF5F5F5, false, 3, false, false, false, false, State.SOLID, true, 0, 20.0f, 801.0f, 1465.0f, 0.85f, 0.20f),
    SAND((byte) 13, "Sand", "SiO2", 0xFFE5C07B, false, 3, false, true, false, false, State.SOLID, true, 0, 20.0f, 1700.0f, Float.MAX_VALUE, 0.80f, 0.25f),
    GLASS((byte) 14, "Glass", "SiO2", 0x80E0F7FA, false, 5, false, false, false, false, State.SOLID, false, 0, 20.0f, 1400.0f, Float.MAX_VALUE, 0.84f, 0.80f),
    LAVA((byte) 15, "Lava", "SiO2(l)", 0xFFFF4500, false, 3, false, false, false, true, State.LIQUID, false, 2, 1200.0f, Float.MAX_VALUE, Float.MAX_VALUE, 1.00f, 0.60f),
    OBSIDIAN((byte) 16, "Obsidian", "SiO2", 0xFF1C1326, false, 5, false, false, false, false, State.SOLID, false, 0, 20.0f, 1100.0f, Float.MAX_VALUE, 0.84f, 0.30f),
    CEMENT((byte) 17, "Cement", "CaCO3", 0xFF9E9E9E, false, 4, false, false, false, false, State.SOLID, true, 0, 20.0f, 1339.0f, Float.MAX_VALUE, 0.88f, 0.20f),
    METHANE((byte) 18, "Methane", "CH4", 0x8800FFAA, false, -2, true, false, false, false, State.GAS, false, 0, 20.0f, -182.5f, -161.5f, 2.22f, 0.03f),
    TNT((byte) 19, "TNT", "C7H5N3O6", 0xFFC23616, false, 1, true, true, false, false, State.SOLID, false, 0, 20.0f, 80.0f, Float.MAX_VALUE, 1.38f, 0.15f),
    GASOLINE((byte) 20, "Gasoline", "C8H18", 0xFFD4A373, false, 1, true, true, false, false, State.LIQUID, false, 5, 20.0f, -57.0f, 95.0f, 2.22f, 0.15f),
    OIL((byte) 21, "Olive Oil", "C18H34O2", 0xFF8A9A20, false, 1, false, true, false, false, State.LIQUID, false, 4, 20.0f, -6.0f, 300.0f, 2.00f, 0.17f),
    WOOD((byte) 22, "Wood", "C6H10O5", 0xFF8B5A2B, false, 4, true, true, false, false, State.SOLID, false, 0, 20.0f, 300.0f, Float.MAX_VALUE, 1.70f, 0.12f),
    MUD((byte) 23, "Mud", "SiO2+H2O", 0xFF3E2723, false, 4, true, true, false, false, State.LIQUID, false, 1, 20.0f, Float.MAX_VALUE, 100.0f, 2.50f, 0.45f),
    SEED((byte) 24, "Seed", "Sd", 0xFF8BC34A, true, 3, true, true, false, false, State.SOLID, true, 0, 20.0f, 250.0f, Float.MAX_VALUE, 1.50f, 0.15f),
    THERMITE((byte) 25, "Thermite", "Fe2O3+Al", 0xFFB71C1C, false, 4, true, false, false, false, State.SOLID, true, 0, 20.0f, 1600.0f, Float.MAX_VALUE, 0.75f, 0.40f),
    GUNPOWDER((byte) 26, "Gunpowder", "KNO3+S+C", 0xFF53565A, false, 3, true, true, false, false, State.SOLID, true, 0, 20.0f, 300.0f, Float.MAX_VALUE, 0.92f, 0.20f),
    STONE((byte) 27, "Stone", "SiO2", 0xFF808080, false, 4, false, false, false, false, State.SOLID, false, 0, 20.0f, 1200.0f, Float.MAX_VALUE, 0.84f, 0.50f),
    GRAVEL((byte) 28, "Gravel", "SiO2", 0xFF9B8773, false, 3, false, true, false, false, State.SOLID, false, 0, 20.0f, 1200.0f, Float.MAX_VALUE, 0.84f, 0.40f),
    DIRT((byte) 29, "Dirt", "CSi+H2O", 0xFF5D4037, false, 3, false, true, false, false, State.SOLID, true, 0, 20.0f, 1100.0f, Float.MAX_VALUE, 1.00f, 0.35f),
    WET_SAND((byte) 30, "Wet Sand", "SiO2+H2O", 0xFF9E753B, false, 4, false, true, false, false, State.SOLID, false, 0, 20.0f, 1700.0f, 100.0f, 2.10f, 0.50f),
    FIRE((byte) 31, "Fire", "Q", 0xFFFF5722, true, 0, false, false, false, true, State.NONE, false, 0, 800.0f, Float.MAX_VALUE, Float.MAX_VALUE, 0.10f, 0.90f),
    GRASS((byte) 32, "Grass", "C6H10O5", 0xFF4CAF50, false, 4, true, true, false, false, State.SOLID, false, 0, 20.0f, 200.0f, Float.MAX_VALUE, 1.60f, 0.12f),
    SILICON((byte) 33, "Silicon", "Si", 0xFF5C6BC0, true, 4, false, true, false, false, State.SOLID, true, 0, 20.0f, 1414.0f, Float.MAX_VALUE, 0.71f, 0.85f),
    SULFUR((byte) 34, "Sulfur", "S", 0xFFFFEB3B, true, 3, true, false, false, false, State.SOLID, true, 0, 20.0f, 115.2f, 444.6f, 0.71f, 0.20f),
    BLACK_POWDER((byte) 35, "Black Powder", "S+C", 0xFF4A442D, false, 3, true, false, false, false, State.SOLID, true, 0, 20.0f, 250.0f, Float.MAX_VALUE, 0.71f, 0.30f),
    CARBON((byte) 36, "Carbon", "C", 0xFF222222, true, 3, true, false, false, false, State.SOLID, true, 0, 20.0f, 3550.0f, Float.MAX_VALUE, 0.71f, 0.60f),
    OXYGEN((byte) 37, "Oxygen", "O2", 0x88B0E0E6, true, -2, false, false, false, false, State.GAS, false, 3, 20.0f, -218.8f, -183.0f, 0.92f, 0.02f),
    IRON((byte) 38, "Iron", "Fe", 0xFF795548, true, 4, false, false, false, false, State.SOLID, false, 0, 20.0f, 1538.0f, Float.MAX_VALUE, 0.45f, 0.80f),
    NITROGEN((byte) 39, "Nitrogen", "N2", 0x88A0C4FF, true, -2, false, false, false, false, State.GAS, false, 0, 20.0f, -210.0f, -195.8f, 1.04f, 0.02f),
    STEEL((byte) 40, "Steel", "Fe+C", 0xFF708090, true, 4, false, true, false, false, State.SOLID, false, 0, 20.0f, 1400.0f, Float.MAX_VALUE, 0.49f, 0.75f),
    ALUMINUM((byte) 41, "Aluminum", "Al", 0xFFD4D8DD, false, 3, false, false, false, false, State.SOLID, false, 0, 20.0f, 660.3f, 2470.0f, 0.90f, 0.88f),
    COPPER((byte) 42, "Copper", "Cu", 0xFFB87333, true, 4, false, true, false, false, State.SOLID, false, 0, 20.0f, 1085.0f, 2562.0f, 0.39f, 0.98f),
    COPPER_OXIDIZED((byte) 43, "Oxidized Copper", "Cu2O", 0xFF43B3AE, false, 4, false, false, false, false, State.SOLID, false, 0, 20.0f, 1235.0f, Float.MAX_VALUE, 0.51f, 0.20f);

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
    private final State state;
    private final boolean isPowder;
    private final int dispersionRate;

    private final float defaultTemp;
    private final float meltingPoint;
    private final float boilingPoint;
    private final float heatCapacity;
    private final float conductivity;

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
              boolean isHot, State state, boolean isPowder,
              int dispersionRate, float defaultTemp, float meltingPoint, float boilingPoint,
              float heatCapacity, float conductivity) {
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
        this.state = state;
        this.isPowder = isPowder;
        this.dispersionRate = dispersionRate;

        this.defaultTemp = defaultTemp;
        this.meltingPoint = meltingPoint;
        this.boilingPoint = boilingPoint;
        this.heatCapacity = heatCapacity;
        this.conductivity = conductivity;
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

    public State getState() {
        return state;
    }

    public boolean isLiquid() {
        return state == State.LIQUID;
    }

    public boolean isGas() {
        return state == State.GAS;
    }

    public boolean isSolid() {
        return state == State.SOLID;
    }

    public boolean isBlock() {
        return state == State.SOLID && !isPowder;
    }

    public boolean isPowder() {
        return isPowder;
    }

    public int getDispersionRate() {
        return dispersionRate;
    }

    public float getDefaultTemp() {
        return defaultTemp;
    }

    public float getMeltingPoint() {
        return meltingPoint;
    }

    public float getBoilingPoint() {
        return boilingPoint;
    }

    public float getHeatCapacity() {
        return heatCapacity;
    }

    public float getConductivity() {
        return conductivity;
    }

    public static ElementID fromId(byte id) {
        int index = id & 0xFF;
        if (index >= BY_ID.length || BY_ID[index] == null) {
            return EMPTY;
        }
        return BY_ID[index];
    }
}