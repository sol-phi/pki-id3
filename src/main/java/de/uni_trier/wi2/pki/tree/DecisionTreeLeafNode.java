package de.uni_trier.wi2.pki.tree;

import java.util.Collection;

/**
 * A class for representing a leaf node in the decision tree.
 */
public class DecisionTreeLeafNode extends DecisionTreeNode {

    /**
     * The label class that is represented by this leaf node.
     */
    private final String labelClass;

    /**
     * Constructor for decision tree leaf node.
     *
     * @param parent     Parent node of current node.
     * @param elements   Elements for current node.
     * @param labelClass Class label for current node.
     */

    public DecisionTreeLeafNode(DecisionTreeNode parent, Collection<Object[]> elements, String labelClass) {
        super(parent, elements, -1);
        this.labelClass = labelClass;
    }

    @Override
    public boolean isLeafNode() {
        return true;
    }

    @Override
    public void addSplit(String Object, DecisionTreeNode decisionTreeNode) {
        // do nothing
    }

    @Override
    protected DecisionTreeNode getClassificationNode(Object[] example) {
        return this;
    }

    public String getLabelClass() {
        return labelClass;
    }

}
