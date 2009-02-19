/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

import java.util.List;

import junit.framework.TestCase;

public class NoRegularExpressionsTest extends TestCase {
    private Yaml yaml;

    @Override
    protected void setUp() throws Exception {
        yaml = new Yaml(new Loader(), new Dumper(new DumperOptions()), false);
    }

    public void testLoad() {
        String[] values = new String[] { "2", "2009-02-19", "false", "3.1416" };
        for (String string : values) {
            check(string);
        }
    }

    @SuppressWarnings("unchecked")
    public void testDump() {
        String[] values = new String[] { "2", "2009-02-19", "false", "3.1416" };
        String output = yaml.dump(values);
        assertEquals("[2, 2009-02-19, false, 3.1416]\n", output);
        List<String> list = (List<String>) yaml.load(output);
        assertEquals(4, list.size());
        for (int i = 0; i < values.length; i++) {
            String string = values[i];
            assertEquals(string, list.get(i));
        }
    }

    public void testNull() {
        assertEquals("null", yaml.load("null"));
    }

    private void check(String content) {
        assertEquals(content, yaml.load(content));
        Class<? extends Object> clazz = new Yaml().load(content).getClass();
        assertFalse("Must not be String: " + clazz, clazz == String.class);
        // dump
        assertEquals(content + "\n", yaml.dump(content));
    }
}
