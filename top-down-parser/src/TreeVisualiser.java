import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TreeVisualiser {
    private Tree tree;
    private Map<Tree, Integer> labels;

    TreeVisualiser(Tree tree) {
        this.tree = tree;
        labels = new HashMap<>();
    }

    void visualize(String filename) throws IOException {
        labels.clear();
        int counter = 0;
        PrintWriter writer = new PrintWriter(new FileOutputStream("gvFiles/" + filename + ".gv"), true);
        writer.println("graph tree {");

        ArrayDeque<Tree> queue = new ArrayDeque<>();
        queue.addLast(tree);

        while (!queue.isEmpty()) {
            Tree node = queue.pollFirst();

            if (!labels.containsKey(node)) {
                writer.println("\t" + counter + " [label = \"" + node.getNode() + "\"];");
                labels.put(node, counter++);
            }

            List<Tree> children = node.getChildren();
            if (children != null) {
                for (Tree child : children) {
                    queue.addLast(child);
                    writer.println("\t" + counter + " [label = \"" + child.getNode() + "\"];");
                    labels.put(child, counter++);
                    writer.println("\t" + labels.get(node) + " -- " + labels.get(child) + ";");
                }
            }


        }
        writer.println("}");

        Runtime.getRuntime().exec("dot -Tpng gvFiles/" + filename + ".gv -o results/" + filename + ".png");
    }
}
