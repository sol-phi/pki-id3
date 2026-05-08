package de.uni_trier.wi2.pki;

import de.uni_trier.wi2.pki.io.CSVReader;
import de.uni_trier.wi2.pki.io.XMLWriter;
import de.uni_trier.wi2.pki.postprocess.CrossValidator;
import de.uni_trier.wi2.pki.postprocess.ReducedErrorPruner;
import de.uni_trier.wi2.pki.preprocess.BinningDiscretizer;
import de.uni_trier.wi2.pki.preprocess.EqualFrequencyDiscretization;
import de.uni_trier.wi2.pki.preprocess.EqualWidthDiscretization;
import de.uni_trier.wi2.pki.preprocess.KMeansDiscretizer;
import de.uni_trier.wi2.pki.tree.DecisionTree;
import de.uni_trier.wi2.pki.util.ID3Utils;

import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        // some constants
        final String FILE_NAME = "student-mat.csv";
        final int LABEL_ATTR_INDEX = 32;
        final int NUMBER_OF_BINS = 3;

        // parse CSV data
        List<String[]> parsedLines = null;
        try { // "target/classes/" corresponds to "src/main/resources/"
            parsedLines = CSVReader.readCsvToArray("target/classes/" + FILE_NAME, ";", true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 1. Convert List<String[]> to List<Object[]> as required by the discretizers
        List<Object[]> examples = new ArrayList<>(parsedLines);

        // The value for a specific attribute can be located through the attribute's index from examples.
        ArrayList<Boolean> attributesToDiscretize = new ArrayList<>();
        // Checks every attribute for whether it should be discretized by running through examples ↓, then →
        // Discretizability of an attribute is defined here as being numeric, and to prevent redundant binning, as having more unique values than bins.
        for (int i = 0; i < examples.get(0).length; i++) {
            Set<String> uniqueValues = new HashSet<>();
            boolean isNumeric = true;

            // Runs through the attribute column.
            // Since there might be a lot of rows, this approach minimizes runtime by aborting once the discretizability of the attribute is known
            for (Object[] row : examples) {
                String val = row[i].toString();
                // Attempts numeric conversion. If it fails, not discretizable, regardless of unique values
                try {
                    Double.parseDouble(val);
                } catch (NumberFormatException e) {
                    isNumeric = false;
                    break;
                }
                // To arrive here, the attribute must have been numeric
                // Checks for the amount of unique values recorded so far.
                // If greater than the number of bins, the second condition is fulfilled too -> discretizable.
                uniqueValues.add(val);
                if (uniqueValues.size() > NUMBER_OF_BINS) break;
            }

            boolean isDiscretizable = isNumeric && uniqueValues.size() > NUMBER_OF_BINS;
            attributesToDiscretize.add(isDiscretizable);
        }

        // 2. Pick a discretizer (e.g., EqualWidth)
        BinningDiscretizer discretizer = new EqualWidthDiscretization();

        // 3. Process all columns that are marked as continuous (true)
        for (int i = 0; i < attributesToDiscretize.size(); i++) {
            // ONLY discretize if it's a number AND NOT the label column
            if (attributesToDiscretize.get(i) && i != LABEL_ATTR_INDEX) {
                examples = discretizer.discretize(NUMBER_OF_BINS, examples, i);
            }
        }

        System.out.println("--- DECISION TREE --");
        DecisionTree bestDecisionTree = CrossValidator.performCrossValidation(
                examples,
                LABEL_ATTR_INDEX,
                ID3Utils::createTree,
                5
        );

        try {
            XMLWriter.writeXML("target/classes/decision-tree.xml", bestDecisionTree);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("--- PRUNED DECISION TREE --");
        DecisionTree bestPrunedDecisionTree = CrossValidator.performCrossValidation(
                examples,
                LABEL_ATTR_INDEX,
                (trainData, labelIndex) -> {
                    // 20% are validation data
                    List<Object[]> constructionData = trainData.subList(0, (int)(trainData.size() * 0.8));
                    List<Object[]> validationData = trainData.subList((int)(trainData.size() * 0.8), trainData.size());

                    DecisionTree tree = ID3Utils.createTree(constructionData, labelIndex);
                    new ReducedErrorPruner().prune(tree, validationData, labelIndex);
                    return tree;
                },
                5
        );

        try {
            XMLWriter.writeXML("target/classes/pruned-decision-tree.xml", bestPrunedDecisionTree);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
