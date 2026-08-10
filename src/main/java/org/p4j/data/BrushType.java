package org.p4j.data;

public enum BrushType {
    BRUSH("Brush", Character.toString(0xF0CE3)),
    FILLER("Filler", Character.toString(0xF1416)),
    ERASER("Eraser", Character.toString(0xF0DFE));

    private final String name;
    private final String symbol;

    BrushType(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }
}