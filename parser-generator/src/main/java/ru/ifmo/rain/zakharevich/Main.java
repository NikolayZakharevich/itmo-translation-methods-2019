package ru.ifmo.rain.zakharevich;

import ru.ifmo.rain.zakharevich.generator.*;
import ru.ifmo.rain.zakharevich.generator.Grammar;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        String grammarFile = "src/main/java/ru/ifmo/rain/zakharevich/examples/calculator/Calculator.gg";
//        String grammarFile = "src/main/java/ru/ifmo/rain/zakharevich/examples/regexp/Regexp.gg";
        String parserRoot = "src/main/java";
        try {
            var grammar = Grammar.of(new FileInputStream(grammarFile).getChannel());
            var lexerGenerator = new LexerGenerator(grammar);
            var parserGenerator = new ParserGenerator(grammar);
            lexerGenerator.generate(parserRoot);
            parserGenerator.generate(parserRoot);

        } catch (FileNotFoundException e) {
            System.err.println("File " + grammarFile + " not found. " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
