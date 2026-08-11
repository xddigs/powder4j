package org.p4j.data;

import org.p4j.core.World;

@FunctionalInterface
public interface ElementReaction {
    boolean process(World world, int x, int y, int idx, ElementID e);
}