package org.p4j.data;

@FunctionalInterface
public interface NeighborVisitor {
    boolean visit(int nx, int ny, int nIdx, ElementID neighbor);
}