package ru.ifmo.rain.zakharevich.generator;

import ru.ifmo.rain.zakharevich.util.Pair;

import java.util.List;

public class Terminal extends Symbol {

    boolean skip;

    // pattern, code
    List<Pair<Pattern, String>> options;

    Terminal(String name) {
        super(name);
    }

    Terminal(String name, List<Pair<Pattern, String>> options, boolean skip) {
        super(name);
        this.skip = skip;
        this.options = options;
    }

    public List<Pair<Pattern, String>> getOptions() {
        return options;
    }

}
