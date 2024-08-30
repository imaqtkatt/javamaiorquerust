package com.github.imaqtkatt.analysis;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public record Program(List<TopLevel> topLevels) {
    public List<Define> defineList() {
        return this.topLevels.stream().filter(t -> t instanceof TopLevel.Def)
                .map(t -> (Define) t)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public List<Evaluate> evaluateList() {
        return this.topLevels.stream().filter(t -> t instanceof TopLevel.Eval)
                .map(t -> (Evaluate) t)
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
