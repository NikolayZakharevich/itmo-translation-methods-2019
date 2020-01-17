package ru.ifmo.rain.zakharevich.examples.regexp;

import gen.results.regexp.RegexpParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TreeVisualiser {
    private RegexpParser.Node tree;
    private Map<RegexpParser.Node, Integer> labels;

    TreeVisualiser(RegexpParser.Node tree) {
        this.tree = tree;
        labels = new HashMap<>();
    }

    void visualize(String filename) throws IOException {
        labels.clear();
        int counter = 0;
        PrintWriter writer = new PrintWriter(new FileOutputStream(Main.TEMP_FILES_FOLDER + "/" + filename + ".gv"), true);
        writer.println("graph tree {");

        ArrayDeque<RegexpParser.Node> queue = new ArrayDeque<>();
        queue.addLast(tree);

        while (!queue.isEmpty()) {
            RegexpParser.Node node = queue.pollFirst();

            if (!labels.containsKey(node)) {
                writer.println("\t" + counter + " [label = \"" + node.getText() + "\"];");
                labels.put(node, counter++);
            }

            List<RegexpParser.Node> children = node.getChildren();
            if (children != null) {
                for (var child : children) {
                    queue.addLast(child);
                    writer.println("\t" + counter + " [label = \"" + child.getText() + "\"];");
                    labels.put(child, counter++);
                    writer.println("\t" + labels.get(node) + " -- " + labels.get(child) + ";");
                }
            }


        }
        writer.println("}");

        Runtime.getRuntime().exec("dot -Tpng " + Main.TEMP_FILES_FOLDER + "/" + filename + ".gv -o " + Main.RESULT_FILES_FOLDER + "/" + filename + ".png");
    }
}
