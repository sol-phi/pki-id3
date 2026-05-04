package de.uni_trier.wi2.pki.util;

import java.util.*;

/**
 * Contains methods that help with computing the entropy.
 */
public class EntropyUtilsTest {




    /**
     * Calculates the information gain for all attributes
     *
     * @param matrix     Matrix of the training data (example data), e.g. ArrayList<String[]>
     * @param labelIndex the index of the attribute that contains the class. If the dataset is [Temperature,Weather,PlayFootball] and you want to predict playing
     *                   football, than labelIndex is 2
     * @return the information gain for each attribute
     */
    public static List<Double> calcInformationGain(Collection<Object[]> matrix, int labelIndex) {
        List<Object[]> data = new ArrayList<>(matrix); // Required for data.get()

        // Just iterates through all attributes and collects the information gains
        List<Double> informationGain = new ArrayList<>();
        for (int i = 0; i < data.get(0).length; i++) {
            informationGain.add(calcInformationGainForAttribute(i, matrix, labelIndex));
        }

        return informationGain;
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
        if (attributeIndex == labelIndex) return 0.0; // Exclude the label attribute from calculation, because it would be the most efficient attribute otherwise

        // Runs through every row and tracks class value frequencies. A map is used here because that is simpler than doing it with a long[]
        Map<String, Long> absoluteClassFrequencies = new HashMap<>();
//        System.out.println("--------");
        for (Object[] row : matrix) {
            String label = row[labelIndex].toString();
            // If not present, create an entry set to 1, with the key classValue. Otherwise, overwrite classValue with classValue + 1
            absoluteClassFrequencies.merge(label, 1L, Long::sum);
        }

//        absoluteClassFrequencies.forEach((label, count) ->
//                System.out.println("Class " + label + ": " + count)
//        );

        // Converts the map into a long[], for calculateEntropy(). classValues are lost in the process,
        // but for the entropy calculation, it's irrelevant which classes the frequencies belong to
        long[] counts = absoluteClassFrequencies.values().stream()
                .mapToLong(Long::longValue)
                .toArray();

//        System.out.println(Arrays.toString(counts));

        // H(E) - R(A)
        return calculateEntropy(counts) - calculateRestEntropyForAttribute(attributeIndex, matrix, labelIndex);
    }

    /**
     * Calculates the entropy for the given parts of the classes. e.g. counts = [3,5] returns entropy 0.954434002924965.
     *
     * @param counts the list containing the sum for each label / value
     * @return the entropy for the given array
     */
    public static double calculateEntropy(long[] counts) {
        long totalClassCount = 0;
        for (long element : counts) totalClassCount += element;

        double entropy = 0;
        // P(Z[i]) * log[2](P(Z[i])) is subtracted from the entropy, one by one for every element, forming the negative sum.
        for (long element : counts) {
            if (element == 0) continue; // Prevents Math.log(0), which results in NaN
            double probability = (double) element / totalClassCount;
            entropy -= probability * (Math.log(probability) / Math.log(2));
        }
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
        // Split matrix into subsets by attributeIndex
        Map<Object, List<Object[]>> subsets = new HashMap<>();
        for (Object[] row : matrix) {
            Object attrValue = row[attributeIndex];
            subsets.computeIfAbsent(attrValue, k -> new ArrayList<>()).add(row);
        }

        // R(A) = weighted sum of entropies of subsets
        // P(A = w[i]) * H(E[i]) is added to the entropy, one by one for every element, forming the sum.
        double residualEntropy = 0;
        for (Map.Entry<Object, List<Object[]>> subset : subsets.entrySet()) {
            double probability = (double) subset.getValue().size() / matrix.size();
            residualEntropy += probability * calculateEntropyForAttributeValue(attributeIndex, matrix, subset.getKey(), labelIndex);

//            System.out.println("Value: " + subset.getKey() + " - " + subset.getValue().size() + " examples");
//            for (Object[] row : subset.getValue()) {
//                System.out.println("  " + Arrays.toString(row));
//            }
        }

        return residualEntropy;
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
        // Works just like calcInformationGainForAttribute(), but is applied only to the subset within matrix where row[attributeIndex].equals(value)

        // Runs through every row and tracks class value frequencies. A map is used here because that is simpler than doing it with a long[]
        Map<String, Long> absoluteClassFrequencies = new HashMap<>();
        for (Object[] row : matrix) {
            if (row[attributeIndex].equals(value)) {
                String classValue = row[labelIndex].toString();
                // If not present, create an entry set to 1, with the key classValue. Otherwise, overwrite classValue with classValue + 1
                absoluteClassFrequencies.merge(classValue, 1L, Long::sum);
            }
        }

        // Converts the map into a long array, for calculateEntropy(). classValues are lost in the process,
        // but for the entropy calculation, it's irrelevant which classes the frequencies belong to
        long[] counts = absoluteClassFrequencies.values().stream()
                .mapToLong(Long::longValue)
                .toArray();

        return calculateEntropy(counts);
    }


}