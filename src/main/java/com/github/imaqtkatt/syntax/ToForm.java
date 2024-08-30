package com.github.imaqtkatt.syntax;

import com.github.imaqtkatt.analysis.Form;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public final class ToForm {
    public static Form form(Node node) {
        return switch (node) {
            case Node.List list -> listForm(list.elements().iterator());
            case Node.Identifier identifier -> new Form.Identifier(identifier.identifier());
            case Node.Number number -> new Form.Number(number.value());
        };
    }

    public static Form listForm(Iterator<Node> iterator) {
        if (!iterator.hasNext()) {
            return new Form.Invalid();
        }
        var head = iterator.next();
        Form result;
        switch (head) {
            case Node.Identifier identifier -> {
                switch (identifier.identifier()) {
                    case "let" -> result = letForm(iterator);
                    case "lambda" -> result = lambdaForm(iterator);
                    case "do" -> result = doForm(iterator);
                    default -> result = callForm(head, iterator);
                }
            }
            case Node.Number _, Node.List _ -> result = callForm(head, iterator);
        }
        return result;
    }

    public static Form letForm(Iterator<Node> iterator) {
        if (!iterator.hasNext()) {
            return new Form.Invalid();
        }
        var binds = nodeList(iterator.next());
        if (binds.isEmpty()) {
            return new Form.Invalid();
        }
        var binds2 = binds.get();
        var keyPairs = new LinkedList<Form.Let.KeyPair>();
        for (var pair : binds2) {
            var keyPair = pair(pair);
            if (keyPair.isEmpty()) {
                return new Form.Invalid();
            }
            keyPairs.add(keyPair.get());
        }

        if (!iterator.hasNext()) {
            return new Form.Invalid();
        }
        var body = form(iterator.next());

        return new Form.Let(keyPairs, body);
    }

    public static Form lambdaForm(Iterator<Node> iterator) {
        if (!iterator.hasNext()) {
            return new Form.Invalid();
        }
        var list = nodeList(iterator.next());
        if (list.isEmpty()) {
            return new Form.Invalid();
        }
        var list2 = list.get();
        List<String> variables = new LinkedList<>();
        for (var e : list2) {
            var name = identifier(e);
            if (name.isEmpty()) {
                return new Form.Invalid();
            }
            variables.add(name.get());
        }

        if (!iterator.hasNext()) {
            return new Form.Invalid();
        }
        var body = form(iterator.next());

        return new Form.Lambda(variables, body);
    }

    public static Form doForm(Iterator<Node> iterator) {
        List<Form> actions = new LinkedList<>();
        while (iterator.hasNext()) {
            actions.add(form(iterator.next()));
        }
        return new Form.Do(actions);
    }

    public static Form callForm(Node head, Iterator<Node> iterator) {
        var tail = new LinkedList<Form>();
        while (iterator.hasNext()) {
            tail.add(form(iterator.next()));
        }
        return new Form.Call(form(head), tail);
    }

    public static Optional<String> identifier(Node node) {
        if (node instanceof Node.Identifier i) {
            return Optional.of(i.identifier());
        } else {
            return Optional.empty();
        }
    }

    public static Optional<List<Node>> nodeList(Node node) {
        if (node instanceof Node.List list) {
            return Optional.of(list.elements());
        } else {
            return Optional.empty();
        }
    }

    public static Optional<Form.Let.KeyPair> pair(Node node) {
        var pair = nodeList(node);
        if (pair.isPresent()) {
            var iterator = pair.get().iterator();
            if (!iterator.hasNext()) {
                return Optional.empty();
            }
            var name = identifier(iterator.next());
            if (name.isEmpty()) {
                return Optional.empty();
            }
            if (!iterator.hasNext()) {
                return Optional.empty();
            }
            var key = name.get();
            var value = form(iterator.next());
            return Optional.of(new Form.Let.KeyPair(key, value));
        } else {
            return Optional.empty();
        }
    }
}
