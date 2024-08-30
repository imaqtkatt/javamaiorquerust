package com.github.imaqtkatt.value;

import com.github.imaqtkatt.analysis.Form;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public sealed interface Value {
    Value NULL = new Value.Object(null);

    java.lang.Object eval();

    record Object(java.lang.Object o) implements Value {
        @Override
        public java.lang.Object eval() {
            return o;
        }
    }

    record Atom(AtomTable table, String name) implements Value {
        @Override
        public java.lang.Object eval() {
            if (table.isDefined(name)) {
                return table.fetch(name);
            } else {
                throw new RuntimeException("Undefined " + name);
            }
        }
    }

    record Lambda(AtomTable table, List<String> binds, Value body) implements Value {
        @Override
        public java.lang.Object eval() {
            return (LambdaFn) args -> {
                if (args.size() != binds.size()) {
                    throw new RuntimeException("Arity error");
                }

                var retrieve = binds.stream().map(table::fetch).collect(Collectors.toCollection(LinkedList::new));

                for (int i = 0; i < args.size(); i++) {
                    table.define(binds.get(i), args.get(i));
                }
                var retVal = body.eval();
                for (int i = 0; i < retrieve.size(); i++) {
                    table.define(binds.get(i), retrieve.get(i));
                }

                return retVal;
            };
        }
    }


    record Expr(Value head, List<Value> tail) implements Value {
        @Override
        public java.lang.Object eval() {
            var hd = head.eval();
            if (hd instanceof SumatraFn fn) {
                return fn.apply(tail).eval();
            } else if (hd instanceof LambdaFn fn) {
                return fn.apply(tail);
            } else {
                throw new RuntimeException("Call to non function <" + hd + ">");
            }
        }
    }

    record Do(List<Value> values) implements Value {
        @Override
        public java.lang.Object eval() {
            java.lang.Object last = NULL;
            for (Value value : values) {
                last = value.eval();
            }
            return last;
        }
    }

    record Let(AtomTable table, List<Form.Let.ValuePair> pairs, Value body) implements Value {
        @Override
        public java.lang.Object eval() {
            var retrieve = pairs.stream().map(pair -> table.fetch(pair.key()))
                    .collect(Collectors.toCollection(LinkedList::new));
            for (var pair : pairs) {
                table.define(pair.key(), pair.value().eval());
            }
            var retVal = body.eval();
            for (int i = 0; i < retrieve.size(); i++) {
                table.define(pairs.get(i).key(), retrieve.get(i));
            }
            return retVal;
        }
    }
}
