package org.p4j.data;

public record Recipe(
        ElementID ingredientA,
        ElementID ingredientB,
        ElementID resultA,
        ElementID resultB,
        float chance
) {
    public Recipe(ElementID ingredientA, ElementID ingredientB,
                  ElementID resultA, ElementID resultB) {
        this(ingredientA, ingredientB, resultA, resultB, 1.0f);
    }
}