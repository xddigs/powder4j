package org.p4j.data;

public record Recipe(
        ElementID elementA,
        ElementID elementB,
        ElementID resultA,
        ElementID resultB,
        float chance
) {}