package ru.ifmo.rain.zakharevich.generator;

public class Symbol {

    String name;

    public Symbol(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Symbol)) {
            return false;
        }
        return name.equals(((Symbol) obj).name);
    }
}
