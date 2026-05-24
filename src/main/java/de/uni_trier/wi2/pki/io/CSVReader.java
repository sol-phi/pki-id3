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
                String[] values = splitRespectingQuotes(line, delimiter.charAt(0), '"');

                // clean data and values
                cleanValues(values);

                parsedLines.add(values);
            }
        }

        return parsedLines;
    }

    /**
     * Removes surrounding quotes and whitespace from each string in the array.
     * @param values The array of strings to be cleaned.
     */
    private static void cleanValues(String[] values) {
        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].replace("\"", "").trim();
        }
    }

    /**
     * Converts a line representing a data point into an array with attribute values.
     * During that process, quotes are respected so that the delimiter character can appear within quoted values.
     * @param line The string to be parsed.
     * @param delimiter The character to split by.
     * @param quote The character that surrounds an attribute value.
     */
    private static String[] splitRespectingQuotes(String line, char delimiter, char quote) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        // Runs through the entire line character after character.
        // inQuotes keeps tracks of whether we are currently operating within one value, and if yes, ignores the delimiter character.
        // Empty values ("") receive their own array entry by nature.
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == quote) {
                inQuotes = !inQuotes;
            } else if (c == delimiter && !inQuotes) { // Split only on delimiters not inside quotes
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        // Last value is added
        fields.add(current.toString());

        return fields.toArray(new String[0]);
    }
}

