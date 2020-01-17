package ru.ifmo.rain.zakharevich.examples.regexp;

import gen.results.regexp.RegexpLexer;
import gen.results.regexp.RegexpParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Main {

    static final String TEMP_FILES_FOLDER = "gvFiles";
    static final String RESULT_FILES_FOLDER = "results";

    public static void main(String[] args) throws IOException {
        try {
            createOrClearDirectory(TEMP_FILES_FOLDER);
            createOrClearDirectory(RESULT_FILES_FOLDER);
        } catch (FileNotFoundException e) {
            return;
        }
        for (Test test : getTests()) {
            var lexer = new RegexpLexer(test.getData());
            var parser = new RegexpParser(lexer);

            var tree = parser.expr();

            TreeVisualiser treeVisualiser = new TreeVisualiser(tree);
            treeVisualiser.visualize(test.getName());
        }
    }

    private static void createOrClearDirectory(String dirName) throws FileNotFoundException {
        File dir = new File(dirName);
        if (dir.exists()) {
            File[] files = Objects.requireNonNull(dir.listFiles());
            for (File entry : files) {
                if (!entry.delete()) {
                    System.out.println("Failed to remove " + dirName + "/" + entry.getName());
                    throw new FileNotFoundException();
                }
            }
            if (!dir.delete()) {
                System.out.println("Failed to remove " + dirName + " directory");
                throw new FileNotFoundException();
            }
        }

        if (!dir.mkdir()) {
            System.out.println("Failed to create " + dirName + " directory");
            throw new FileNotFoundException();
        }
    }

    private static List<Test> getTests() {
        return new ArrayList<>(Arrays.asList(
                new Test("double_closure_and_option", "a**|b**"),
                new Test("double_closure_and_concat", "a**b"),
                new Test("skip_spaces", "a b      c  d"),
                new Test("choice_operator", "(a|b|c(a|b|c)(ab|c|d))"),
                new Test("concatenation", "((abcd)de)afd"),
                new Test("concatenation_long", "aasdgasqdfafqjwehflkjasdhflkadsjhflasdhflkjasdhfilwfljdaslgoiepudsjfgoiperfjlkads"),
                new Test("kleene_closure", "z*x*d*"),
                new Test("kleene_closure_with_parenthesis", "(((ab)*)c*)d*"),
                new Test("all_operators", "(a(b*)|((a*|b)*)(p)ab)*"),
                new Test("all_operators_and_spaces", "(s     (f*)|  ((  z * |x)*)(e)cb  )*")
        ));
    }
}
