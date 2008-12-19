package org.yaml.snakeyaml.constructor;

import junit.framework.TestCase;

import org.yaml.snakeyaml.Yaml;

public class SafeConstructorTest extends TestCase {

    public void testConstructFloat() {
        Yaml yaml = new Yaml();
        assertEquals(3.1416, yaml.load("+3.1416"));
        assertEquals(Double.POSITIVE_INFINITY, yaml.load("+.inf"));
        assertEquals(Double.POSITIVE_INFINITY, yaml.load(".inf"));
        assertEquals(Double.NEGATIVE_INFINITY, yaml.load("-.inf"));
    }
}
