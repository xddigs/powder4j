package org.p4j.data;

public record Recipe(
        ElementID ingredientA,
        ElementID ingredientB,
        ElementID resultA,
        ElementID resultB,
        float chance) {}