package com.github.imaqtkatt;

import com.github.imaqtkatt.lexical.Lexer;
import com.github.imaqtkatt.syntax.Parser;
import com.github.imaqtkatt.syntax.ToAnalyzedProgram;
import com.github.imaqtkatt.value.AtomTable;

public class Main {
    public static void main(String[] args) {
        var input = """
                (eval (add 1 2 3 4))
                """;
        var parser = new Parser(new Lexer(input));

        try {
            var program = parser.program();
            var analyzed = ToAnalyzedProgram.program(program);
            var defineList = analyzed.defineList();
            var evaluateList = analyzed.evaluateList();
            for (var def : defineList) {
                def.define(AtomTable.getInstance());
            }
            for (var eval : evaluateList) {
                var result = eval.eval(AtomTable.getInstance());
                System.out.println(":> " + result);
            }
        } catch (Exception e) {
            System.err.println("Error = " + e.getMessage());
        }
    }
}