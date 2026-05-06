package de.uni_trier.wi2.pki.preprocess;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class DiscretizationJUnitTest {


    private List<Object[]> getTestData() {
        List<Object[]> data = new ArrayList<>();
        data.add(new Object[]{"10.0"});
        data.add(new Object[]{"20.0"});
        data.add(new Object[]{"30.0"});
        data.add(new Object[]{"40.0"});
        return data;
    }

    @Test
    void testEqualWidth() {
        BinningDiscretizer ew = new EqualWidthDiscretization();
        List<Object[]> result = ew.discretize(2, getTestData(), 0);

        // Width: Bin0 is 10-25, Bin1 is 25-40
        assertEquals("Bin0", result.get(0)[0]); // 10.0
        assertEquals("Bin1", result.get(3)[0]); // 40.0
    }

    @Test
    void testEqualFrequency() {
        BinningDiscretizer ef = new EqualFrequencyDiscretization();
        List<Object[]> result = ef.discretize(2, getTestData(), 0);

        // Frequency: 4 Elements on 2 Bins = 2 Elements pro Bin
        assertEquals("Bin0", result.get(0)[0]); // 10.0
        assertEquals("Bin0", result.get(1)[0]); // 20.0
        assertEquals("Bin1", result.get(2)[0]); // 30.0
    }

    @Test
    void testKMeans() {
        BinningDiscretizer km = new KMeansDiscretizer();
        List<Object[]> result = km.discretize(2, getTestData(), 0);

        // KMeans gruppiert nach Clustern.
        // Wir prüfen nur, ob überhaupt gelabelt wurde (Cluster0 oder Cluster1)
        assertNotNull(result.get(0)[0]);
        assertTrue(result.get(0)[0].toString().startsWith("Cluster"));
    }
}