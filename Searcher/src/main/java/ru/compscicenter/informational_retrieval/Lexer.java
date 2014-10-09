package ru.compscicenter.informational_retrieval;

public class Lexer {

    public enum TokenType {
        AND,
        OR
    }

    public Lexer(String input) {
        text = input;
        nextI = 0;
    }

    private boolean endOfText() {
        return nextI >= text.length();
    }

    private boolean isValid(char c) {
        return (((c >= 'а') && (c <= 'я'))
                        || ((c >= 'А') && (c <= 'Я'))
                        || (c == 'ё') || (c == 'Ё') || (c == '-')
                        || ((c >= 'a') && (c <= 'z'))
                        || ((c >= 'A') && (c <= 'Z'))
        );
    }

    private boolean nextMatches(String str) {
        int lastI = nextI + str.length();
        return (lastI <= text.length() && str.equalsIgnoreCase(text.substring(nextI, lastI)));
    }

    private void skipWhitespaces() {
        while (!endOfText() && (Character.isWhitespace(text.charAt(nextI)))) {
            ++nextI;
        }
    }

    public TokenType getTokenType () {
        skipWhitespaces();
        if (nextMatches("and")) {
            nextI += 3;
            return TokenType.AND;
        }
        else if (nextMatches("or")) {
            nextI += 2;
            return TokenType.OR;
        }
        else {
            throw new IllegalArgumentException("invalid query");
        }
    }

    public String getWord() {
        skipWhitespaces();
        int startI = nextI;
        while (!endOfText() && isValid(text.charAt(nextI))) {
            ++nextI;
        }
        if (nextI == startI) {
            throw new IllegalArgumentException("invalid query");
        }
        return text.substring(startI, nextI);
    }

    public boolean hasNext() {
        skipWhitespaces();
        return !endOfText();
    }

    private final String text;
    private int          nextI;
}
