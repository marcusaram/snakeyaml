/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

import java.io.IOException;

import junit.framework.TestCase;

import org.yaml.snakeyaml.constructor.Constructor;

public class StressTest extends TestCase {
    String doc;

    public StressTest() throws IOException {
        doc = Util.getLocalResource("specification/example2_27.yaml");
    }

    public void testPerfomance() throws IOException {
        Loader loader = new Loader(new Constructor(Invoice.class));
        Yaml yaml = new Yaml(loader);
        int[] range = new int[] { 500, 1000 };
        process(yaml, range);
    }

    private float process(Yaml yaml, int[] range) {
        float summ = 0;
        for (int r : range) {
            float duration = fire(yaml, r);
            summ += duration;
            System.out.println("Duration for r=" + r + " was " + duration + " ms/load.");
            assertTrue("duration=" + duration, duration < 5);
        }
        return summ;
    }

    private float fire(Yaml yaml, int number) {
        long time1 = System.currentTimeMillis();
        for (int i = 0; i < number; i++) {
            Invoice invoice = (Invoice) yaml.load(doc);
            assertNotNull(invoice);
        }
        long time2 = System.currentTimeMillis();
        float duration = (time2 - time1) / (float) number;
        return duration;
    }
}
