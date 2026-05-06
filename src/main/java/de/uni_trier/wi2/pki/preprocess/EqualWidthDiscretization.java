package de.uni_trier.wi2.pki.preprocess;

import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Class that holds logic for discretizing values.
 */
public class EqualWidthDiscretization extends BinningDiscretizer {

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

        // 1. Find the range (min to max)
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (Object[] row : examples) {
            double value = Double.parseDouble(row[attributeId].toString());
            if (value < min) min = value;
            if (value > max) max = value;
        }


        // 2. Calculate how wide each bin is
        double binWidth = (max - min) / numberOfBins;

        // 3. Assign each value to its bin
        for (Object[] row : examples) {
            double value = Double.parseDouble(row[attributeId].toString());

            // Calculate bin index. If value is max, put it in the last bin
            int binIndex = (int) ((value - min) / binWidth);
            if (binIndex >= numberOfBins) {
                binIndex = numberOfBins - 1;
            }

            row[attributeId] = "Bin" + binIndex;
        }
        return examples;
    }

}
