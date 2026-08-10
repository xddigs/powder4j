package org.p4j.data;

public record ElementCard(ElementID e, byte id,
                          String name, String formula,
                          int temperature) {}