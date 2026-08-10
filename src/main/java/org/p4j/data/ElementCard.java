package org.p4j.data;

public record ElementCard(
        ElementID element,
        byte id,
        String name,
        String symbol,
        float liveTemp,
        float defaultTemp,
        float boilingPoint,
        float meltingPoint
) {}