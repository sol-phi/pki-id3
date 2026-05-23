package de.uni_trier.wi2.pki;

import de.uni_trier.wi2.pki.io.CSVReader;
import de.uni_trier.wi2.pki.postprocess.ReducedErrorPruner;
import de.uni_trier.wi2.pki.preprocess.BinningDiscretizer;
import de.uni_trier.wi2.pki.preprocess.EqualFrequencyDiscretization;
import de.uni_trier.wi2.pki.tree.DecisionTree;
import de.uni_trier.wi2.pki.util.ID3Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BinaryExperimentRunner {

    private static final String FILE_PATH = "target/classes/student-por.csv";
    private static final int LABEL_ATTR_INDEX = 32;
    private static final int TARGET_BINS = 5;

    public static void main(String[] args) {
        try {
            List<Object[]> examples = getPreparedBinaryData();

            BinningDiscretizer discretizer = new EqualFrequencyDiscretization();
            examples = applyCustomDiscretization(examples, discretizer, TARGET_BINS);

            int numFolds = 5;
            Collections.shuffle(examples, new java.util.Random(0));
            double[] rawAccs = new double[numFolds];
            double[] prunedAccs = new double[numFolds];

            for (int i = 0; i < numFolds; i++) {
                int from = i * examples.size() / numFolds;
                int to = (i + 1) * examples.size() / numFolds;

                List<Object[]> testData = examples.subList(from, to);
                List<Object[]> trainDataFull = new ArrayList<>(examples);
                trainDataFull.removeAll(testData);

                DecisionTree rawTree = ID3Utils.createTree(trainDataFull, LABEL_ATTR_INDEX);
                rawAccs[i] = ID3Utils.getClassificationAccuracy(rawTree, testData, LABEL_ATTR_INDEX);

                int splitIdx = (int) (trainDataFull.size() * 0.8);
                List<Object[]> buildData = trainDataFull.subList(0, splitIdx);
                List<Object[]> pruneValidationData = trainDataFull.subList(splitIdx, trainDataFull.size());

                DecisionTree treeToPrune = ID3Utils.createTree(buildData, LABEL_ATTR_INDEX);
                new ReducedErrorPruner().prune(treeToPrune, pruneValidationData, LABEL_ATTR_INDEX);
                prunedAccs[i] = ID3Utils.getClassificationAccuracy(treeToPrune, testData, LABEL_ATTR_INDEX);
            }

            double avgRaw = java.util.Arrays.stream(rawAccs).average().orElse(0.0);
            double avgPruned = java.util.Arrays.stream(prunedAccs).average().orElse(0.0);

            System.out.println("=====================================================");
            System.out.println("BINARY CONFIGURATION: EqualFrequency with 5 Bins");
            System.out.println("=====================================================");
            System.out.printf("Binary Accuracy (Raw Data):    %.2f%%%n", avgRaw * 100);
            System.out.printf("Binary Accuracy (Pruned Tree): %.2f%%%n", avgPruned * 100);
            System.out.println("=====================================================");

        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }

    private static List<Object[]> getPreparedBinaryData() throws IOException {
        List<String[]> parsedLines = CSVReader.readCsvToArray(FILE_PATH, ";", true);
        List<Object[]> examples = new ArrayList<>(parsedLines);

        for (Object[] row : examples) {
            try {
                String rawGrade = (String) row[LABEL_ATTR_INDEX];
                int grade = Integer.parseInt(rawGrade.trim());

                if (grade >= 10) {
                    row[LABEL_ATTR_INDEX] = "PASS";
                } else {
                    row[LABEL_ATTR_INDEX] = "FAIL";
                }
            } catch (NumberFormatException ignored) {}
        }
        return examples;
    }

    private static List<Object[]> applyCustomDiscretization(List<Object[]> examples, BinningDiscretizer discretizer, int bins) {
        List<Boolean> isCont = java.util.Arrays.asList(
                false, false, true, false, false, false, true, true, false, false, false, false, true, true, true,
                false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true
        );

        for (int i = 0; i < isCont.size(); i++) {
            if (isCont.get(i) && i != LABEL_ATTR_INDEX) {
                examples = discretizer.discretize(bins, examples, i);
            }
        }
        return examples;
    }
}