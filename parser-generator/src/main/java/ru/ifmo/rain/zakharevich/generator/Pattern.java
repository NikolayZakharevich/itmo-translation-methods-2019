package ru.ifmo.rain.zakharevich.generator;

public class Pattern {

    String pattern;

    String quantifier;

    boolean isCharset;

    Pattern(String pattern, String quantifier, boolean isCharset) {
        this.pattern = pattern;
        this.quantifier = quantifier;
        this.isCharset = isCharset;
    }

    @Override
    public String toString() {
        if (isCharset) {
            return "[" + escape(pattern) + "]" + (quantifier != null ? quantifier : "");
        }
        return escape(pattern);
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("|", "\\\\|")
                .replace("(", "\\\\(")
                .replace(")", "\\\\)")
                .replace("*", "\\\\*")
                .replace("+", "\\\\+")
                .replace("?", "\\\\?")
                .replace("\"", "\\\"");
    }
}
