package de.uni_trier.wi2.pki.util;

import de.uni_trier.wi2.pki.tree.DecisionTree;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class ID3JUnitTest {

    @Test
    void testSimpleID3Logic() {
        // 1. Create minimal test data
        List<Object[]> data = new ArrayList<>();
        data.add(new Object[]{"Sunny", "Yes"});
        data.add(new Object[]{"Rainy", "No"});

        // 2. Build the tree (Label index is 1)
        DecisionTree tree = ID3Utils.createTree(data, 1);

        assertNotNull(tree, "Tree should not be null");

        // check if the root is not a leaf (it should be a split on "Weather")
        assertFalse(tree.isLeafNode(), "Root node should be a split node");

        // check if we have two splits (one for Sunny, one for Rainy)
        assertEquals(2, tree.getSplits().size(), "Tree should have 2 splits for the 2 weather types");

        //Visual check in console
        System.out.println("ID3 Small-Data Test-Tree:");
        ID3Utils.printTree(tree, "");
    }
}