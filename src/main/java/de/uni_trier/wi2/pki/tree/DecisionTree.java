package de.uni_trier.wi2.pki.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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


        return leafNode.getLabelClass();
    }

    /**
     * Predict the class of multiple examples.
     *
     * @param examples the list of examples
     * @return a list of string values that represent the predicted classes
     */
    public List<String> predictAll(List<Object[]> examples) {
        ArrayList<String> results = new ArrayList<>();




        return results;
    }

}