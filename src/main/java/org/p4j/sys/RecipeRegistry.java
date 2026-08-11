package org.p4j.sys;

import org.p4j.data.ElementID;
import org.p4j.data.Recipe;

import java.util.HashMap;
import java.util.Map;

public class RecipeRegistry {
    private final Map<Long, Recipe> recipes = new HashMap<>();

    public void register(ElementID a, ElementID b,
                         ElementID resultA, ElementID resultB) {
        register(a, b, resultA, resultB, 1.0f);
    }

    public void register(ElementID a, ElementID b,
                         ElementID resultA, ElementID resultB, float chance) {
        long key = getPairKey(a, b);
        recipes.put(key, new Recipe(a, b, resultA, resultB, chance));
    }

    public Recipe getRecipe(ElementID a, ElementID b) {
        if (a == null || b == null) return null;
        return recipes.get(getPairKey(a, b));
    }

    private long getPairKey(ElementID a, ElementID b) {
        int id1 = Math.min(a.getId() & 0xFF, b.getId() & 0xFF);
        int id2 = Math.max(a.getId() & 0xFF, b.getId() & 0xFF);
        return ((long) id1 << 32) | id2;
    }
}