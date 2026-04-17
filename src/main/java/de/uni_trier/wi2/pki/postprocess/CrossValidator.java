package de.uni_trier.wi2.pki.postprocess;

import de.uni_trier.wi2.pki.tree.DecisionTree;
import de.uni_trier.wi2.pki.util.ID3Utils;

import java.util.ArrayList;
import java.util.List;
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

        System.out.println("Classification accuracy of best model: " + accuracyBestModel);
        return bestModel;
    }

}
