package com.github.imaqtkatt.syntax;

import com.github.imaqtkatt.analysis.Program;

import java.util.LinkedList;
import java.util.stream.Collectors;

public final class ToAnalyzedProgram {

    public static Program program(com.github.imaqtkatt.syntax.Program program) {
        var topLevels = program.topLevels().stream()
                .map(ToTopLevel::topLevel)
                .collect(Collectors.toCollection(LinkedList::new));
        return new Program(topLevels);
    }

}
