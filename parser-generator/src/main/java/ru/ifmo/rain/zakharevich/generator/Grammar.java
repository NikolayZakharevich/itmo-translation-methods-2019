package ru.ifmo.rain.zakharevich.generator;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import ru.ifmo.rain.zakharevich.generator.parser.GrammarLexer;
import ru.ifmo.rain.zakharevich.generator.parser.GrammarParser;

import java.io.*;
import java.nio.channels.ReadableByteChannel;
import java.util.*;
import java.util.stream.Collectors;

public class Grammar {

    static final String EPSILON = "eps";
    private String END = "EOF";
    static final String NO_ARGUMENT = "";


    String name;
    Map<String, NonTerminal> nonTerminals;
    Map<String, Terminal> terminals;
    NonTerminal start;
    String header;
    String fields;

    Map<String, Set<String>> first = new HashMap<>();
    Map<String, Set<String>> follow = new HashMap<>();
    HashMap<String, ArrayList<Set<String>>> firstAp = new HashMap<>();


    private Grammar(String languageName, List<Terminal> terminals, List<NonTerminal> nonTerminals, NonTerminal startSymbol, String header, String fields) {
        this.name = languageName;
        this.terminals = terminals.stream().collect(Collectors.toMap(x -> x.name, x -> x));
        this.nonTerminals = nonTerminals.stream().collect(Collectors.toMap(x -> x.name, x -> x));
        this.start = startSymbol;
        this.header = header;
        this.fields = fields;

        for (var entry : nonTerminals) {
            var name = entry.name;
            Set<String> firstEmpty = new HashSet<>();
            Set<String> followEmpty = new HashSet<>();
            first.put(name, firstEmpty);
            follow.put(name, followEmpty);
        }
    }

    public static Grammar of(ReadableByteChannel channel) throws IOException {
        var lexer = new GrammarLexer(CharStreams.fromChannel(channel));
        var tokens = new CommonTokenStream(lexer);
        var parser = new GrammarParser(tokens);
        var visitor = new GrammarBaseVisitorImpl();
        var root = parser.metaGrammar();

        var header = visitor.getHeader(root);
        var fields = visitor.getFields(root);
        var nonTerminals = visitor.getNonTerminals(root.productionRules);
        var terminals = visitor.getTerminals(root.lexemRules);

        terminals.add(new Terminal(EPSILON, new ArrayList<>(), false));
        var startSymbol = nonTerminals.get(0);
        var grammar = new Grammar(visitor.getName(root), terminals, nonTerminals, startSymbol, header, fields);
        grammar.validate();
        grammar.findFirstAndFollow();
        return grammar;
    }

    public List<Terminal> getTerminals() {
        return new ArrayList<>(terminals.values());
    }


    public List<NonTerminal> getNonTerminals() {
        return new ArrayList<>(nonTerminals.values());
    }

    public String getName() {
        return name;
    }

    private boolean isNotEqual(Map<String, Set<String>> a, Map<String, Set<String>> b) {
        if (a == null) {
            return true;
        }

        for (var entry : nonTerminals.entrySet()) {
            var symbol = entry.getValue();
            if (!a.get(symbol.name).containsAll(b.get(symbol.name))) {
                return true;
            }
        }
        return false;
    }

    private Set<String> getFirst(Rule a) {
        var curr = new HashSet<String>();
        if (a.producing.size() == 1 &&
                (a.producing.get(0) instanceof Terminal &&
                        (a.producing.get(0)).name.equals(EPSILON))) {
            curr.add(EPSILON);
            return curr;
        }
        if (a.producing.get(0) instanceof Terminal) {
            curr.add(a.producing.get(0).name.toUpperCase());
            return curr;
        }
        curr.addAll(first.get(a.producing.get(0).name));
        if (curr.contains(EPSILON)) {
            curr.remove(EPSILON);
            if (a.producing.size() > 1) {
                List<Symbol> symbols = new ArrayList<>();
                for (int i = 1; i < a.producing.size(); i++) {
                    symbols.add(a.producing.get(i));
                }
                Rule currRule = new Rule(symbols);
                curr.addAll(getFirst(currRule));
            }
        }
        return curr;
    }

