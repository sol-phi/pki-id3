package de.uni_trier.wi2.pki.postprocess;

import de.uni_trier.wi2.pki.tree.DecisionTree;
import de.uni_trier.wi2.pki.tree.DecisionTreeLeafNode;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;
import de.uni_trier.wi2.pki.util.ID3Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Prunes a trained decision tree in a post-pruning way.
 */
public class ReducedErrorPruner {

    /**
     * The starting classification accuracy.
     */
    private double classificationAccuracy;

    /**
     * The decision tree with the highest classification accuracy along the pruning procedure.
     */
    private DecisionTree decisionTree;

    /**
     * Prunes the given decision tree in-place.
     *
     * @param trainedDecisionTree The decision tree to prune.
     * @param validationExamples  the examples to validate the pruning with.
     * @param labelAttributeId    The label attribute.
     */
    public void prune(DecisionTree trainedDecisionTree, Collection<Object[]> validationExamples, int labelAttributeId) {
        // Store the root node reference to be able to getClassificationAccuracy() for the entire tree
        this.decisionTree = trainedDecisionTree;
        // For every run-through, the greatest classificationAccuracy is stored (with the tree left pruned as-is on that classificationAccuracy)
        this.classificationAccuracy = ID3Utils.getClassificationAccuracy(decisionTree, validationExamples, labelAttributeId);

        // Repeat recursive bottom-up pruning run-throughs until the classificationAccuracy does not increase anymore
        double previousAccuracy = -1.0;
        while (this.classificationAccuracy > previousAccuracy) {
            previousAccuracy = this.classificationAccuracy;
            pruneTree(trainedDecisionTree, validationExamples, labelAttributeId);
        }
    }

    /**
     * Recursive method for pruning the tree along certain paths.
     *
     * @param currentDecisionTreeNode the current tree node to start from.
     * @param validationExamples      the validation examples.
     * @param labelAttributeIndex     the label attribute index
     */
    private void pruneTree(DecisionTreeNode currentDecisionTreeNode, Collection<Object[]> validationExamples, int labelAttributeIndex) {
        // Pruning a leaf node would replace the leaf node with a leaf node, which is pointless.
        // Pruning the root node misses the point of a decision tree.
        if (currentDecisionTreeNode instanceof DecisionTreeLeafNode || currentDecisionTreeNode.getParent() == null) return;

        DecisionTree currentTree = (DecisionTree) currentDecisionTreeNode;

        // Post-order tree traversal - recurse into children first, so that pruning occurs bottom-up.
        // Top-down pruning is impossible, the children of the pruned node will be gone.
        for (DecisionTreeNode child : currentTree.getSplits().values()) {
            pruneTree(child, validationExamples, labelAttributeIndex);
        }

        // Find the parent and the value under which currentTree is stored
        DecisionTree parentTree = (DecisionTree) currentTree.getParent();
        String parentSplitValue = null;
        for (Map.Entry<String, DecisionTreeNode> entry : parentTree.getSplits().entrySet()) {
            if (entry.getValue() == currentTree) {
                parentSplitValue = entry.getKey();
                break;
            }
        }

        // Prunes the tree by overwriting the entry under parentSplitValue, which was currentTree, with the new leaf.
        String dominantClass = ID3Utils.getDominantClass(currentTree.getElements(), labelAttributeIndex);
        DecisionTreeLeafNode replacementLeaf = new DecisionTreeLeafNode(parentTree, currentTree.getElements(), dominantClass);
        parentTree.addSplit(parentSplitValue, replacementLeaf);

        // Measures newAccuracy. If greater than the previous classificationAccuracy, leave the pruned tree as is. If not, restore currentTree.
        double newAccuracy = ID3Utils.getClassificationAccuracy(decisionTree, validationExamples, labelAttributeIndex);
        if (newAccuracy > this.classificationAccuracy) {
            classificationAccuracy = newAccuracy;
        } else {
            parentTree.addSplit(parentSplitValue, currentTree);
        }

        // Now that currentDecisionTreeNode has been looked at, move back up in the recursion and attempt pruning on either a sibling node or the parent node.
    }

}
