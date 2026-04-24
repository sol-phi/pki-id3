package de.uni_trier.wi2.pki.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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
        // One String[] is one line in the CSV file, one student. One String[] entry is one value for one trait of that student.
        // The List contains all lines, aka all students.

        List<String[]> parsedLines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(relativePath)))) {
            // If ignoreHeader is enabled, read and discard the first line, using it up already
            if (ignoreHeader) br.readLine();

            // Reads and assigns every line in the while head, then splits the long string into a String[], and adds to the list.
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(delimiter);
                parsedLines.add(values);
            }
        }

        return parsedLines;
    }

}
