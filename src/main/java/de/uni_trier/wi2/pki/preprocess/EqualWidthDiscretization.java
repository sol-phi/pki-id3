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
        if (examples.isEmpty()) return examples;

        // 1. Find the range (min to max)
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (Object[] row : examples) {
            double value = Double.parseDouble(row[attributeId].toString());
            if (value < min) min = value;
            if (value > max) max = value;
        }

        /*
         * Berechnung des Bin-Index (Beispiel):
         * ------------------------------------
         * Formel: index = (Eingabewert - Minimum) / Intervallbreite
         * * Beispiel mit Alter (min=15, max=21, bins=3):
         * 1. Breite = (22 - 15) / 3 = 2.0
         * 2. Für Wert 15: (15 - 15) / 2.0 = 0.0 -> (int)0 -> Bin0
         * 3. Für Wert 17: (17 - 15) / 2.0 = 1.0 -> (int)1 -> Bin1
         * 4. Für Wert 18: (18 - 15) / 2.0 = 1.5 -> (int)1 -> Bin1
         * 5. Für Wert 21: (21 - 15) / 2.0 = 3.0 -> (int)3 -> if(3>=3) -> Bin2
         */

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
