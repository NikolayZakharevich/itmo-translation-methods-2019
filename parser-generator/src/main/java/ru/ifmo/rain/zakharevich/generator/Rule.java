package ru.ifmo.rain.zakharevich.generator;

import java.util.List;

class Rule {

    List<Symbol> producing;

    String code;

    Rule(List<Symbol> producing, String code) {
        this.producing = producing;
        this.code = code;
    }

    Rule(List<Symbol> producing) {
        this.producing = producing;
    }
}
