package org.yaml.snakeyaml.representer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class SafeRepresenterTest extends TestCase {

    public void testBinaryPattern() {
        Pattern pattern = SafeRepresenter.BINARY_PATTERN;
        assertFalse(pattern.matcher("\tAndrey\r\n").find());
        assertTrue(pattern.matcher("\u0005Andrey").find());
    }

    public void testFloat() {
        assertEquals("1.0E12", new Double("1e12").toString());
    }

    public void testClass() {
        HashMap data = new HashMap();
        Class clazz = data.getClass();
        assertTrue(Map.class.isInstance(data));
    }

}
