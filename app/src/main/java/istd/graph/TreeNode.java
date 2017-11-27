package istd.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by yaojie on 24/11/17.
 */

public class TreeNode {
    private Vertex node;
    private List<TreeNode> children;
    private Stack<Vertex> stack;

    public TreeNode(){
        children = new ArrayList<>();
        stack = new Stack<>();
    }

    public TreeNode(Vertex node){
        this();
        this.node = node;
    }
    public void addChild(Vertex node){
        children.add(new TreeNode(node));
    }

    public Stack<Vertex> getStack() {
        return stack;
    }

    public void traverseTreeAndAdd(Vertex source, Vertex destination){
        if (node.equals(source)){
            addChild(destination);
        } else {
            for (TreeNode child: children){
                child.traverseTreeAndAdd(source, destination);
            }
        }
    }

    public void preorder(){
        stack.clear();
        stack.push(node);
        for (TreeNode child: children){
            preorder(child);
        }
    }

    private void preorder(TreeNode node){
        stack.push(node.node);
        for (TreeNode child: node.children){
            preorder(node);
        }
    }
}
