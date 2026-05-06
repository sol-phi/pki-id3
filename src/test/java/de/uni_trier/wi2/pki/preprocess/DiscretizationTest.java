package de.uni_trier.wi2.pki.preprocess;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class DiscretizationTest {

    @Test
    void testEqualWidth() {
        EqualWidthDiscretization ew = new EqualWidthDiscretization();

        // create test data
        List<Object[]> data = new ArrayList<>();
        data.add(new Object[]{"10.0"}); // Min
        data.add(new Object[]{"20.0"}); // Max


        List<Object[]> result = ew.discretize(2, data, 0);

        // first element in Bin0 ??
        assertNotNull(result);
        assertEquals("Bin0", result.get(0)[0]);
    }
}