package de.uni_trier.wi2.pki;

import de.uni_trier.wi2.pki.io.CSVReader;
import java.io.IOException;
import java.util.List;

public class Main_Min_max_index {
    public static void main(String[] args) {
        String path = "src/main/resources/student-mat.csv"; // Dein Dateipfad
        int colIndex = 2; // Beispiel: Spalte "age" ist der 3. Wert (Index 2)

        try {
            // 1. Daten einlesen (Header ignorieren)
            List<String[]> data = CSVReader.readCsvToArray(path, ";", true);

            if (data.isEmpty()) {
                System.out.println("Die Datei ist leer.");
                return;
            }

            // 2. Initialisierung mit Extremwerten
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;

            // 3. Durch die Zeilen iterieren
            for (String[] row : data) {
                try {
                    // Wert extrahieren und parsen
                    int value = Integer.parseInt(row[colIndex]);

                    // Vergleich
                    if (value < min) min = value;
                    if (value > max) max = value;
                } catch (NumberFormatException e) {
                    // Falls mal ein Wert keine Zahl ist (Datenqualität)
                    System.err.println("Konnte Wert nicht parsen: " + row[colIndex]);
                }
            }

            // 4. Ergebnis ausgeben
            System.out.println("Analyse für Spalten-Index " + colIndex);
            System.out.println("Minimum: " + min);
            System.out.println("Maximum: " + max);

        } catch (IOException e) {
            System.err.println("Fehler beim Lesen der Datei: " + e.getMessage());
        }
    }
}