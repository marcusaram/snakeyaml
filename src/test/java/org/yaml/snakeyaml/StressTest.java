/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

import java.io.IOException;

import junit.framework.TestCase;

import org.yaml.snakeyaml.constructor.Constructor;

public class StressTest extends TestCase {
    String doc;

    public static void main(String[] args) throws IOException {
        int[] range = new int[] { 10, 20, 40, 50, 60, 100, 200, 500, 1000, 2000 };
        StressTest test = new StressTest();
        Yaml yamlSlow = new Yaml(new Loader(new Constructor(Invoice.class)));
        Yaml yamlFast = new Yaml(new Loader(new Constructor(Invoice.class)), new Dumper(
                new DumperOptions()), false);
        for (int i : range) {
            float slow = test.fire(yamlSlow, i);
            float fast = test.fire(yamlFast, i);
            System.out.println("Duration for number=" + i + " was (with RE/without RE): " + slow
                    + "/" + fast + " ms/load.");
        }
    }

    public StressTest() throws IOException {
        doc = Util.getLocalResource("specification/example2_27.yaml");
    }

    public void testPerfomance() throws IOException {
        Loader loader = new Loader(new Constructor(Invoice.class));
        Yaml yaml = new Yaml(loader);
        int[] range = new int[] { 10, 20, 40, 50, 60, 100, 200, 500 };
        float withRE = process(yaml, range);
        yaml = new Yaml(loader, new Dumper(new DumperOptions()), false);
        float withoutRE = process(yaml, range);
        System.out.println("with RE=" + withRE + "; withoutRE=" + withoutRE);
        assertTrue("with RE=" + withRE + "; withoutRE=" + withoutRE, withRE > withoutRE * 2);
    }

    private float process(Yaml yaml, int[] range) {
        float summ = 0;
        for (int r : range) {
            float duration = fire(yaml, r);
            summ += duration;
            // System.out.println("Duration for r=" + r + " was " + duration +
            // " ms/load.");
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
