package de.uni_trier.wi2.pki.preprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;

/**
 * Class that holds logic for discretizing values.
 */
public class EqualFrequencyDiscretization extends BinningDiscretizer {

    /**
     * Discretizes a collection of examples according to the number of bins and the respective attribute ID.
     *
     * @param numberOfBins Specifies the number of numeric ranges that the data will be split up in.
     * @param examples     The list of examples to discretize.
     * @param attributeId  The ID of the attribute to discretize.
     * @return the list of discretized examples.
     */
    @Override
    public List<Object[]> discretize(int numberOfBins, List<Object[]> examples, int attributeId) {
        if (examples == null || examples.isEmpty()) return examples;

        // 1. Collect all values and sort them to find frequency thresholds
        double[] sortedValues = new double[examples.size()];
        for (int i = 0; i < examples.size(); i++) {
            sortedValues[i] = Double.parseDouble(examples.get(i)[attributeId].toString());
        }
        Arrays.sort(sortedValues);

        // 2. Determine how many items fit in one bin.
        // We round up to make space for the last sortedValues.length % numberOfBins values,
        // rather than round down, which would cut off those remaining values.
        int itemsPerBin = (int) Math.ceil((double) sortedValues.length / numberOfBins);

        // 3. Assign bins based on position in the sorted distribution
        for (Object[] row : examples) {
            double value = Double.parseDouble(row[attributeId].toString());
            int assignedBin = 0;

            for (int b = 0; b < numberOfBins; b++) {
                // (b + 1) * itemsPerBin - 1 calculates the index of the right end of the intervals.
                // Since the last interval has that leeway of the sortedValues.length % numberOfBins remaining values, we adapt to that using min().
                // Then, since we have access to the sorted number line, we can just check which right interval end value falls under first
                // and assign the bin based on that.
                int thresholdIdx = Math.min((b + 1) * itemsPerBin - 1, sortedValues.length - 1);
                if (value <= sortedValues[thresholdIdx]) {
                    assignedBin = b;
                    break;
                }
            }
            row[attributeId] = "Bin" + assignedBin;
        }
        return examples;
    }

}
