package org.p4j.data;

public enum BrushShape {
    CIRCLE("Circle", "\uF111"),
    SQUARE("Square", "\uF0C8"),
    TRIANGLE("Triangle", "\uDB81\uDD36");

    private final String name;
    private final String symbol;

    BrushShape(String name, String symbol) {
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