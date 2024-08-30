package com.github.imaqtkatt.syntax;

public sealed interface Node {
    record Identifier(String identifier) implements Node {
    }

    record Number(Integer value) implements Node {
    }

    record List(java.util.List<Node> elements) implements Node {
    }
}
