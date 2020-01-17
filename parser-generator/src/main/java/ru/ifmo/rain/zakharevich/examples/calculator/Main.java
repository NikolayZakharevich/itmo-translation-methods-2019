package ru.ifmo.rain.zakharevich.examples.calculator;

import gen.results.calculator.CalculatorLexer;
import gen.results.calculator.CalculatorParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        getTests().stream().map(Test::run).forEach(System.out::println);
    }
    private static List<Test> getTests() {
        return new ArrayList<>(Arrays.asList(
                new Test("2 + 4 * 5", 22),
                new Test("1 - 2 - 3", -4),
                new Test("2 * 3 - 4",2),
                new Test("3 - -3", 6),
                new Test("10 / 2 + 3", 8),
                new Test("0", 0),
                new Test("(1+2)*30", 90),
                new Test("1*2*3*4*5*6*7", 5040),
                new Test("60 / 2 / 5", 6),
                new Test("5!", 120),
                new Test("3!!", 720),
                new Test("3!! + 5!", 840)

        ));
    }

    private static class Test {
        String input;
        int value;

        Test(String input, int value) {
            this.input = input;
            this.value = value;
        }

        String run() {
            var lexer = new CalculatorLexer(input);
            var parser = new CalculatorParser(lexer);
            var res = parser.expr().val;
            return res == value ?  "OK" : "FAIL. Expected: " + value + ", found: " + res;
        }
    }
}
