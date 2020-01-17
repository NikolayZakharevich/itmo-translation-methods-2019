package ru.ifmo.rain.zakharevich.parser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {
        BufferedReader correct;
        try {
            var test = new StringBuilder();
            String line;
            correct = new BufferedReader(new FileReader("src/main/resources/tests/test1"));
            while ((line = correct.readLine()) != null) {
                test.append(line).append("\n");
            }
            var lexer = new CalculatorLexer(CharStreams.fromString(test.toString()));
            var tokens = new CommonTokenStream(lexer);
            var parser = new CalculatorParser(tokens);
            parser.s().results.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

