package de.uni_trier.wi2.pki.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EntropyJUnitTest {

    @Test
    void entropyTest() {
        //  test: if 5 people said yes and 5 said no -> entropy = 1
        long[] counts = {5, 5};
        double ergebnis = EntropyUtils.calculateEntropy(counts);

        // result 1.0 ?
        assertEquals(1.0, ergebnis, 0.001);
    }
}