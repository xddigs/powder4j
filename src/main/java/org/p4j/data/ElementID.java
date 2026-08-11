package org.p4j.data;

/**
 * Defines the distinct types of elements available within the simulation.
 * Pure periodic table elements and thermal catalysts are selectable.
 * Compounds, organic materials, and mixtures must be crafted or generated via reactions.
 */
public enum ElementID {
    VOID((byte) 0, "Air", "N2+O2", 0xFF25273B, false, 0, false, false, false, false, false, State.NONE, false, 0, 20.0f, Float.MAX_VALUE, Float.MAX_VALUE, 1.00f, 0.02f, null, null),
    HYDROGEN((byte) 1, "Hydrogen", "H2", 0x88E0F2FE, true, -3, true, false, false, false, false, State.GAS, false, 0, 20.0f, -259.1f, -252.8f, 14.30f, 0.18f, null, null),
    CARBON_DIOXIDE((byte) 2, "Carbon Dioxide", "CO2", 0xAA7F8C8D, false, -1, false, false, false, false, false, State.GAS, false, 0, 20.0f, -78.5f, Float.MAX_VALUE, 0.84f, 0.015f, null, null),
    SODIUM((byte) 3, "Sodium", "Na", 0xFFD1D5DB, true, 3, false, true, false, false, true, State.SOLID, true, 0, 20.0f, 97.8f, 883.0f, 1.23f, 0.80f, "LAVA", null),
    CHLORINE((byte) 4, "Chlorine", "Cl2", 0xFF88FF00, true, 1, false, false, false, false, false, State.LIQUID, false, 4, 20.0f, -101.5f, -34.0f, 0.48f, 0.01f, null, null),
    MERCURY((byte) 5, "Mercury", "Hg", 0xFFCFD8DC, false, 5, false, false, false, false, true, State.LIQUID, false, 1, 20.0f, -38.8f, 356.7f, 0.14f, 0.25f, null, null),
    WATER((byte) 6, "Water", "H2O", 0xFF4FA6ED, false, 2, false, false, true, false, false, State.LIQUID, false, 5, 20.0f, 0.0f, 100.0f, 4.18f, 0.60f, null, "STEAM"),
    ICE((byte) 7, "Ice", "H2O(s)", 0xFFA5F2F3, false, 4, false, true, false, false, false, State.SOLID, false, 0, -10.0f, 0.0f, Float.MAX_VALUE, 2.09f, 0.70f, "WATER", null),
    STEAM((byte) 8, "Steam", "H2O(g)", 0xCCFFFFFF, false, -1, false, false, false, false, false, State.GAS, false, 0, 120.0f, Float.MAX_VALUE, Float.MAX_VALUE, 2.01f, 0.03f, null, null),
    CARBON_MONOXIDE((byte) 9, "Carbon Monoxide", "CO", 0x88555555, false, -2, true, false, false, false, false, State.GAS, false, 0, 20.0f, -205.0f, -191.5f, 1.04f, 0.02f, null, null),
    ASH((byte) 10, "Ash", "C/Ash", 0xFF5C5C5C, false, 2, false, false, false, false, false, State.SOLID, true, 0, 20.0f, 1200.0f, Float.MAX_VALUE, 0.80f, 0.10f, "LAVA", null),
    ACID((byte) 11, "Acid", "HCl", 0xFF2ECC71, false, 2, false, false, false, false, false, State.LIQUID, false, 5, 20.0f, -30.0f, 108.0f, 3.10f, 0.50f, null, null),
    SALT((byte) 12, "Salt", "NaCl", 0xFFF5F5F5, false, 3, false, false, false, false, false, State.SOLID, true, 0, 20.0f, 801.0f, 1465.0f, 0.85f, 0.20f, "LAVA", null),
    SAND((byte) 13, "Sand", "SiO2", 0xFFE5C07B, false, 3, false, true, false, false, false, State.SOLID, true, 0, 20.0f, 1700.0f, Float.MAX_VALUE, 0.80f, 0.25f, "LAVA", null),
    GLASS((byte) 14, "Glass", "SiO2", 0x80E0F7FA, false, 5, false, false, false, false, false, State.SOLID, false, 0, 20.0f, 1400.0f, Float.MAX_VALUE, 0.84f, 0.80f, "LAVA", null),
    LAVA((byte) 15, "Lava", "SiO2(l)", 0xFFFF4500, false, 3, false, false, false, true, false, State.LIQUID, false, 2, 1200.0f, Float.MAX_VALUE, Float.MAX_VALUE, 1.00f, 0.60f, null, null),
    OBSIDIAN((byte) 16, "Obsidian", "SiO2", 0xFF1C1326, false, 5, false, false, false, false, false, State.SOLID, false, 0, 20.0f, 1100.0f, Float.MAX_VALUE, 0.84f, 0.30f, "LAVA", null),
    CEMENT((byte) 17, "Cement", "CaCO3", 0xFF9E9E9E, false, 4, false, false, false, false, false, State.SOLID, true, 0, 20.0f, 1339.0f, Float.MAX_VALUE, 0.88f, 0.20f, "LAVA", null),
    METHANE((byte) 18, "Methane", "CH4", 0x8800FFAA, false, -2, true, false, false, false, false, State.GAS, false, 0, 20.0f, -182.5f, -161.5f, 2.22f, 0.03f, null, null),
    TNT((byte) 19, "TNT", "C7H5N3O6", 0xFFC23616, false, 1, true, true, false, false, false, State.SOLID, false, 0, 20.0f, 80.0f, Float.MAX_VALUE, 1.38f, 0.15f, "CARBON_DIOXIDE", null),
    GASOLINE((byte) 20, "Gasoline", "C8H18", 0xFFD4A373, false, 1, true, true, false, false, false, State.LIQUID, false, 5, 20.0f, -57.0f, 95.0f, 2.22f, 0.15f, null, "METHANE"),
    OIL((byte) 21, "Olive Oil", "C18H34O2", 0xFF8A9A20, false, 1, false, true, false, false, false, State.LIQUID, false, 4, 20.0f, -6.0f, 300.0f, 2.00f, 0.17f, null, "CARBON_MONOXIDE"),
    WOOD((byte) 22, "Wood", "C6H10O5", 0xFF8B5A2B, false, 4, true, true, false, false, false, State.SOLID, false, 0, 20.0f, 300.0f, Float.MAX_VALUE, 1.70f, 0.12f, "CARBON_MONOXIDE", null),
    MUD((byte) 23, "Mud", "SiO2+H2O", 0xFF3E2723, false, 4, true, true, false, false, false, State.LIQUID, false, 1, 20.0f, Float.MAX_VALUE, 100.0f, 2.50f, 0.45f, null, "DIRT"),
    SEED((byte) 24, "Seed", "Sd", 0xFF8BC34A, true, 3, true, true, false, false, false, State.SOLID, true, 0, 20.0f, 250.0f, Float.MAX_VALUE, 1.50f, 0.15f, "CARBON_MONOXIDE", null),
    THERMITE((byte) 25, "Thermite", "Fe2O3+Al", 0xFFB71C1C, false, 4, true, false, false, false, false, State.SOLID, true, 0, 20.0f, 1600.0f, Float.MAX_VALUE, 0.75f, 0.40f, "LAVA", null),
    GUNPOWDER((byte) 26, "Gunpowder", "KNO3+S+C", 0xFF53565A, false, 3, true, true, false, false, false, State.SOLID, true, 0, 20.0f, 300.0f, Float.MAX_VALUE, 0.92f, 0.20f, "CARBON_DIOXIDE", null),
    STONE((byte) 27, "Stone", "SiO2", 0xFF808080, false, 4, false, false, false, false, false, State.SOLID, false, 0, 20.0f, 1200.0f, Float.MAX_VALUE, 0.84f, 0.50f, "LAVA", null),
    GRAVEL((byte) 28, "Gravel", "SiO2", 0xFF9B8773, false, 3, false, true, false, false, false, State.SOLID, false, 0, 20.0f, 1200.0f, Float.MAX_VALUE, 0.84f, 0.40f, "LAVA", null),
    DIRT((byte) 29, "Dirt", "CSi+H2O", 0xFF5D4037, false, 3, false, true, false, false, false, State.SOLID, true, 0, 20.0f, 1100.0f, Float.MAX_VALUE, 1.00f, 0.35f, "LAVA", null),
    WET_SAND((byte) 30, "Wet Sand", "SiO2+H2O", 0xFF9E753B, false, 4, false, true, false, false, false, State.SOLID, false, 0, 20.0f, 1700.0f, 100.0f, 2.10f, 0.50f, "LAVA", "STEAM"),
    FIRE((byte) 31, "Fire", "Q", 0xFFFF5722, true, 0, false, false, false, true, false, State.NONE, false, 0, 800.0f, Float.MAX_VALUE, Float.MAX_VALUE, 0.10f, 0.90f, null, null),
    GRASS((byte) 32, "Grass", "C6H10O5", 0xFF4CAF50, false, 4, true, true, false, false, false, State.SOLID, false, 0, 20.0f, 200.0f, Float.MAX_VALUE, 1.60f, 0.12f, "CARBON_MONOXIDE", null),
    SILICON((byte) 33, "Silicon", "Si", 0xFF5C6BC0, true, 4, false, true, false, false, false, State.SOLID, true, 0, 20.0f, 1414.0f, Float.MAX_VALUE, 0.71f, 0.85f, "LAVA", null),
    SULFUR((byte) 34, "Sulfur", "S", 0xFFFFEB3B, true, 3, true, false, false, false, false, State.SOLID, true, 0, 20.0f, 115.2f, 444.6f, 0.71f, 0.20f, "LAVA", "CARBON_DIOXIDE"),
    BLACK_POWDER((byte) 35, "Black Powder", "S+C", 0xFF4A442D, false, 3, true, false, false, false, false, State.SOLID, true, 0, 20.0f, 250.0f, Float.MAX_VALUE, 0.71f, 0.30f, "CARBON_MONOXIDE", null),
    CARBON((byte) 36, "Carbon", "C", 0xFF222222, true, 3, true, false, false, false, false, State.SOLID, true, 0, 20.0f, 3550.0f, Float.MAX_VALUE, 0.71f, 0.60f, "CARBON_MONOXIDE", null),
    OXYGEN((byte) 37, "Oxygen", "O2", 0x88B0E0E6, true, -2, false, false, false, false, false, State.GAS, false, 3, 20.0f, -218.8f, -183.0f, 0.92f, 0.02f, null, null),
    IRON((byte) 38, "Iron", "Fe", 0xFF795548, false, 4, false, false, false, false, true, State.SOLID, true, 0, 20.0f, 1538.0f, Float.MAX_VALUE, 0.45f, 0.80f, "LAVA", null),
    NITROGEN((byte) 39, "Nitrogen", "N2", 0x88A0C4FF, false, -2, false, false, false, false, false, State.GAS, false, 0, 20.0f, -210.0f, -195.8f, 1.04f, 0.02f, null, null),
    STEEL((byte) 40, "Steel", "Fe+C", 0xFF708090, false, 4, false, true, false, false, true, State.SOLID, false, 0, 20.0f, 1400.0f, Float.MAX_VALUE, 0.49f, 0.75f, "LAVA", null),
    STAINLESS_STEEL((byte) 41, "Stainless Steel", "Fe+Cr", 0xFFCFD8DC, true, 4, false, false, false, false, true, State.SOLID, false, 0, 20.0f, 1450.0f, 2800.0f, 0.50f, 0.70f, "LAVA", null),
    ALUMINUM((byte) 42, "Aluminum", "Al", 0xFFD4D8DD, false, 3, false, false, false, false, true, State.SOLID, false, 0, 20.0f, 660.3f, 2470.0f, 0.90f, 0.88f, "LAVA", null),
    COPPER((byte) 43, "Copper", "Cu", 0xFFB87333, false, 4, false, true, false, false, true, State.SOLID, false, 0, 20.0f, 1085.0f, 2562.0f, 0.39f, 0.98f, "LAVA", null),
    COPPER_OXIDIZED((byte) 44, "Oxidized Copper", "Cu2O", 0xFF43B3AE, false, 4, false, false, false, false, false, State.SOLID, false, 0, 20.0f, 1235.0f, Float.MAX_VALUE, 0.51f, 0.20f, "LAVA", null),
    BEIGE_POWDER((byte) 45, "Beige Powder", "C25H52(p)", 0xFFE6D7B8, false, 3, true, false, false, false, false, State.SOLID, true, 0, 20.0f, 62.0f, Float.MAX_VALUE, 2.00f, 0.20f, "WAX_LIQUID", null),
    WAX((byte) 46, "Wax", "C25H52", 0xFFF5E6CC, false, 4, true, false, false, false, false, State.SOLID, false, 0, 20.0f, 62.0f, Float.MAX_VALUE, 2.14f, 0.25f, "WAX_LIQUID", null),
    WAX_LIQUID((byte) 47, "Liquid Wax", "C25H52(l)", 0xFFE6C894, false, 3, true, false, false, false, false, State.LIQUID, false, 2, 70.0f, Float.MAX_VALUE, 370.0f, 2.50f, 0.15f, null, "METHANE"),
    GALLIUM((byte) 48, "Gallium", "Ga", 0xFF8FA3AD, true, 5, false, true, false, false, true, State.SOLID, false, 0, 20.0f, 29.8f, 2204.0f, 0.37f, 0.40f, "GALLIUM_LIQUID", null),
    GALLIUM_LIQUID((byte) 49, "Liquid Gallium", "Ga(l)", 0xFFB0BEC5, false, 5, false, true, false, false, true, State.LIQUID, false, 3, 35.0f, Float.MAX_VALUE, 2204.0f, 0.37f, 0.40f, null, null),
    TITANIUM((byte) 50, "Titanium", "Ti", 0xFF90A4AE, true, 4, false, false, false, false, true, State.SOLID, false, 0, 20.0f, 1668.0f, 3287.0f, 0.52f, 0.65f, "LAVA", null);

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
    private final boolean isMetal;
    private final State state;
    private final boolean isPowder;
    private final int dispersionRate;

