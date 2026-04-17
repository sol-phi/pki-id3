package de.uni_trier.wi2.pki.util;

import de.uni_trier.wi2.pki.tree.DecisionTree;
import de.uni_trier.wi2.pki.tree.DecisionTreeLeafNode;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;

import java.util.*;

/**
 * Utility class for creating a decision tree with the ID3 algorithm.
 */
public class ID3Utils {

    /**
     * Create the decision tree given the example and the index of the label attribute.
     *
     * @param examples   The examples to train with. This is a collection of arrays.
     * @param labelIndex The label of the attribute that should be used as an index.
     * @return The root node of the decision tree
     */
    public static DecisionTree createTree(Collection<Object[]> examples, int labelIndex) {
        return createTree(examples, labelIndex, -1);
    }

    /**
     * Create the decision tree given the example and the index of the label attribute.
     *
     * @param examples     The examples to train with. This is a collection of arrays.
     * @param labelIndex   The label of the attribute that should be used as an index.
     * @param maximumDepth Maximum depth of tree.
     * @return The root node of the decision tree
     */
    public static DecisionTree createTree(Collection<Object[]> examples, int labelIndex, int maximumDepth) {
        return (DecisionTree) createTree(examples, labelIndex, maximumDepth, 1, null);
    }

    private static DecisionTreeNode createTree(Collection<Object[]> examples, int labelIndex, int maximumDepth, int currentDepth, DecisionTreeNode parent) {



        return currentNode;
    }

    /**
     * Selects the most efficient attribute.
     *
     * @param examples   The examples to train with. This is a collection of arrays.
     * @param labelIndex The label of the attribute that should be used as an index.
     * @return the index of the attribute to select next.
     */
    public static int selectEfficientAttribute(Collection<Object[]> examples, int labelIndex) {

    }

    /**
     * Determines the dominant label type for the given examples.
     *
     * @param examples   the examples to evaluate.
     * @param labelIndex the label index
     * @return the class name of the dominant class.
     */
    public static String getDominantClass(Collection<Object[]> examples, int labelIndex) {

    }

    /**
     * Compute the classification accuracy for the given decision tree and the examples.
     *
     * @param decisionTree       the decision tree to use for predictions.
     * @param validationExamples the examples to evaluate.
     * @param labelIndex         the index of the label attribute.
     * @return the classification accuracy.
     */
    public static double getClassificationAccuracy(DecisionTree decisionTree, Collection<Object[]> validationExamples, int labelIndex) {

    }
}