package com.github.imaqtkatt.lexical;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;

public final class Lexer implements Iterator<Token> {
    private final String input;
    private final int length;

    private int index;
    private int start;

    public Lexer(String input) {
        this.length = input.length();
        this.input = input;
        this.index = 0;
        this.start = 0;
    }

    @Override
    public boolean hasNext() {
        return this.index < this.length;
    }

    @Override
    public Token next() {
//        if (this.index >= this.length) {
//            return new Token(TokenType.EOF, "");
//        } else {
            return this.nextToken();
//        }
    }

    private Optional<Character> peek() {
        if (this.index >= this.length) {
            return Optional.empty();
        } else {
            return Optional.of(this.input.charAt(this.index));
        }
    }

    private Optional<Character> advance() {
        if (this.index >= this.length) {
            return Optional.empty();
        } else {
            return Optional.of(this.input.charAt(this.index++));
        }
    }

    private void advanceWhile(Function<Character, Boolean> predicate) {
        Optional<Character> character;
        while ((character = this.peek()).isPresent()) {
            var inner = character.get();
            if (predicate.apply(inner)) {
                var ignored = this.advance();
            } else {
                break;
            }
        }
    }

    private void whitespaces() {
        this.advanceWhile(Character::isWhitespace);
    }

    private Token nextToken() {
        this.whitespaces();
        this.start = this.index;

        var character = this.advance();

        TokenType type;
        if (character.isEmpty()) {
            type = TokenType.EOF;
        } else {
            var inner = character.get();
            if (inner.equals('(')) {
                type = TokenType.L_PARENS;
            } else if (inner.equals(')')) {
                type = TokenType.R_PARENS;
            } else if (Character.isDigit(inner)) {
                this.advanceWhile(Character::isDigit);
                type = TokenType.NUMBER;
            } else if (Character.isAlphabetic(inner)) {
                this.advanceWhile(Character::isAlphabetic);
                type = TokenType.IDENTIFIER;
            } else {
                type = TokenType.ERROR;
            }
        }
        var lexeme = this.input.substring(this.start, this.index);
        return new Token(type, lexeme);
    }
}