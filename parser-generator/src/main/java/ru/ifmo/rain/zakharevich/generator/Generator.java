package ru.ifmo.rain.zakharevich.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

abstract public class Generator {

    static final String fourSpaces = "    ";

    Grammar grammar;

    String filename;

    Path pathDir;

    PrintWriter printer;

    String header;

    Generator(Grammar grammar, String filename) {
        this.grammar = grammar;
        this.filename = filename;
        this.header = grammar.header.isEmpty() ? "package gen.results." + grammar.name.toLowerCase() + ";" : grammar.header;
    }

    public void generate(String folderName) throws IOException {
        pathDir = Paths.get(folderName).resolve("gen.results").resolve(grammar.name.toLowerCase());
        Files.createDirectories(pathDir);
        String fileName = grammar.name + filename + ".java";
        printer = new PrintWriter(new File(pathDir.toString(), fileName));
        generateCode();
        printer.close();
    }

    abstract void generateCode() throws FileNotFoundException;

    void print(String code) {
        printer.write(code);
    }

    static String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("|", "\\\\|")
                .replace("(", "\\\\(")
                .replace(")", "\\\\)")
                .replace("*", "\\\\*")
                .replace("+", "\\\\+")
                .replace("?", "\\\\?")
                .replace("\"", "\\\"");
    }

    String indent(int n) {
        return fourSpaces.repeat(n);
    }
}
