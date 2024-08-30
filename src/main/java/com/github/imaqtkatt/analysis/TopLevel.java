package com.github.imaqtkatt.analysis;

import com.github.imaqtkatt.value.AtomTable;

public sealed interface TopLevel {
    record Invalid() implements TopLevel {
    }

    record Eval(Form form) implements TopLevel, Evaluate {
        @Override
        public Object eval(AtomTable table) {
            return form.accept(table).eval();
        }
    }

    record Def(String name, Form body) implements TopLevel, Define {
        @Override
        public void define(AtomTable table) {
            if (table.isDefined(name)) {
                throw new RuntimeException("Redefinition of " + name + ".");
            } else {
                table.define(name, body.accept(table).eval());
            }
        }
    }
}
