package de.uni_trier.wi2.pki.postprocess;

import de.uni_trier.wi2.pki.tree.DecisionTree;
import de.uni_trier.wi2.pki.util.ID3Utils;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Contains util methods for performing a cross-validation.
 */
public class CrossValidator {

    /**
     * Performs a cross-validation with the specified dataset and the function to train the model.
     *
     * @param dataset        the complete dataset to use.
     * @param labelAttribute the label attribute.
     * @param trainFunction  the function to train the model with.
     * @param numFolds       the number of data folds.
     */
    public static DecisionTree performCrossValidation(List<Object[]> dataset, int labelAttribute, BiFunction<List<Object[]>, Integer, DecisionTree> trainFunction,
                                                      int numFolds) {
        // Prevents empty partitions, which would have led to NaN accuracy
        if (numFolds > dataset.size()) numFolds = dataset.size();

        // seed 0 makes the shuffle deterministic, allowing for consistent results
        Collections.shuffle(dataset, new Random(0));

        // Partitions dataset, into numFolds lists
        List<List<Object[]>> partitions = new ArrayList<>();
        for (int i = 0; i < numFolds; i++) {
            int from = i * dataset.size() / numFolds;
            int to   = (i + 1) * dataset.size() / numFolds;
            partitions.add(dataset.subList(from, to));
        }

        // On every fold, generate test and training data,
        // construct the decision tree with training data
        // and determine classification accuracy with the test data.
        double[] classificationAccuracies = new double[numFolds];
        DecisionTree[] decisionTrees = new DecisionTree[numFolds];
        for (int i = 0; i < numFolds; i++) {
            List<Object[]> testData = partitions.get(i);
            List<Object[]> trainingData = new ArrayList<>(dataset);
            trainingData.removeAll(testData);

            decisionTrees[i] = trainFunction.apply(trainingData, labelAttribute);
            classificationAccuracies[i] = ID3Utils.getClassificationAccuracy(decisionTrees[i], testData, labelAttribute);
        }

        // From all decision trees, determine the one with the best classification accuracy
        int bestIndex = 0;
        for (int i = 1; i < numFolds; i++) {
            if (classificationAccuracies[i] > classificationAccuracies[bestIndex]) bestIndex = i;
        }

        double avgAccuracy = Arrays.stream(classificationAccuracies).average().orElse(0.0);
        System.out.println("Average classification accuracy: " + avgAccuracy);
        System.out.println("Classification accuracy of best tree: " + classificationAccuracies[bestIndex]);
        return decisionTrees[bestIndex];
    }

}
