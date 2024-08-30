package com.github.imaqtkatt.syntax;

import com.github.imaqtkatt.analysis.TopLevel;

import java.util.Iterator;

import static com.github.imaqtkatt.syntax.ToForm.form;
import static com.github.imaqtkatt.syntax.ToForm.identifier;

public final class ToTopLevel {
    public static TopLevel topLevel(Node.List node) {
        var iterator = node.elements().iterator();
        if (!iterator.hasNext()) {
            return new TopLevel.Invalid();
        }
        var identifier = identifier(iterator.next());
        return identifier.map(s -> switch (s) {
                    case "def" -> def(iterator);
                    case "eval" -> eval(iterator);
                    default -> new TopLevel.Invalid();
                })
                .orElseGet(TopLevel.Invalid::new);
    }

    public static TopLevel def(Iterator<Node> iterator) {
        if (!iterator.hasNext()) {
            return new TopLevel.Invalid();
        }
        var name = identifier(iterator.next());
        if (name.isEmpty()) {
            return new TopLevel.Invalid();
        }
        if (!iterator.hasNext()) {
            return new TopLevel.Invalid();
        }
        var body = form(iterator.next());
        return new TopLevel.Def(name.get(), body);
    }

    public static TopLevel eval(Iterator<Node> iterator) {
        if (!iterator.hasNext()) {
            return new TopLevel.Invalid();
        }
        return new TopLevel.Eval(form(iterator.next()));
    }
}
