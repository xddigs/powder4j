package org.p4j.data;

public enum BrushShape {
    CIRCLE("Circle", "●"),
    SQUARE("Square", "■"),
    TRIANGLE("Triangle", "▲");

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