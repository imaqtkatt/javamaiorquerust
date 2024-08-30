package com.github.imaqtkatt.syntax;

import com.github.imaqtkatt.lexical.Lexer;
import com.github.imaqtkatt.lexical.Token;
import com.github.imaqtkatt.lexical.TokenType;

import java.util.LinkedList;
import java.util.List;

public final class Parser {
    private Token current;
    private Token next;
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        this.current = lexer.next();
        this.next = lexer.next();
        this.lexer = lexer;
    }

    private TokenType type() {
        return this.current.type();
    }

    private Token advance() {
        var temp = this.current;
        this.current = this.next;
        this.next = lexer.next();
        return temp;
    }

    private Token expect(TokenType expected) throws Exception {
        if (this.current.type() == expected) {
            return this.advance();
        } else {
            throw new Exception("Expected " + expected + "but found '" + this.current.lexeme() + "'");
        }
    }

    public Node node() throws Exception {
        return switch (type()) {
            case L_PARENS -> list();
            case NUMBER -> number();
            case IDENTIFIER -> identifier();
            case R_PARENS -> throw new Exception("Syntax error, unexpected ')'");
            case ERROR -> throw new Exception("Syntax error, found '" + this.current.lexeme() + "'");
            case EOF -> throw new Exception("Syntax error, reached EOF.");
        };
    }

    public Program program() throws Exception {
        List<Node.List> topLevels = new LinkedList<>();
        while (!(type() == TokenType.EOF)) {
            topLevels.add(this.list());
        }
        expect(TokenType.EOF);
        return new Program(topLevels);
    }

    private Node.List list() throws Exception {
        expect(TokenType.L_PARENS);
        var elements = new LinkedList<Node>();
        while (!(type() == TokenType.R_PARENS)) {
            elements.add(node());
        }
        expect(TokenType.R_PARENS);

        return new Node.List(elements);
    }

    private Node.Number number() throws Exception {
        var number = expect(TokenType.NUMBER);
        return new Node.Number(Integer.valueOf(number.lexeme()));
    }

    private Node.Identifier identifier() throws Exception {
        var identifier = expect(TokenType.IDENTIFIER);
        return new Node.Identifier(identifier.lexeme());
    }
}
