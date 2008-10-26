/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.types;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.jvyaml.YAML;

/**
 * @see http://yaml.org/type/str.html
 */
public class StrTagTest extends TestCase {
    @SuppressWarnings("unchecked")
    private Map<String, String> getData(String data) {
        Map<String, String> nativeData = (Map<String, String>) YAML.load(data);
        return nativeData;
    }

    public void testString() throws IOException {
        Map<String, String> nativeData = getData("string: abcd");
        assertEquals("abcd", nativeData.get("string"));
    }

    public void testStringShorthand() throws IOException {
        Map<String, String> nativeData = getData("string: !!str abcd");
        assertEquals("abcd", nativeData.get("string"));
    }

    public void testStringTag() throws IOException {
        Map<String, String> nativeData = getData("string: !<tag:yaml.org,2002:str> abcd");
        assertEquals("abcd", nativeData.get("string"));
    }

    public void testStringIntOut() throws IOException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("number", "1");
        String output = YAML.dump(map);
        assertTrue(output.contains("number: !!str 1"));
    }

    public void testStringFloatOut() throws IOException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("number", "1.1");
        String output = YAML.dump(map);
        assertTrue(output.contains("number: !!str 1.1"));
    }

    public void testStringBoolOut() throws IOException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("number", "True");
        String output = YAML.dump(map);
        assertTrue(output.contains("number: !!str True"));
    }
}
