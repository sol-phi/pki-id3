package de.uni_trier.wi2.pki.util;

import java.util.*;

/**
 * Contains methods that help with computing the entropy.
 */
public class EntropyUtils {




    /**
     * Calculates the information gain for all attributes
     *
     * @param matrix     Matrix of the training data (example data), e.g. ArrayList<String[]>
     * @param labelIndex the index of the attribute that contains the class. If the dataset is [Temperature,Weather,PlayFootball] and you want to predict playing
     *                   football, than labelIndex is 2
     * @return the information gain for each attribute
     */
    public static List<Double> calcInformationGain(Collection<Object[]> matrix, int labelIndex) {

    }

    /**
     * Calculates the information gain for the given attributes.
     *
     * @param attributeIndex the index of the attribute for which the entropy is to be calculated.
     * @param matrix         Matrix of the training data (example data), e.g. ArrayList<String[]>
     * @param labelIndex     the index of the attribute that contains the class. If the dataset is [Temperature,Weather,PlayFootball] and you want to predict playing
     *                       football, than labelIndex is 2
     * @return the information gain for a single attribute
     */
    public static double calcInformationGainForAttribute(int attributeIndex, Collection<Object[]> matrix, int labelIndex) {

    }

    /**
     * Calculates the entropy for the given parts of the classes. e.g. counts = [3,5] returns entropy 0.954434002924965.
     *
     * @param counts the list containing the sum for each label / value
     * @return the entropy for the given array
     */
    public static double calculateEntropy(long[] counts) {


        return entropy;
    }

    /**
     * Compute the rest entropy for a single attribute.
     *
     * @param attributeIndex the index of the attribute for which the entropy is to be calculated.
     * @param matrix         Matrix of the training data (example data), e.g. ArrayList<String[]>
     * @param labelIndex     the index of the attribute that contains the class. If the dataset is [Temperature,Weather,PlayFootball] and you want to predict playing
     *                       football, than labelIndex is 2
     * @return the rest entropy for a single attribute
     */
    public static double calculateRestEntropyForAttribute(int attributeIndex, Collection<Object[]> matrix, int labelIndex) {

        return restEntropy;
    }

    /**
     * Computes the entropy for an attribute value.
     *
     * @param attributeIndex the index of the attribute for which the entropy is to be calculated.
     * @param matrix         Matrix of the training data (example data), e.g. ArrayList<String[]>
     * @param value          attribute value (z.B. 'huge', 'small')
     * @param labelIndex     the index of the attribute that contains the class. If the dataset is [Temperature,Weather,PlayFootball] and you want to predict playing
     *                       football, than labelIndex is 2
     * @return the entropy an attribute value
     */
    public static double calculateEntropyForAttributeValue(int attributeIndex, Collection<Object[]> matrix, Object value, int labelIndex) {

        return calculateEntropy(counts);
    }


}