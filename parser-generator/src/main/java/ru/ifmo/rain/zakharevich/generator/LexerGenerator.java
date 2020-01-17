package ru.ifmo.rain.zakharevich.generator;

import ru.ifmo.rain.zakharevich.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LexerGenerator extends Generator {

    public LexerGenerator(Grammar grammar) {
        super(grammar, "Lexer");
    }

    @Override
    void generateCode() throws FileNotFoundException {
        generateEnum();
        generateLexer();
    }

    private void generateEnum() throws FileNotFoundException {
        var writer = new PrintWriter(new File(pathDir.toString(), "Symbol.java"));
        var nonTerminalStrings = grammar.nonTerminals.values().stream().map(x -> "    " + x.toString() + "(false)");
        var terminalStrings = grammar.terminals.values().stream().map(x -> "    " + x.toString() + "(true)");
        var symbols = Stream.concat(nonTerminalStrings, terminalStrings).collect(Collectors.joining(",\n"));
        writer.write(header + "\n" +
                "public enum Symbol {\n" + symbols + ",\n" +
                "    EOF(true),\n" +
                "    DUMMY(true);\n" +
                "                    \n" +
                "    private final boolean isTerminal;\n" +
                "\n" +
                "    Symbol(final boolean isTerminal) {\n" +
                "        this.isTerminal = isTerminal;\n" +
                "    }\n" +
                "\n" +
                "    public boolean isTerminal() {\n" +
                "        return isTerminal;\n" +
                "    }\n" +
                "}\n" +
                "\n"
        );
        writer.close();
    }

    private void generateLexer() {
        var skipTokens = grammar.terminals.values().stream()
                .filter(t -> t.skip)
                .map(x -> "Symbol." + x)
                .collect(Collectors.joining(", "));


        var terminals = grammar.terminals.values().stream()
                .map(x -> "    new TokenSymbol(Symbol." + x + ", Pattern.compile(" + "\"" +
                        x.options.stream()
                                .map(Pair::getFirst)
                                .map(Pattern::toString)
                                .collect(Collectors.joining("|")) + "\"" +
                        "))\n")
                .collect(Collectors.joining(", "));

        var joinAllPatterns = grammar.terminals.values()
                .stream()
                .map(x -> "(" + x.options.stream().map(Pair::getFirst).map(Pattern::toString).collect(Collectors.joining("|")) + ")")
                .collect(Collectors.joining("|"));

        print(header + "\n" +
                "import java.util.Arrays;\n" +
                "import java.util.List;\n" +
                "import java.util.Set;\n" +
                "import java.util.regex.Matcher;\n" +
                "import java.util.regex.Pattern;\n" +
                "\n" +
                "public class " + grammar.name + "Lexer {\n" +
                "\n" +
                "    private final String input;\n" +
                "    private final int length;\n" +
                "    private Token curToken;\n" +
                "    private StringBuilder word;\n" +
                "    private int index;\n" + "\n" +
                "    public static class Token {\n" +
                "        private final Symbol rule;\n" +
                "        private final String text;\n" +
                "\n" +
                "        public Token(final Symbol rule, final String text) {\n" +
                "            this.rule = rule;\n" +
                "            this.text = text;\n" +
                "        }\n" +
                "\n" +
                "        public Symbol getSymbol() {\n" +
                "            return rule;\n" +
                "        }\n" +
                "\n" +
                "        public String getText() {\n" +
                "            return text;\n" +
                "        }\n" +
                "    }" +
                "\n" +
                "\n" +
                "    private static class TokenSymbol {\n" +
                "        private final Symbol rule;\n" +
                "        private final Pattern pattern;\n" +
                "\n" +
                "        private TokenSymbol(Symbol rule, Pattern pattern) {\n" +
                "            this.rule = rule;\n" +
                "            this.pattern = pattern;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    public static class LexicalException extends RuntimeException {\n" +
                "        public LexicalException(String message) {\n" +
                "            super(message);\n" +
                "        }\n" +
                "    }" +
                "\n" +
                "    private final Pattern oneToSymbolThemAll = Pattern.compile(\"" + joinAllPatterns + "\");\n" +
                "    \n" +
                "    private final List<TokenSymbol> tokenSymbols = Arrays.asList(\n" + terminals +
                "    );\n" +
                "    \n" +
                "    private Set<Symbol> ignore = Set.of(" + skipTokens + ");\n" +
                "\n" +
                "    private Symbol find(final String s) {\n" +
                "        Symbol result = Symbol.DUMMY;\n" +
                "        for (final TokenSymbol r : tokenSymbols) {\n" +
                "            if (r.pattern.matcher(s).matches()) {\n" +
                "                result = r.rule;\n" +
                "                break;\n" +
                "            }\n" +
                "        }\n" +
                "        return result;\n" +
                "    }" +
                "\n" +
                "    public " + grammar.name + "Lexer(final String input) {\n" +
                "        this.input = input;\n" +
                "        this.word = new StringBuilder();\n" +
                "        this.curToken = new Token(Symbol.DUMMY, \"\");\n" +
                "        this.index = 0;\n" +
                "        this.length = input.length();\n" +
                "    }\n" +
                "\n" +
                "    public Token getCurToken() {\n" +
                "        return curToken;\n" +
                "    }\n" +
                "\n" +
                "    public void nextToken() {\n" +
                "        word = new StringBuilder();\n" +
                "        if (index == length) {\n" +
                "            curToken = new Token(Symbol.EOF, \"\");\n" +
                "            return;\n" +
                "        }\n" +
                "        char cur = input.charAt(index);\n" +
                "        index++;\n" +
                "        word.append(cur);\n" +
                "        String s = word.toString();\n" +
                "        Symbol t = find(s);\n" +
                "        Matcher m = oneToSymbolThemAll.matcher(s);\n" +
                "        if (m.matches()) {\n" +
                "            while (index < length) {\n" +
                "                cur = input.charAt(index);\n" +
                "                index++;\n" +
                "                word.append(cur);\n" +
                "                s = word.toString();\n" +
                "                t = find(s);\n" +
                "                m = oneToSymbolThemAll.matcher(s);\n" +
                "                if (!m.matches()) {\n" +
                "                    index--;\n" +
                "                    s = s.substring(0, s.length() - 1);\n" +
                "                    t = find(s);\n" +
                "                    if (ignore.contains(t)) {\n" +
                "                        nextToken();\n" +
                "                        return;\n" +
                "                    }\n" +
                "                    break;\n" +
                "                }\n" +
                "            }\n" +
                "            curToken = new Token(t, s);\n" +
                "        } else {\n" +
                "            throw new LexicalException(\"Unexpected token \" + word.toString());\n" +
                "        }\n" +
                "    }\n" +
                "}"
        );
    }

}
