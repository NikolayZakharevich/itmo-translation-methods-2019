import java.util.Arrays;
import java.util.List;

class Tree {
    private final String node;

    private final List<Tree> children;

    Tree(String node, Tree... children) {
        this.node = node;
        this.children = Arrays.asList(children);
    }

    Tree(String node) {
        this.node = node;
        children = null;
    }

    String getNode() {
        return node;
    }

    List<Tree> getChildren() {
        return children;
    }
}
