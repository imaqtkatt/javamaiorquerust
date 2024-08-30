package com.github.imaqtkatt.value;

import java.util.HashMap;
import java.util.Map;

public final class AtomTable {
    private final Map<String, Object> context = new HashMap<>();
    private static AtomTable INSTANCE;

    private AtomTable() {
        define("null", Value.NULL);
        define("add", (SumatraFn) args -> {
            var acc = 0;
            for (var arg : args) {
                var e = (Integer) arg.eval();
                acc += e;
            }
            return new Value.Object(acc);
        });
    }

    public static AtomTable getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AtomTable();
        }
        return INSTANCE;
    }

    public void define(String name, Object o) {
        context.put(name, o);
    }

    public Object fetch(String name) {
        return context.get(name);
    }

    public boolean isDefined(String name) {
        return context.containsKey(name);
    }
}
