package de.uni_trier.wi2.pki.postprocess;

import de.uni_trier.wi2.pki.tree.DecisionTree;
import de.uni_trier.wi2.pki.util.ID3Utils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReducedErrorPrunerJUnitTest {

    @Test
    void testReducedErrorPruner() {
        // 1. Training examples: Weather (Sunny/Rainy/Cloudy) + Temperature (Hot/Cold) -> Play (Yes/No)
        List<Object[]> examples = new ArrayList<>();
        examples.add(new Object[]{"Sunny",  "Hot",  "Yes"});
        examples.add(new Object[]{"Sunny",  "Cold", "Yes"});
        examples.add(new Object[]{"Rainy",  "Hot",  "Yes"});
        examples.add(new Object[]{"Rainy",  "Cold", "No"});
        examples.add(new Object[]{"Cloudy", "Hot",  "Yes"});
        examples.add(new Object[]{"Cloudy", "Cold", "No"});
        examples.add(new Object[]{"Sunny",  "Hot",  "Yes"});
        examples.add(new Object[]{"Rainy",  "Hot",  "Yes"});
        examples.add(new Object[]{"Cloudy", "Hot",  "Yes"});

        // Label index is 2
        final int LABEL_INDEX = 2;

        List<Object[]> testData = new ArrayList<>();
        testData.add(new Object[]{"Sunny",  "Hot",  "Yes"});
        testData.add(new Object[]{"Rainy",  "Cold", "No"});
        testData.add(new Object[]{"Cloudy", "Hot",  "Yes"});

        // Build the decisionTree
        DecisionTree decisionTree = ID3Utils.createTree(examples, LABEL_INDEX);
        System.out.println("Before pruning:");
        ID3Utils.printTree(decisionTree);
        double accuracyBefore = ID3Utils.getClassificationAccuracy(decisionTree, testData, LABEL_INDEX);
        System.out.println("Accuracy before pruning: " + accuracyBefore);

        // Prune. The pruned decisionTree doesn't know about Sunny + Cold -> Yes
        DecisionTree bestPrunedDecisionTree = CrossValidator.performCrossValidation(
                examples,
                LABEL_INDEX,
                (trainData, labelIndex) -> {
                    // 20% are validation data
                    List<Object[]> constructionData = trainData.subList(0, (int)(trainData.size() * 0.8));
                    List<Object[]> validationData = trainData.subList((int)(trainData.size() * 0.8), trainData.size());

                    DecisionTree tree = ID3Utils.createTree(constructionData, labelIndex);
                    new ReducedErrorPruner().prune(tree, validationData, labelIndex);
                    return tree;
                },
                3
        );

        System.out.println("After pruning:");
        ID3Utils.printTree(bestPrunedDecisionTree);
        double accuracyAfter = ID3Utils.getClassificationAccuracy(decisionTree, testData, LABEL_INDEX);
        System.out.println("Accuracy after pruning: " + accuracyAfter);

        // Tree should still exist and be valid
        assertNotNull(decisionTree, "Pruned decisionTree should not be null");

        // Pruning should never decrease accuracy on the validation set
        assertTrue(accuracyAfter >= accuracyBefore,
                "Accuracy after pruning should be >= accuracy before pruning");

        // Pruned decisionTree should still predict correctly on clear-cut cases
        assertEquals(new ArrayList<>(List.of("Yes", "No", "Yes")), decisionTree.predictAll(testData));
    }
}