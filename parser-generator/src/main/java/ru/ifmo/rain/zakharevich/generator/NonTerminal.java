package ru.ifmo.rain.zakharevich.generator;

import java.util.List;

public class NonTerminal extends Symbol {

    String argument;

    List<Rule> rules;

    String args;


    public NonTerminal(String name, String args) {
        super(name);
        this.name = name;
        this.args = args;
    }

    public NonTerminal(String name, List<Rule> rules, String argument) {
        super(name);
        this.rules = rules;
        this.argument = argument;
    }

    public String getArgument() {
        return argument;
    }

    public List<Rule> getRules() {
        return rules;
    }
}
