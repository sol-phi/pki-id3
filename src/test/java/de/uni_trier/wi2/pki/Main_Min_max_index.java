package de.uni_trier.wi2.pki;

import de.uni_trier.wi2.pki.io.CSVReader;
import java.io.IOException;
import java.util.List;

public class Main_Min_max_index {
    public static void main(String[] args) {
        String path = "src/main/resources/student-mat.csv";
        int colIndex = 2; // e.g. Age is col 3 -> index 2

        try {
            List<String[]> data = CSVReader.readCsvToArray(path, ";", true);

            if (data.isEmpty()) {
                System.out.println("data is empty");
                return;
            }

            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;


            for (String[] row : data) {
                try {
                    int value = Integer.parseInt(row[colIndex]);

                    if (value < min) min = value;
                    if (value > max) max = value;
                } catch (NumberFormatException e) {
                    // if no value then:
                    System.err.println("cannot parse value: " + row[colIndex]);
                }
            }

            System.out.println("Col-Index " + colIndex);
            System.out.println("Minimum: " + min);
            System.out.println("Maximum: " + max);

        } catch (IOException e) {
            System.err.println("cannot read data: " + e.getMessage());
        }
    }
}