    private Map<String, Set<String>> copy(Map<String, Set<String>> a) {
        var b = new HashMap<String, Set<String>>();
        for (String s : a.keySet()) {
            Set<String> curr = new HashSet<>(a.get(s));
            b.put(s, curr);
        }
        return b;
    }

    private void findFirst() {
        Map<String, Set<String>> firstA = null;
        while (isNotEqual(firstA, first)) {
            firstA = copy(first);
            nonTerminals.forEach((name, symbol) -> {
                List<Rule> rules = symbol.rules;
                for (var rule : rules) {
                    var ansSet = first.get(symbol.name);
                    ansSet.addAll(getFirst(rule));
                    first.put(symbol.name, ansSet);
                }
            });
        }
    }

    private void findFollow() {
        follow.get(start.name).add(END);
        Map<String, Set<String>> followA = null;
        while (isNotEqual(followA, follow)) {
            followA = copy(follow);
            for (var entry : nonTerminals.entrySet()) {
                var symbol = entry.getValue();
                for (Rule rule : symbol.rules) {
                    for (int i = 0; i < rule.producing.size() - 1; i++) {
                        if (rule.producing.get(i) instanceof NonTerminal) {
                            Set<String> ansSet = new HashSet<>(follow.get(rule.producing.get(i).name));
                            if (rule.producing.get(i + 1) instanceof Terminal) {
                                ansSet.add((rule.producing.get(i + 1)).name.toUpperCase());
                            } else {
                                Set<String> currFst = first.get(rule.producing.get(i + 1).name);
                                ansSet.addAll(currFst);
                                if (currFst.contains(EPSILON)) {
                                    ansSet.remove(EPSILON);
                                    Set<String> curr = follow.get(symbol.name);
                                    ansSet.addAll(curr);
                                }
                            }
                            follow.put(rule.producing.get(i).name, ansSet);
                        }
                    }
                    int t = rule.producing.size() - 1;
                    if (rule.producing.get(t) instanceof NonTerminal) {
                        Set<String> ansSet = new HashSet<>(follow.get(rule.producing.get(t).name));
                        Set<String> curr = follow.get(symbol.name);
                        ansSet.addAll(curr);
                        follow.put(rule.producing.get(t).name, ansSet);
                    }
                }
            }
        }
    }

    private Set<String> findFirstForRule(Rule rule, NonTerminal a) {
        var result = new HashSet<String>();
        Symbol currSymbol = rule.producing.get(0);
        int ind = 0;
        while (currSymbol instanceof NonTerminal && first.get(currSymbol.name).contains(EPSILON)) {
            result.addAll(first.get(currSymbol.name));
            result.remove(EPSILON);
            currSymbol = rule.producing.get(ind++);
            if (ind == rule.producing.size()) {
                break;
            }
        }
        if (ind == rule.producing.size()) {
            result.addAll(follow.get(a.name));
        }
        if (ind < rule.producing.size()) {
            if (currSymbol instanceof NonTerminal) {
                result.addAll(first.get(currSymbol.name));
            } else {
                if (currSymbol.name.equals(EPSILON)) {
                    result.addAll(follow.get(a.name));
                } else {
                    result.add(currSymbol.name.toUpperCase());
                }
            }
        }
        return result;
    }

    private void findFirstAp() {
        nonTerminals.forEach((name, nonTerminal) -> {
            ArrayList<Set<String>> currAns = new ArrayList<>();
            for (var rule : nonTerminal.rules) {
                currAns.add(findFirstForRule(rule, nonTerminal));
            }
            firstAp.put(nonTerminal.name, currAns);
        });
    }

    private void findFirstAndFollow() {
        findFirst();
        findFollow();
        findFirstAp();
    }

    private void validate() {
        nonTerminals.forEach((key, value) -> value.rules.forEach(rule ->
                rule.producing.forEach(opt -> {
                    if (!terminals.keySet().contains(opt.name) && !nonTerminals.keySet().contains(opt.name)) {
                        throw new IllegalStateException("Unknown symbol " + opt.name + " in rule " + rule);
                    }
                })));
    }
}
