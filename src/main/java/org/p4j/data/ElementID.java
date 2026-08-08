package org.p4j.data;

/**
 * Defines the distinct types of elements available within the simulation,
 * including all 118 chemical elements of the periodic table (Z = 1 to 118),
 * alongside custom compounds, mixtures, smokes, wood, bedrock, and specialized blocks.
 */
public enum ElementID {
    EMPTY((short) 0, "Air", "O2", 0xFF0B0E14, false, 0, false, false, false, false, false, 0),
    HYDROGEN((short) 1, "Hydrogen", "H", 0x88E0F2FE, true, -3, true, false, false, false, false, 0),
    HELIUM((short) 2, "Helium", "He", 0x88FFEEFF, true, -3, false, false, false, false, false, 0),
    LITHIUM((short) 3, "Lithium", "Li", 0xFFCC99FF, true, 3, true, true, false, false, false, 0),
    BERYLLIUM((short) 4, "Beryllium", "Be", 0xFF99CC66, true, 4, false, false, false, false, false, 0),
    BORON((short) 5, "Boron", "B", 0xFFCC9966, true, 4, false, false, false, false, false, 0),
    CARBON((short) 6, "Carbon", "C", 0xFF222222, true, 4, true, false, false, false, false, 0),
    NITROGEN((short) 7, "Nitrogen", "N", 0x88CCCCFF, true, -2, false, false, false, false, false, 0),
    OXYGEN((short) 8, "Oxygen", "O", 0x88FFCCFF, true, -2, true, false, false, false, false, 0),
    FLUORINE((short) 9, "Fluorine", "F", 0xFF99FF33, true, -1, false, true, false, false, false, 0),
    NEON((short) 10, "Neon", "Ne", 0xFFFF3366, true, -2, false, false, false, false, false, 0),
    SODIUM((short) 11, "Sodium", "Na", 0xFFD1D5DB, true, 3, true, true, false, false, false, 0),
    MAGNESIUM((short) 12, "Magnesium", "Mg", 0xFFE0E0E0, true, 3, true, false, false, false, false, 0),
    ALUMINUM((short) 13, "Aluminum", "Al", 0xFFB0C4DE, true, 4, true, false, false, false, false, 0),
    SILICON_ELEM((short) 14, "Silicon", "Si", 0xFF5C6BC0, true, 4, false, false, false, false, false, 0),
    PHOSPHORUS((short) 15, "Phosphorus", "P", 0xFFFF9900, true, 3, true, false, false, false, false, 0),
    SULFUR((short) 16, "Sulfur", "S", 0xFFFFEE55, true, 3, true, false, false, false, false, 0),
    CHLORINE_ELEM((short) 17, "Chlorine", "Cl", 0xFF88FF00, true, -1, false, true, false, false, false, 0),
    ARGON((short) 18, "Argon", "Ar", 0xFFE0FFFF, true, -2, false, false, false, false, false, 0),
    POTASSIUM((short) 19, "Potassium", "K", 0xFFDDA0DD, true, 3, true, true, false, false, false, 0),
    CALCIUM((short) 20, "Calcium", "Ca", 0xFFEFEFEF, true, 4, true, false, false, false, false, 0),
    SCANDIUM((short) 21, "Scandium", "Sc", 0xFFC0C0C0, true, 4, false, false, false, false, false, 0),
    TITANIUM((short) 22, "Titanium", "Ti", 0xFF808080, true, 5, false, false, false, false, false, 0),
    VANADIUM((short) 23, "Vanadium", "V", 0xFF708090, true, 5, false, false, false, false, false, 0),
    CHROMIUM((short) 24, "Chromium", "Cr", 0xFFB0C4DE, true, 5, false, false, false, false, false, 0),
    MANGANESE((short) 25, "Manganese", "Mn", 0xFFA9A9A9, true, 5, false, false, false, false, false, 0),
    IRON((short) 26, "Iron", "Fe", 0xFF708090, true, 5, false, false, false, false, false, 0),
    COBALT((short) 27, "Cobalt", "Co", 0xFF4682B4, true, 5, false, false, false, false, false, 0),
    NICKEL((short) 28, "Nickel", "Ni", 0xFFA0A0A0, true, 5, false, false, false, false, false, 0),
    COPPER((short) 29, "Copper", "Cu", 0xFFB87333, true, 5, false, false, false, false, false, 0),
    ZINC((short) 30, "Zinc", "Zn", 0xFF778899, true, 4, false, false, false, false, false, 0),
    GALLIUM((short) 31, "Gallium", "Ga", 0xFFB0E0E6, true, 4, false, false, false, true, true, 2),
    GERMANIUM((short) 32, "Germanium", "Ge", 0xFF696969, true, 4, false, false, false, false, false, 0),
    ARSENIC((short) 33, "Arsenic", "As", 0xFF8F8F8F, true, 4, false, true, false, false, false, 0),
    SELENIUM((short) 34, "Selenium", "Se", 0xFFCD853F, true, 4, false, false, false, false, false, 0),
    BROMINE((short) 35, "Bromine", "Br", 0xFF8B0000, true, 3, false, true, false, false, true, 3),
    KRYPTON((short) 36, "Krypton", "Kr", 0xFFE0FFFF, true, -2, false, false, false, false, false, 0),
    RUBIDIUM((short) 37, "Rubidium", "Rb", 0xFFD8BFD8, true, 3, true, true, false, false, false, 0),
    STRONTIUM((short) 38, "Strontium", "Sr", 0xFFDCDCDC, true, 4, true, false, false, false, false, 0),
    YTTRIUM((short) 39, "Yttrium", "Y", 0xFFC0C0C0, true, 4, false, false, false, false, false, 0),
    ZIRCONIUM((short) 40, "Zirconium", "Zr", 0xFFA9A9A9, true, 5, false, false, false, false, false, 0),
    NIOBIUM((short) 41, "Niobium", "Nb", 0xFF708090, true, 5, false, false, false, false, false, 0),
    MOLYBDENUM((short) 42, "Molybdenum", "Mo", 0xFF696969, true, 5, false, false, false, false, false, 0),
    TECHNETIUM((short) 43, "Technetium", "Tc", 0xFF808080, true, 5, false, false, false, false, false, 0),
    RUTHENIUM((short) 44, "Ruthenium", "Ru", 0xFF696969, true, 5, false, false, false, false, false, 0),
    RHODIUM((short) 45, "Rhodium", "Rh", 0xFFC0C0C0, true, 5, false, false, false, false, false, 0),
    PALLADIUM((short) 46, "Palladium", "Pd", 0xFFD3D3D3, true, 5, false, false, false, false, false, 0),
    SILVER((short) 47, "Silver", "Ag", 0xFFC0C0C0, true, 5, false, false, false, false, false, 0),
    CADMIUM((short) 48, "Cadmium", "Cd", 0xFF708090, true, 4, false, true, false, false, false, 0),
    INDIUM((short) 49, "Indium", "In", 0xFFDCDCDC, true, 4, false, false, false, false, false, 0),
    TIN((short) 50, "Tin", "Sn", 0xFFC0C0C0, true, 4, false, false, false, false, false, 0),
    ANTIMONY((short) 51, "Antimony", "Sb", 0xFFA9A9A9, true, 4, false, false, false, false, false, 0),
    TELLURIUM((short) 52, "Tellurium", "Te", 0xFFD2B48C, true, 4, false, false, false, false, false, 0),
    IODINE((short) 53, "Iodine", "I", 0xFF4B0082, true, 3, false, false, false, false, false, 0),
    XENON((short) 54, "Xenon", "Xe", 0xFFE0FFFF, true, -2, false, false, false, false, false, 0),
    CESIUM((short) 55, "Cesium", "Cs", 0xFFF4A460, true, 3, true, true, false, false, false, 0),
    BARIUM((short) 56, "Barium", "Ba", 0xFFDCDCDC, true, 4, true, false, false, false, false, 0),
    LANTHANUM((short) 57, "Lanthanum", "La", 0xFFC0C0C0, true, 4, false, false, false, false, false, 0),
    CERIUM((short) 58, "Cerium", "Ce", 0xFFC0C0C0, true, 4, true, false, false, false, false, 0),
    PRASEODYMIUM((short) 59, "Praseodymium", "Pr", 0xFFC0C0C0, true, 4, false, false, false, false, false, 0),
    NEODYMIUM((short) 60, "Neodymium", "Nd", 0xFFC0C0C0, true, 4, false, false, false, false, false, 0),
    PROMETHIUM((short) 61, "Promethium", "Pm", 0xFFC0C0C0, true, 4, false, false, false, false, false, 0),
    SAMARIUM((short) 62, "Samarium", "Sm", 0xFFC0C0C0, true, 4, false, false, false, false, false, 0),
    EUROPIUM((short) 63, "Europium", "Eu", 0xFFC0C0C0, true, 4, true, false, false, false, false, 0),
    GADOLINIUM((short) 64, "Gadolinium", "Gd", 0xFFC0C0C0, true, 4, false, false, false, false, false, 0),
    TERBIUM((short) 65, "Terbium", "Tb", 0xFFC0C0C0, true, 4, false, false, false, false, false, 0),
    DYSPROSIUM((short) 66, "Dysprosium", "Dy", 0xFFC0C0C0, true, 4, false, false, false, false, false, 0),
    HOLMIUM((short) 67, "Holmium", "Ho", 0xFFC0C0C0, true, 4, false, false, false, false, false, 0),
    ERBIUM((short) 68, "Erbium", "Er", 0xFFC0C0C0, true, 4, false, false, false, false, false, 0),
    THULIUM((short) 69, "Thulium", "Tm", 0xFFC0C0C0, true, 4, false, false, false, false, false, 0),
    YTTERBIUM((short) 70, "Ytterbium", "Yb", 0xFFC0C0C0, true, 4, false, false, false, false, false, 0),
    LUTETIUM((short) 71, "Lutetium", "Lu", 0xFFC0C0C0, true, 4, false, false, false, false, false, 0),
    HAFNIUM((short) 72, "Hafnium", "Hf", 0xFF708090, true, 5, false, false, false, false, false, 0),
    TANTALUM((short) 73, "Tantalum", "Ta", 0xFF708090, true, 5, false, false, false, false, false, 0),
    TUNGSTEN((short) 74, "Tungsten", "W", 0xFF556B2F, true, 5, false, false, false, false, false, 0),
    RHENIUM((short) 75, "Rhenium", "Re", 0xFF708090, true, 5, false, false, false, false, false, 0),
    OSMIUM((short) 76, "Osmium", "Os", 0xFF4F4F4F, true, 5, false, false, false, false, false, 0),
    IRIDIUM((short) 77, "Iridium", "Ir", 0xFF505050, true, 5, false, false, false, false, false, 0),
    PLATINUM((short) 78, "Platinum", "Pt", 0xFFE5E4E2, true, 5, false, false, false, false, false, 0),
    GOLD((short) 79, "Gold", "Au", 0xFFFFD700, true, 5, false, false, false, false, false, 0),
    MERCURY((short) 80, "Mercury", "Hg", 0xFFCFD8DC, true, 5, false, false, false, false, true, 1),
    THALLIUM((short) 81, "Thallium", "Tl", 0xFF708090, true, 4, false, true, false, false, false, 0),
    LEAD((short) 82, "Lead", "Pb", 0xFF4A607A, true, 5, false, false, false, false, false, 0),
    BISMUTH((short) 83, "Bismuth", "Bi", 0xFF4682B4, true, 4, false, false, false, false, false, 0),
    POLONIUM((short) 84, "Polonium", "Po", 0xFF808080, true, 4, false, true, false, true, false, 0),
    ASTATINE((short) 85, "Astatine", "At", 0xFF8B0000, true, 4, false, true, false, true, false, 0),
    RADON((short) 86, "Radon", "Rn", 0xFFE0FFFF, true, -2, false, false, false, true, false, 0),
    FRANCIUM((short) 87, "Francium", "Fr", 0xFFCD5C5C, true, 3, true, true, false, true, false, 0),
    RADIUM((short) 88, "Radium", "Ra", 0xFFE0EEEE, true, 4, true, false, false, true, false, 0),
    ACTINIUM((short) 89, "Actinium", "Ac", 0xFFC0C0C0, true, 4, false, false, false, true, false, 0),
    THORIUM((short) 90, "Thorium", "Th", 0xFFC0C0C0, true, 4, true, false, false, true, false, 0),
    PROTACTINIUM((short) 91, "Protactinium", "Pa", 0xFFC0C0C0, true, 4, false, false, false, true, false, 0),
    URANIUM((short) 92, "Uranium", "U", 0xFF2E8B57, true, 5, false, false, false, true, false, 0),
    NEPTUNIUM((short) 93, "Neptunium", "Np", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),
    PLUTONIUM((short) 94, "Plutonium", "Pu", 0xFF4169E1, true, 5, false, false, false, true, false, 0),
    AMERICIUM((short) 95, "Americium", "Am", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),
    CURIUM((short) 96, "Curium", "Cm", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),
    BERKELIUM((short) 97, "Berkelium", "Bk", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),
    CALIFORNIUM((short) 98, "Californium", "Cf", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),
    EINSTEINIUM((short) 99, "Einsteinium", "Es", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),
    FERMIUM((short) 100, "Fermium", "Fm", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),
    MENDELEVIUM((short) 101, "Mendelevium", "Md", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),
    NOBELIUM((short) 102, "Nobelium", "No", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),
    LAWRENCIUM((short) 103, "Lawrencium", "Lr", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),
    RUTHERFORDIUM((short) 104, "Rutherfordium", "Rf", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),
    DUBNIUM((short) 105, "Dubnium", "Db", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),
    SEABORGIUM((short) 106, "Seaborgium", "Sg", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),
    BOHRIUM((short) 107, "Bohrium", "Bh", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),
    HASSIUM((short) 108, "Hassium", "Hs", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),
    MEITNERIUM((short) 109, "Meitnerium", "Mt", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),
    DARMSTADTIUM((short) 110, "Darmstadtium", "Ds", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),
    ROENTGENIUM((short) 111, "Roentgenium", "Rg", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),
    COPERNICIUM((short) 112, "Copernicium", "Cn", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),
    NIHONIUM((short) 113, "Nihonium", "Nh", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),
    FLEROVIUM((short) 114, "Flerovium", "Fl", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),
    MOSCOVIUM((short) 115, "Moscovium", "Mc", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),
    LIVERMORIUM((short) 116, "Livermorium", "Lv", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),
    TENNESSINE((short) 117, "Tennessine", "Ts", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),
    OGANESSON((short) 118, "Oganesson", "Og", 0xFFC0C0C0, true, 5, false, false, false, true, false, 0),

