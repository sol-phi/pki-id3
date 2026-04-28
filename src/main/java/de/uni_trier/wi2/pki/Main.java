package de.uni_trier.wi2.pki;

import de.uni_trier.wi2.pki.io.CSVReader;
import de.uni_trier.wi2.pki.io.XMLWriter;
import de.uni_trier.wi2.pki.postprocess.CrossValidator;
import de.uni_trier.wi2.pki.postprocess.ReducedErrorPruner;
import de.uni_trier.wi2.pki.preprocess.BinningDiscretizer;
import de.uni_trier.wi2.pki.preprocess.EqualWidthDiscretization;
import de.uni_trier.wi2.pki.tree.DecisionTree;
import de.uni_trier.wi2.pki.util.EntropyUtils;
import de.uni_trier.wi2.pki.util.ID3Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {

    public static void main(String[] args) {
        // some constants
        final String FILE_NAME = "student-mat.csv";
        final int LABEL_ATTR_INDEX = 32;

        // parse CSV data
        List<String[]> parsedLines = null;
        try { // "target/classes/" corresponds to "src/main/resources/"
            parsedLines = CSVReader.readCsvToArray("target/classes/" + FILE_NAME, ";", true);

//            // Debug print to peek the data just read
//            parsedLines.subList(0, 9).forEach(line -> System.out.printf(("%-13s| ".repeat(line.length) + "%n"), (Object[]) line));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // define data types of the dataset (matching the 33 columns of student-mat.csv)
        ArrayList<Boolean> attrIsContinuous = new ArrayList<>();
        attrIsContinuous.add(false); // 0: school ("GP")
        attrIsContinuous.add(false); // 1: sex ("F")
        attrIsContinuous.add(true);  // 2: age (18)
        attrIsContinuous.add(false); // 3: address ("U")
        attrIsContinuous.add(false); // 4: famsize ("GT3")
        attrIsContinuous.add(false); // 5: Pstatus ("A")
        attrIsContinuous.add(true);  // 6: Medu (4)
        attrIsContinuous.add(true);  // 7: Fedu (4)
        attrIsContinuous.add(false); // 8: Mjob ("at_home")
        attrIsContinuous.add(false); // 9: Fjob ("teacher")
        attrIsContinuous.add(false); // 10: reason ("course")
        attrIsContinuous.add(false); // 11: guardian ("mother")
        attrIsContinuous.add(true);  // 12: traveltime (2)
        attrIsContinuous.add(true);  // 13: studytime (2)
        attrIsContinuous.add(true);  // 14: failures (0)
        attrIsContinuous.add(false); // 15: schoolsup ("yes")
        attrIsContinuous.add(false); // 16: famsup ("no")
        attrIsContinuous.add(false); // 17: paid ("no")
        attrIsContinuous.add(false); // 18: activities ("no")
        attrIsContinuous.add(false); // 19: nursery ("yes")
        attrIsContinuous.add(false); // 20: higher ("yes")
        attrIsContinuous.add(false); // 21: internet ("no")
        attrIsContinuous.add(false); // 22: romantic ("no")
        attrIsContinuous.add(true);  // 23: famrel (4)
        attrIsContinuous.add(true);  // 24: freetime (3)
        attrIsContinuous.add(true);  // 25: goout (4)
        attrIsContinuous.add(true);  // 26: Dalc (1)
        attrIsContinuous.add(true);  // 27: Walc (1)
        attrIsContinuous.add(true);  // 28: health (3)
        attrIsContinuous.add(true);  // 29: absences (6)
        attrIsContinuous.add(true);  // 30: G1 ("5")
        attrIsContinuous.add(true);  // 31: G2 ("6")
        attrIsContinuous.add(true);  // 32: G3 (6)

        // 1. Convert List<String[]> to List<Object[]> as required by the discretizers
        List<Object[]> examples = new ArrayList<>(parsedLines);

        // 2. Pick a discretizer (e.g., EqualWidth)
        BinningDiscretizer discretizer = new EqualWidthDiscretization();

        // 3. Process all columns that are marked as continuous (true)
        for (int i = 0; i < attrIsContinuous.size(); i++) {
            // ONLY discretize if it's a number AND NOT the label column
            if (attrIsContinuous.get(i) && i != LABEL_ATTR_INDEX) {
                examples = discretizer.discretize(3, examples, i);
            }
        }



        // Train model, evaluate model, write XML, ...


        // --- TEST OUTPUT ---
        System.out.println("--- DISCRETIZATION TEST ---");
        System.out.println("Printing the first 5 students to check results:");

// We look at Index 2 (Age) and Index 29 (Absences) because they were 'true' (continuous)
        for (int i = 0; i < 100; i++) {
            Object[] row = examples.get(i);
            System.out.println("Student " + (i+1) + ": " +
                    "Age=" + row[2] + " | " +
                    "Absences=" + row[29] + " | " +
                    "G3 (Label)=" + row[32]);
        }
        System.out.println("---------------------------");
    }

}
