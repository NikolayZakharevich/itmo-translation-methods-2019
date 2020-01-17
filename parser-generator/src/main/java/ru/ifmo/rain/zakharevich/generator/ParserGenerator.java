package ru.ifmo.rain.zakharevich.generator;

import java.io.FileNotFoundException;
import java.util.stream.Collectors;

public class ParserGenerator extends Generator {

    String lexerName;

    public ParserGenerator(Grammar grammar) {
        super(grammar, "Parser");
        lexerName = grammar.name + "Lexer";
    }

    @Override
    void generateCode() throws FileNotFoundException {

        var functions = grammar.nonTerminals.values()
                .stream()
                .map(this::generateFunction)
                .collect(Collectors.joining("\n\n"));

        var fields = grammar.fields != null ? grammar.fields : "";

        print(header + "\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.List;\n" +
                "\n" +
                "@SuppressWarnings(\"all\")\n" +
                "public class " + grammar.name + "Parser {\n" +
                "\n" +
                "    private final " + lexerName + " lexer;\n" +
                "\n" +
                "    public " + grammar.name + "Parser(final " + lexerName + " lexer) {\n" +
                "        this.lexer = lexer;\n" +
                "        lexer.nextToken();\n" +
                "    }\n" +
                "\n" +
                "    public static class Node {\n" +
                "        private final String text;\n" +
                "        private final Symbol symbol;\n" +
                fields + "\n" +
                "        private List<Node> children = new ArrayList<>();\n" +
                "\n" +
                "        public Node(final String text, final Symbol symbol) {\n" +
                "            this.text = text;\n" +
                "            this.symbol = symbol;\n" +
                "        }\n" +
                "\n" +
                "        public String getText() {\n" +
                "            return text;\n" +
                "        }\n" +
                "\n" +
                "        public Symbol getSymbol() {\n" +
                "            return symbol;\n" +
                "        }\n" +
                "\n" +
                "        public List<Node> getChildren() {\n" +
                "            return children;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    public static class ParseException extends RuntimeException {\n" +
                "        public ParseException(String message) {\n" +
                "            super(message);\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                functions + "\n" +
                "    \n" +
                "    private void consume(final Symbol expected) {\n" +
                "        Symbol actual = lexer.getCurToken().getSymbol();\n" +
                "        if (expected != actual) {\n" +
                "            throw new ParseException(\"Illegal token \" + actual.name() + \", expected \" + expected.name());\n" +
                "        }\n" +
                "        lexer.nextToken();\n" +
                "    }\n" +
                "}\n"

        );
    }

    private String generateFunction(NonTerminal symbol) {

        StringBuilder cases = new StringBuilder();
        var name = symbol.name;
        for (int i = 0; i < symbol.rules.size(); i++) {
            var rule = symbol.rules.get(i);
            for (String t : grammar.firstAp.get(name).get(i)) {
                cases.append(indent(2)).append("case ").append(t).append(":\n");
            }
            cases.append(indent(3)).append("{").append("\n");
            rule.producing.forEach(c -> {
                if (c instanceof Terminal && !c.name.equals(Grammar.EPSILON)) {
                    cases.append(indent(4))
                            .append("Node " + c.name + " = new Node(lexer.getCurToken().getText(), Symbol." + c.name + ");\n")
                            .append(indent(4) + "res.children.add(" + c.name + ");\n")
                            .append(indent(4) + "consume(Symbol." + c.name + ");\n");
                } else if (c instanceof NonTerminal) {
                    cases.append(indent(4) + "Node " + c.name + " = " + c.name + "(");
                    if (((NonTerminal) c).args != null) {
                        cases.append(((NonTerminal) c).args);
                    }
                    cases.append(");\n")
                            .append(indent(4) + "res.children.add(" + c.name + ");\n");
                }
            });
            cases.append(indent(3));
            if (rule.code != null) {
                cases.append(rule.code)
                        .append("\n");
            }
            cases.append(indent(3) + "break;\n")
                    .append(indent(3)).append("}").append("\n");

        }

        return "    public Node " + name + "(" + symbol.argument + ") {\n" +
                "        Node res = new Node(\"" + name + "\", Symbol." + name + ");\n" +
                "        Symbol currentSymbol = lexer.getCurToken().getSymbol();\n" +
                "        switch (currentSymbol) {\n" + cases +
                "            default:\n" +
                "                throw new ParseException(\"Illegal token \" + currentSymbol.name());\n" +
                "        }\n" +
                "        return res;\n" +
                "    }";

    }
}
