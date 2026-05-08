package de.uni_trier.wi2.pki.postprocess;

import de.uni_trier.wi2.pki.util.ID3Utils;
import de.uni_trier.wi2.pki.tree.DecisionTree;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class CrossValidatorJUnitTest {

    @Test
    void testCrossValidator() {
        // 1. Training data: Weather (Sunny/Rainy/Cloudy) + Temperature (Hot/Cold) -> Play (Yes/No)
        List<Object[]> data = new ArrayList<>();
        data.add(new Object[]{"Sunny",  "Hot",  "Yes"});
        data.add(new Object[]{"Sunny",  "Cold", "Yes"});
        data.add(new Object[]{"Rainy",  "Hot",  "Yes"});
        data.add(new Object[]{"Rainy",  "Cold", "No"});
        data.add(new Object[]{"Cloudy", "Hot",  "Yes"});
        data.add(new Object[]{"Cloudy", "Cold", "No"});
        data.add(new Object[]{"Sunny",  "Hot",  "Yes"});
        data.add(new Object[]{"Rainy",  "Hot",  "Yes"});
        data.add(new Object[]{"Cloudy", "Hot",  "Yes"});

        // Label index is 2
        final int LABEL_INDEX = 2;

        List<Object[]> testData = new ArrayList<>();
        testData.add(new Object[]{"Sunny",  "Hot",  "Yes"});
        testData.add(new Object[]{"Rainy",  "Cold", "No"});
        testData.add(new Object[]{"Cloudy", "Hot",  "Yes"});

        DecisionTree tree = CrossValidator.performCrossValidation(data, LABEL_INDEX, ID3Utils::createTree, 2);
        System.out.println("Tree from cross validation:");
        ID3Utils.printTree(tree);

        // Perform the same tests as above, but on the result tree
        assertNotNull(tree, "Best model should not be null");
        assertFalse(tree.isLeafNode(), "Root node should be a split node");
        assertEquals(new ArrayList<>(List.of("Yes", "No", "Yes")), tree.predictAll(testData));
        assertEquals(1.0, ID3Utils.getClassificationAccuracy(tree, testData, LABEL_INDEX));
    }
}