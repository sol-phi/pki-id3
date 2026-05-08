package de.uni_trier.wi2.pki.tree;

import java.util.*;

public class DecisionTree extends DecisionTreeNode {

    /**
     * Constructor for decision tree.
     *
     * @param attributeIndex Index for current attribute.
     * @param elements       Elements for current node.
     */
    public DecisionTree(int attributeIndex, Collection<Object[]> elements) {
        super(null, elements, attributeIndex);
    }

    /**
     * Predict the class of a single example.
     *
     * @param example the attribute array of the example to predict.
     * @return the predicted class as a string
     */
    public String predict(Object[] example) {
        // This method is always called with the topmost node
        DecisionTreeNode currentNode = this;

        // Gets the value in examples for the node's attribute, and wanders down to the child node with that instance. Repeat.
        while (currentNode instanceof DecisionTree) {
            DecisionTree tree = (DecisionTree) currentNode;
            int attrIndex = tree.getAttributeIndex();
            String value = example[attrIndex].toString();
            DecisionTreeNode next = tree.getSplits().get(value);

            // If no child has that value (because it wasn't present in the training data),
            // pick the largest branch as it contains the most information.
            if (next == null) {
                next = Collections.max(
                        tree.getSplits().entrySet(),
                        Comparator.comparingInt(e -> e.getValue().getElements().size())
                ).getValue();
            }

            currentNode = next;
        }

        // The while-loop terminates because we arrived at the last node in the tree, which by definition is a leaf node.
        return ((DecisionTreeLeafNode) currentNode).getLabelClass();
    }

    /**
     * Predict the class of multiple examples.
     *
     * @param examples the list of examples
     * @return a list of string values that represent the predicted classes
     */
    public List<String> predictAll(List<Object[]> examples) {
        ArrayList<String> results = new ArrayList<>();

        for (Object[] example : examples) {
            results.add(predict(example));
        }

        return results;
    }

}