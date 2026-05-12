package de.uni_trier.wi2.pki;

import de.uni_trier.wi2.pki.io.CSVReader;
import de.uni_trier.wi2.pki.postprocess.CrossValidator;
import de.uni_trier.wi2.pki.postprocess.ReducedErrorPruner;
import de.uni_trier.wi2.pki.preprocess.BinningDiscretizer;
import de.uni_trier.wi2.pki.preprocess.EqualFrequencyDiscretization;
import de.uni_trier.wi2.pki.preprocess.EqualWidthDiscretization;
import de.uni_trier.wi2.pki.preprocess.KMeansDiscretizer;
import de.uni_trier.wi2.pki.tree.DecisionTree;
import de.uni_trier.wi2.pki.util.ID3Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExperimentRunner {

    private static final String FILE_PATH = "target/classes/student-mat.csv";
    private static final int LABEL_ATTR_INDEX = 32;

    public static void main(String[] args) {
        try {
            // --- Toggle methods here to see specific outputs ---

 //           runMinMaxAnalysis(29); // Example: Column "age"

//            runDiscretizationDemo();

//            runId3TreeDemo();

  //          runCrossValidationDemo();

    //        runReducedErrorPrunerDemo();
            runFullOptimizationTest();

        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }

    private static void runMinMaxAnalysis(int colIndex) throws IOException {
        System.out.println("\n--- MIN-MAX ANALYSIS ---");
        List<String[]> data = CSVReader.readCsvToArray(FILE_PATH, ";", true);
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (String[] row : data) {
            try {
                int value = Integer.parseInt(row[colIndex]);
                if (value < min) min = value;
                if (value > max) max = value;
            } catch (NumberFormatException ignored) {}
        }
        System.out.println("Column Index: " + colIndex + " | Min: " + min + " | Max: " + max);
    }

    private static void runDiscretizationDemo() throws IOException {
        System.out.println("\n--- DISCRETIZATION TEST OUTPUT ---");
        List<Object[]> examples = getPreparedData();
        BinningDiscretizer discretizer = new EqualWidthDiscretization();

        examples = applyDiscretization(examples, discretizer);

        // Print first 5 rows for demonstration
        for (int i = 0; i < 5; i++) {
            System.out.println(Arrays.toString(examples.get(i)));
        }
    }

    private static void runId3TreeDemo() throws IOException {
        System.out.println("\n--- ID3 DECISION TREE ---");
        List<Object[]> examples = getPreparedData();
        BinningDiscretizer discretizer = new EqualWidthDiscretization();

        // 1. Discretize continuous values
        examples = applyDiscretization(examples, discretizer);

        // 2. Build and print the tree
        DecisionTree decisionTree = ID3Utils.createTree(examples, LABEL_ATTR_INDEX);
        ID3Utils.printTree(decisionTree);
    }

    private static void runCrossValidationDemo() throws IOException {
        System.out.println("\n--- CROSS VALIDATION ---");
        List<Object[]> examples = getPreparedData();
        BinningDiscretizer discretizer = new EqualWidthDiscretization();

        // 1. Discretize continuous values
        examples = applyDiscretization(examples, discretizer);

        // 2. Cross validate and print the best tree for comparison
        DecisionTree result = CrossValidator.performCrossValidation(examples, LABEL_ATTR_INDEX, ID3Utils::createTree, 5);
        ID3Utils.printTree(result);
    }

    private static void runReducedErrorPrunerDemo() throws IOException {
        System.out.println("\n--- REDUCED ERROR PRUNING ---");
        List<Object[]> examples = getPreparedData();
        BinningDiscretizer discretizer = new EqualWidthDiscretization();

        // 1. Discretize continuous values
        examples = applyDiscretization(examples, discretizer);

        // 2. Cross validate and print the best tree for comparison
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
        ID3Utils.printTree(bestPrunedDecisionTree);
    }


    // --- Helper methods to reduce redundancy ---

    private static List<Object[]> getPreparedData() throws IOException {
        List<String[]> parsedLines = CSVReader.readCsvToArray(FILE_PATH, ";", true);
        return new ArrayList<>(parsedLines);
    }

    private static List<Object[]> applyDiscretization(List<Object[]> examples, BinningDiscretizer discretizer) {
        ArrayList<Boolean> continuousMap = getContinuousAttributeMap();
        for (int i = 0; i < continuousMap.size(); i++) {
            // Discretize only if it is a numeric column and not the label
            if (continuousMap.get(i) && i != LABEL_ATTR_INDEX) {
                examples = discretizer.discretize(3, examples, i);
            }
        }
        return examples;
    }

    private static ArrayList<Boolean> getContinuousAttributeMap() {
        ArrayList<Boolean> isCont = new ArrayList<>();
        // Definition of the 33 columns (true = continuous, false = categorical)
        Boolean[] map = {false, false, true, false, false, false, true, true, false, false, false, false, true, true, true, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true};
        isCont.addAll(Arrays.asList(map));
        return isCont;
    }


    private static void runFullOptimizationTest() throws IOException {
        System.out.println("\n=========================================================");
        System.out.println("       ERGEBNISSE: DISKRETISIERUNG & ACCURACY");
        System.out.println("=========================================================");
        System.out.printf("%-20s | %-5s | %-15s%n", "Methode", "Bins", "Avg. Accuracy");
        System.out.println("---------------------------------------------------------");

        int[] binValues = {2, 3, 5, 10, 20};
        List<BinningDiscretizer> discretizers = Arrays.asList(
                new EqualWidthDiscretization(),
                new EqualFrequencyDiscretization(),
                new KMeansDiscretizer()
        );

        for (BinningDiscretizer discretizer : discretizers) {
            for (int bins : binValues) {
                List<Object[]> examples = getPreparedData();

                // 1. Diskretisierung anwenden
                examples = applyCustomDiscretization(examples, discretizer, bins);

                // 2. Cross-Validation manuell steuern für korrekte Statistik
                int numFolds = 5;
                Collections.shuffle(examples, new java.util.Random(0));
                double[] accuracies = new double[numFolds];

                for (int i = 0; i < numFolds; i++) {
                    int from = i * examples.size() / numFolds;
                    int to = (i + 1) * examples.size() / numFolds;

                    List<Object[]> testData = examples.subList(from, to);
                    List<Object[]> trainingData = new ArrayList<>(examples);
                    trainingData.removeAll(testData);

                    DecisionTree tree = ID3Utils.createTree(trainingData, LABEL_ATTR_INDEX);
                    accuracies[i] = ID3Utils.getClassificationAccuracy(tree, testData, LABEL_ATTR_INDEX);
                }

                double avgAcc = java.util.Arrays.stream(accuracies).average().orElse(0.0);

                // 3. Saubere Ausgabe
                String name = discretizer.getClass().getSimpleName().replace("Discretization", "");
                System.out.printf("%-20s | %-5d | %.2f%%%n", name, bins, avgAcc * 100);
            }
            System.out.println("---------------------------------------------------------");
        }
    }

    private static List<Object[]> applyCustomDiscretization(List<Object[]> examples, BinningDiscretizer discretizer, int bins) {
        ArrayList<Boolean> continuousMap = getContinuousAttributeMap();
        for (int i = 0; i < continuousMap.size(); i++) {
            // Diskretisiere nur, wenn es eine numerische Spalte und nicht das Label ist
            if (continuousMap.get(i) && i != LABEL_ATTR_INDEX) {
                examples = discretizer.discretize(bins, examples, i);
            }
        }
        return examples;
    }
}