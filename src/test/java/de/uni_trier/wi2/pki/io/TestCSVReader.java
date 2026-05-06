package de.uni_trier.wi2.pki.io;

import java.util.List;

//Test class for output
class TestCSVReader {

    public static void main(String[] args) {
        String path = "src/main/resources/student-mat.csv";
        String delimiter = ";";

        try {
            List<String[]> data = CSVReader.readCsvToArray(path, delimiter, true);

            for (String[] row : data) {
                for (String value : row) {
                    System.out.print(value + "   |   ");
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
