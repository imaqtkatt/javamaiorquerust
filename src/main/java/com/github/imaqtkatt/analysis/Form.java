package com.github.imaqtkatt.analysis;

import com.github.imaqtkatt.value.AtomTable;
import com.github.imaqtkatt.value.Value;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public sealed interface Form {
    Value accept(AtomTable table);

    record Invalid() implements Form {
        @Override
        public Value accept(AtomTable table) {
            // TODO: handle this case
            return null;
        }
    }

    record Identifier(String identifier) implements Form {
        @Override
        public Value accept(AtomTable table) {
            return new Value.Atom(table, identifier);
        }
    }

    record Number(Integer value) implements Form {
        @Override
        public Value accept(AtomTable table) {
            return new Value.Object(value);
        }
    }

    record Lambda(List<String> variables, Form body) implements Form {
        @Override
        public Value accept(AtomTable table) {
            return new Value.Lambda(table, variables, body.accept(table));
        }
    }

    record Call(Form head, List<Form> tail) implements Form {
        @Override
        public Value accept(AtomTable table) {
            var hd = head.accept(table);
            var tl = tail.stream()
                    .map(e -> e.accept(table))
                    .collect(Collectors.toCollection(LinkedList::new));
            return new Value.Expr(hd, tl);
        }
    }

    record Do(List<Form> actions) implements Form {
        @Override
        public Value accept(AtomTable table) {
            return new Value.Do(actions.stream().map(act -> act.accept(table))
                    .collect(Collectors.toCollection(LinkedList::new)));
        }
    }

    record Let(List<KeyPair> pairs, Form body) implements Form {
        @Override
        public Value accept(AtomTable table) {
            var valBinds = pairs.stream().map(keyPair ->
                    new Let.ValuePair(keyPair.key(), keyPair.value().accept(table))
            ).collect(Collectors.toCollection(LinkedList::new));
            var valBody = body.accept(table);
            return new Value.Let(table, valBinds, valBody);
        }

        public record KeyPair(String key, Form value) {
        }

        public record ValuePair(String key, Value value) {
        }
    }
}
