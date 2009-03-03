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

    public void testPerfomanceOneInstance() throws IOException {
        Loader loader = new Loader(new Constructor(Invoice.class));
        Yaml yaml = new Yaml(loader);
        int[] range = new int[] { 500, 1000, 2000 };
        System.out.println("One instance.");
        for (int number : range) {
            long time1 = System.currentTimeMillis();
            for (int i = 0; i < number; i++) {
                yaml.load(doc);
            }
            long time2 = System.currentTimeMillis();
            float duration = (time2 - time1) / (float) number;
            System.out.println("Duration for r=" + number + " was " + duration + " ms/load.");
            assertTrue("duration=" + duration, duration < 5);
        }
    }

    public void testPerfomanceManyInstances() throws IOException {
        int[] range = new int[] { 500, 1000, 2000 };
        System.out.println("Many instances.");
        for (int number : range) {
            long time1 = System.currentTimeMillis();
            for (int i = 0; i < number; i++) {
                Loader loader = new Loader(new Constructor(Invoice.class));
                Yaml yaml = new Yaml(loader);
                yaml.load(doc);
            }
            long time2 = System.currentTimeMillis();
            float duration = (time2 - time1) / (float) number;
            System.out.println("Duration for r=" + number + " was " + duration + " ms/load.");
            assertTrue("duration=" + duration, duration < 5);
        }
    }
}
