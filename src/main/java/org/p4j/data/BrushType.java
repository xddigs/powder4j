package org.p4j.data;

public enum BrushType {
    BRUSH("Brush", "B"),
    FILLER("Filler", "F"),
    ERASER("Eraser", "E");

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