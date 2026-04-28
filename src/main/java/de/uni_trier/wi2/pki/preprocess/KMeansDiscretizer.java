package de.uni_trier.wi2.pki.preprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;

/**
 * Class that holds logic for discretizing values using K-means clustering.
 */
public class KMeansDiscretizer extends BinningDiscretizer {

    /**
     * Discretizes a collection of examples according to the number of bins (clusters) and the respective attribute ID.
     *
     * @param numberOfBins Specifies the number of numeric clusters that the data will be split up in.
     * @param examples     The list of examples to discretize.
     * @param attributeId  The ID of the attribute to discretize.
     * @return the list of discretized examples.
     */
    public List<Object[]> discretize(int numberOfBins, List<Object[]> examples, int attributeId) {
        if (examples == null || examples.isEmpty()) {
            return examples;
        }

        // 1. Extract values from the specified attribute column
        double[] values = new double[examples.size()];
        for (int i = 0; i < examples.size(); i++) {
            values[i] = Double.parseDouble(examples.get(i)[attributeId].toString());
        }

        // 2. Initialize centroids and prepare clustering
        double[] centroids = initializeCentroids(values, numberOfBins);
        int[] clusters = new int[values.length];
        double epsilon = 0.001; // Convergence threshold from notes
        boolean converged = false;

        // 3. Iterative clustering process
        while (!converged) {
            double[] oldCentroids = centroids.clone();

            // 4.2. Assign each value to the nearest centroid
            for (int i = 0; i < values.length; i++) {
                clusters[i] = findNearestCentroid(values[i], centroids);
            }

            // 4.3. Recalculate centroids based on the average of assigned values
            centroids = calculateNewCentroids(values, clusters, numberOfBins);

            // 4.4. Check for convergence (if centroids stopped moving significantly)
            double totalShift = 0;
            for (int i = 0; i < centroids.length; i++) {
                totalShift += Math.abs(centroids[i] - oldCentroids[i]);
            }
            if (totalShift < epsilon) {
                converged = true;
            }
        }

        // 5. Update the original examples with the cluster labels
        for (int i = 0; i < examples.size(); i++) {
            examples.get(i)[attributeId] = "Cluster" + clusters[i];
        }

        return examples;
    }


    /**
     * Initializes the centroids for the K-means algorithm.
     *
     * @param values The values to be clustered.
     * @param numberOfBins The number of clusters (centroids).
     * @return An array of initial centroids.
     */
    private double[] initializeCentroids(double[] values, int numberOfBins) {
        double[] centroids = new double[numberOfBins];
        // Distribute initial centroids evenly across the dataset as a starting point
        for (int i = 0; i < numberOfBins; i++) {
            int index = i * (values.length / numberOfBins);
            centroids[i] = values[index];
        }
        return centroids;
    }

    /**
     * Finds the nearest centroid for a given value.
     *
     * @param value The value to cluster.
     * @param centroids The array of centroid values.
     * @return The index of the nearest centroid.
     */
    private int findNearestCentroid(double value, double[] centroids) {
        int nearestIndex = 0;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < centroids.length; i++) {
            double distance = Math.abs(value - centroids[i]); // Euclidean distance in 1D
            if (distance < minDistance) {
                minDistance = distance;
                nearestIndex = i;
            }
        }
        return nearestIndex;
    }

    /**
     * Calculates new centroids based on current cluster assignments.
     *
     * @param values The original values.
     * @param clusters The current cluster assignments.
     * @param numberOfBins The number of clusters.
     * @return The recalculated centroids.
     */
    private double[] calculateNewCentroids(double[] values, int[] clusters, int numberOfBins) {
        double[] newCentroids = new double[numberOfBins];
        int[] counts = new int[numberOfBins];

        // Sum up all values assigned to each cluster
        for (int i = 0; i < values.length; i++) {
            int clusterIndex = clusters[i];
            newCentroids[clusterIndex] += values[i];
            counts[clusterIndex]++;
        }

        // Calculate the mean (average) for each cluster
        for (int i = 0; i < numberOfBins; i++) {
            if (counts[i] > 0) {
                newCentroids[i] /= counts[i];
            }
        }
        return newCentroids;
    }
}