    WATER((short) 119, "Water", "H2O", 0xFF4FA6ED, true, 2, false, false, true, false, true, 5),
    ICE((short) 120, "Ice", "H2O(s)", 0xFFA5F2F3, false, 4, false, true, false, false, false, 0),
    STEAM((short) 121, "Steam", "H2O(g)", 0xCCFFFFFF, false, -1, false, false, false, false, false, 0),
    SMOKE_DARK((short) 122, "Dark Smoke", "C", 0xFF3E4451, false, -1, false, false, false, false, false, 0),
    SMOKE_LIGHT((short) 123, "Light Smoke", "H2O(g)", 0xFFDCDFE4, false, -1, false, false, false, false, false, 0),
    SMOKE_GRAY((short) 124, "Smoke", "CO2", 0xFFABB2BF, false, -1, false, false, false, false, false, 0),
    ACID((short) 125, "Acid", "HCl", 0xFF2ECC71, false, 2, false, false, false, false, true, 5),
    SALT((short) 126, "Salt", "NaCl", 0xFFF5F5F5, false, 3, false, false, false, false, false, 0),
    SAND((short) 127, "Sand", "SiO2", 0xFFE5C07B, true, 3, false, true, false, false, false, 0),
    GLASS((short) 128, "Glass", "SiO2", 0x80E0F7FA, false, 5, false, false, false, false, false, 0),
    LAVA((short) 129, "Lava", "SiO2+", 0xFFFF4500, true, 3, false, false, false, true, true, 2),
    OBSIDIAN((short) 130, "Obsidian", "SiO2+", 0xFF1C1326, false, 5, false, false, false, false, false, 0),
    CEMENT((short) 131, "Cement", "CaCO3", 0xFF9E9E9E, false, 4, false, false, false, false, false, 0),
    METHANE((short) 132, "Methane", "CH4", 0x8800FFAA, false, -2, true, false, false, false, false, 0),
    TNT((short) 133, "TNT", "C7H5N3O6", 0xFFC23616, false, 1, true, true, false, false, false, 0),
    GASOLINE((short) 134, "Gasoline", "C8H18", 0xFFD4A373, false, 1, true, true, false, false, true, 5),
    OIL((short) 135, "Oil", "CnHm", 0xFF8A9A20, false, 1, true, true, false, false, true, 4),
    WOOD((short) 136, "Wood", "C6H10O5", 0xFF8B5A2B, false, 4, true, true, false, false, false, 0),
    MUD((short) 137, "Mud", "SiO2+H2O", 0xFF3E2723, false, 4, true, true, false, false, true, 1),
    SEED((short) 138, "Seed", "Sd", 0xFF8BC34A, false, 3, true, true, false, false, false, 0),
    THERMITE((short) 139, "Thermite", "Fe+Al", 0xFFB71C1C, false, 4, true, false, false, false, false, 0),
    GUNPOWDER((short) 140, "Gunpowder", "KNO3", 0xFF53565A, false, 3, true, true, false, false, false, 0),
    STONE((short) 141, "Stone", "ST", 0xFF808080, false, 4, false, false, false, false, false, 0),
    BEDROCK((short) 142, "Bedrock", "BR", 0xFF111111, false, 100, false, false, false, false, false, 0),
    GRAVEL((short) 143, "Gravel", "Gr", 0xFF9B8773, false, 3, false, true, false, false, false, 0),
    DIRT((short) 144, "Dirt", "Soil", 0xFF5D4037, true, 3, false, true, false, false, false, 0),
    WET_SAND((short) 145, "Wet Sand", "SiO2", 0xFF9E753B, false, 4, false, true, false, false, false, 0),
    FIRE((short) 146, "Fire", "Q", 0xFFE06C75, true, 0, false, false, false, true, false, 0),
    GRASS((short) 147, "Grass", "G", 0xFF4CAF50, false, 4, true, true, false, false, false, 0);

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
        return this == SAND || this == SILICON_ELEM || this == DIRT ||
                this == SEED || this == SALT || this == SODIUM ||
                this == CEMENT || this == GUNPOWDER || this == THERMITE ||
                this == GRAVEL || this == WET_SAND;
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