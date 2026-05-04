package de.uni_trier.wi2.pki.util;

import de.uni_trier.wi2.pki.tree.DecisionTree;
import de.uni_trier.wi2.pki.tree.DecisionTreeLeafNode;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;

import java.util.*;


/**
 * Utility class for creating a decision tree with the ID3 algorithm.
 */
public class ID3Utils_Test {

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

    // The following method is an implementation of the ID3 algorithm.
    // The resulting decision tree consists of DecisionTreeLeafNodes on all leaf nodes
    // and DecisionTrees on all inner nodes (due to recursively creating efficientAttributeNode).
    private static DecisionTreeNode createTree(Collection<Object[]> examples, int labelIndex, int maximumDepth, int currentDepth, DecisionTreeNode parent) {
        // If no examples are received, adapt the class that is most appropriate in the parent node
        if (examples == null || examples.isEmpty()) {
            String className = getDominantClass(parent.getElements(), labelIndex);
//            System.out.println("isEmptyClass: " + className);
            return new DecisionTreeLeafNode(parent, examples, className);
        }

        // If the maximum depth has been reached, stop the recursive splitting and instead return the most appropriate class for the current set of examples.
        // As currentDepth is 1, this if-block can only be entered on recursive calls,
        // which is why returning DecisionTreeLeafNode is safe in terms of the (DecisionTree) cast.
        if (maximumDepth >= 1 && currentDepth >= maximumDepth) {
            String className = getDominantClass(examples, labelIndex);
//            System.out.println("maximumDepthClass: " + className);
            return new DecisionTreeLeafNode(parent, examples, className);
        }

        // if all instances in example belong to the same class C, return a leaf node that is marked with C.
        String pureClassName = checkForPureClass(examples, labelIndex);
        if (!(pureClassName == null)) {
//            System.out.println("PureClass: " + pureClassName);
            return new DecisionTreeLeafNode(parent, examples, pureClassName);
        }

        // Within examples, select the most efficient attribute A to split examples by, and create a node for A.
        int efficientAttributeIndex = selectEfficientAttribute(examples, labelIndex);
        DecisionTree efficientAttributeNode = new DecisionTree(efficientAttributeIndex, examples);

        // If every attribute has only one unique value for all data points in examples, return early to prevent infinite recursion.
        if (efficientAttributeIndex <= -1) {
            if (parent == null) {
                // On the initial call, return a DecisionTree representing the root, satisfying the (DecisionTree) cast
                return efficientAttributeNode;
            } else {
                // On recursive calls, return a DecisionTreeLeafNode
                String className = getDominantClass(examples, labelIndex);
                return new DecisionTreeLeafNode(parent, examples, className);
            }
        }

        // Partition examples into smaller lists E[1] through E[N] based on the values of A.
        // computeIfAbsent(): if efficientAttributeValue is not present, create a new List<Object[]> entry in subsets under the Object key efficientAttributeValue.
        // If efficientAttributeValue is present in subsets, do nothing. Returns the list that now belongs to efficientAttributeValue either way.
        // On that list, add() can be called.
        Map<Object, List<Object[]>> subsets = new HashMap<>();
        for (Object[] row : examples) {
            Object efficientAttributeValue = row[efficientAttributeIndex];
            subsets.computeIfAbsent(efficientAttributeValue, k -> new ArrayList<>()).add(row);
        }

//        System.out.println(currentDepth);

        // Then, call createTree recursively with E[1] through E[N].
        // childNode represents T[1] through T[N] and is added to efficientAttributeNode A one by one, eliminating the need to store T[1] through T[N] all at once.
        for (Map.Entry<Object, List<Object[]>> subset : subsets.entrySet()) {
            DecisionTreeNode childNode = createTree(subset.getValue(), labelIndex, maximumDepth, currentDepth + 1, efficientAttributeNode);
            efficientAttributeNode.addSplit(subset.getKey().toString(), childNode);

//            System.out.println("Value: " + subset.getKey() + " - " + subset.getValue().size() + " examples");
//            for (Object[] row : subset.getValue()) {
//                System.out.println("  " + Arrays.toString(row));
//            }
        }

        // Climbing the recursion back up, return a tree that has A as the root node and the trees T1 through TN extending that root node.
        return efficientAttributeNode;
    }

    /**
     * Selects the most efficient attribute.
     *
     * @param examples   The examples to train with. This is a collection of arrays.
     * @param labelIndex The label of the attribute that should be used as an index.
     * @return the index of the attribute to select next.
     */
    public static int selectEfficientAttribute(Collection<Object[]> examples, int labelIndex) {
        // Retrieve the information gain of every single attribute
        List<Double> informationGains = EntropyUtils.calcInformationGain(examples, labelIndex);

//        System.out.println(informationGains.indexOf(Collections.max(informationGains)));

        // Extract the highest information gain and return the index of the attribute that belongs to it.
        // If every attribute has only one unique value for all data points in examples, return a warning in the form of -1
        double maxInformationGain = Collections.max(informationGains);
        if (maxInformationGain <= 0.0) return -1;
        return informationGains.indexOf(maxInformationGain);
    }

    /**
     * Determines the dominant label type for the given examples.
     *
     * @param examples   the examples to evaluate.
     * @param labelIndex the label index
     * @return the class name of the dominant class.
     */
    public static String getDominantClass(Collection<Object[]> examples, int labelIndex) {
        Map<String, Integer> absoluteClassFrequencies = new HashMap<>();
        // Runs through every row and tracks class value frequencies
        for (Object[] row : examples) {
            String classValue = row[labelIndex].toString();
            // If not present, create an entry set to 1, with the key classValue. Otherwise, overwrite classValue with classValue + 1
            absoluteClassFrequencies.merge(classValue, 1, Integer::sum);
        }

//        absoluteClassFrequencies.forEach((label, count) ->
//                System.out.println("Class " + label + ": " + count)
//        );
//        System.out.println("Dominant class: " + Collections.max(absoluteClassFrequencies.entrySet(), Map.Entry.comparingByValue()).getKey() + "");


        // Extract the class with the most entries in examples
        return Collections.max(absoluteClassFrequencies.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public static String checkForPureClass(Collection<Object[]> examples, int labelIndex) {
        List<Object[]> data = new ArrayList<>(examples);

        String initialClassValue = data.get(data.size() - 1)[labelIndex].toString();

        for (Object[] row : data) {
            if (!row[labelIndex].toString().equals(initialClassValue)) {
                return null;
            }
        }

        return initialClassValue;
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
        return 0.0;
    }

    // A recursive method that starts at the root node and moves down, printing everything in its way
    public static void printTree(DecisionTreeNode node, String indent) {
        if (node instanceof DecisionTreeLeafNode) {
            System.out.println(indent + "→ Class: " + ((DecisionTreeLeafNode) node).getLabelClass());
        } else if (node instanceof DecisionTree) {
            DecisionTree tree = (DecisionTree) node;
            System.out.println(indent + "Attribute[" + tree.getAttributeIndex() + "]");

            for (Map.Entry<String, DecisionTreeNode> split : tree.getSplits().entrySet()) {
                System.out.println(indent + "  ├─ " + split.getKey() + ":");
                printTree(split.getValue(), indent + "  │  ");
            }
        }
    }
}