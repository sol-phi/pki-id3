package de.uni_trier.wi2.pki.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Is used to read in CSV files.
 */
public class CSVReader {

    /**
     * Read a CSV file and return a list of string arrays.
     *
     * @param relativePath the path where the CSV file is located (has to be relative path!)
     * @param delimiter    the delimiter symbol which is used in the CSV
     * @param ignoreHeader A boolean that indicates whether to ignore the header line or not, i.e., whether to include the first line into the list or not
     * @return A list that contains string arrays. Each string array stands for one parsed line of the CSV file
     * @throws IOException if something goes wrong. Exception should be handled at the calling function.
     */
    public static List<String[]> readCsvToArray(String relativePath, String delimiter, boolean ignoreHeader) throws IOException {
        List<String[]> parsedLines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(relativePath)))) {

            if (ignoreHeader) {
                br.readLine();
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(delimiter);

                // clean data and values
                cleanValues(values);

                parsedLines.add(values);
            }
        }

        return parsedLines;
    }

    /**
     * Removes surrounding quotes and whitespace from each string in the array.
     * * @param values The array of strings to be cleaned.
     */
    private static void cleanValues(String[] values) {
        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].replace("\"", "").trim();
        }
    }
}

