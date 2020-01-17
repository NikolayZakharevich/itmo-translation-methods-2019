import java.io.*;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException {
        Parser parser = new Parser();

        try {
            createOrClearDirectory("./gvFiles");
            createOrClearDirectory("./results");
        } catch (FileNotFoundException e) {
            return;
        }
        for (Test test: getTests()) {
            try {
                Tree tree = parser.parse(new ByteArrayInputStream(test.getData().getBytes(Charset.forName("UTF-8"))));
                TreeVisualiser treeVisualiser = new TreeVisualiser(tree);
                treeVisualiser.visualize(test.getName());
            } catch (ParseException e) {
                System.out.println(e.getMessage() + " at position " + e.getErrorOffset());
            }
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
                new Test("fail_1", "b|**b"),
                new Test("fail_2", "*b"),
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