    private final float defaultTemp;
    private final float meltingPoint;
    private final float boilingPoint;
    private final float heatCapacity;
    private final float conductivity;

    private final String meltsIntoName;
    private final String boilsIntoName;

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
              boolean isHot, boolean isMetal, State state, boolean isPowder,
              int dispersionRate, float defaultTemp, float meltingPoint, float boilingPoint,
              float heatCapacity, float conductivity,
              String meltsIntoName, String boilsIntoName) {
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
        this.isMetal = isMetal;
        this.state = state;
        this.isPowder = isPowder;
        this.dispersionRate = dispersionRate;

        this.defaultTemp = defaultTemp;
        this.meltingPoint = meltingPoint;
        this.boilingPoint = boilingPoint;
        this.heatCapacity = heatCapacity;
        this.conductivity = conductivity;

        this.meltsIntoName = meltsIntoName;
        this.boilsIntoName = boilsIntoName;
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

    public boolean isMetal() {
        return isMetal;
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

    public ElementID getMeltsInto() {
        try {
            return ElementID.valueOf(meltsIntoName);
        } catch (IllegalArgumentException | NullPointerException e) {
            return VOID;
        }
    }

    public ElementID getBoilsInto() {
        try {
            return ElementID.valueOf(boilsIntoName);
        } catch (IllegalArgumentException | NullPointerException e) {
            return VOID;
        }
    }

    public static ElementID fromId(byte id) {
        int index = id & 0xFF;
        if (index >= BY_ID.length || BY_ID[index] == null) {
            return VOID;
        }
        return BY_ID[index];
    }
}