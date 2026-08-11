package org.p4j.data;

public enum BrushType {
    BRUSH("Brush", "\uF1FC"),
    FILLER("Filler", "\uF043"),
    ERASER("Eraser", "\uF12D"),
    DROPPER("Dropper", "\uF1FB");

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