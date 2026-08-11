package org.p4j.data;

@FunctionalInterface
public interface ElementReaction {
    boolean process(int x, int y, int idx);
}